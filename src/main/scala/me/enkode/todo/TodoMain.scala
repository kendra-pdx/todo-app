package me.enkode.todo

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import me.enkode.todo.server.backend.{TodoListTable, CassandraTodoDB, TodoDB, TodoBackendRouter}
import me.enkode.todo.server.common.{Logging, Router}
import me.enkode.todo.server.ui.UiServerRouter
import org.slf4j.LoggerFactory

class TodoMain extends Router with Directives with Logging {
  val logger = LoggerFactory.getLogger(classOf[TodoMain])

  lazy val about: Route = (get & path("about")) { complete("todo: v0")}

  lazy val default: Route = (get & pathEndOrSingleSlash) { redirect("/ui/index.html", StatusCodes.PermanentRedirect) }

  lazy val routes: Seq[Route] = Seq(
    about,
    default
  )

  def run(): Unit = {
    implicit val actorSystem = ActorSystem("Todo")
    implicit val actorMaterializer = ActorMaterializer()

    import actorSystem.dispatcher

    val (bindHost, bindPort) = {
      val config = actorSystem.settings.config.getConfig("todo.bind")
      (config.getString("host"), config.getInt("port"))
    }

    val todoDB = {
      import com.websudos.phantom.connectors.ContactPoint
      import com.websudos.phantom.dsl.{context ⇒ _, _}

      val dbConfig = actorSystem.settings.config.getConfig("todo.db")

      implicit val keySpace = KeySpace(dbConfig.getString("keySpaceName"))
      val contactPoint = ContactPoint.local.keySpace(keySpace.name)

      object TodoListTable extends TodoListTable(dbConfig.getString("todoListTableName")) with contactPoint.Connector
      new CassandraTodoDB(TodoListTable) {
        init()
      }
    }

    val routers: Seq[Router] = Seq(
      UiServerRouter(),
      TodoBackendRouter(todoDB),
      this
    )

    def logRoute(route: Route): Route = { ctx: RequestContext ⇒
      val start = System.currentTimeMillis()
      route(ctx) map {
        case result@RouteResult.Complete(response) ⇒
          val elapsed = System.currentTimeMillis() - start
          info("completed", Seq(
            "method" → ctx.request.method.name,
            "path" → ctx.request.uri.path,
            "time" → elapsed,
            "response" → response.status.value
          ))
          result

        case result@RouteResult.Rejected(rejections) ⇒
          val elapsed = System.currentTimeMillis() - start
          debug("rejected", Seq(
            "method" → ctx.request.method.name,
            "path" → ctx.request.uri.path,
            "time" → elapsed
          ))
          result
      }
    }

    val route = routers.map(_.routes).reduce(_ ++ _).reduce((rs, r) ⇒ rs ~ logRoute(r))

    val binding = Http().bindAndHandle(route, bindHost, bindPort)

    binding map { bound ⇒
      info("todo server bound", ("host" → bindHost) :: ("port", bindPort) :: Nil)
    }

    actorSystem registerOnTermination {
      binding.map(_.unbind())
    }
  }
}

object TodoMain extends TodoMain with App {
  run()
}
