package me.enkode.todo.server.backend

import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import me.enkode.todo.model._
import me.enkode.todo.server.common.{Directives, Logging, Router, μPickleMarshallingSupport}
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext

trait TodoBackendRouter extends Router with Directives with μPickleMarshallingSupport with Logging {
  def todoDB: TodoDB
  implicit def executionContext: ExecutionContext
  implicit def materializer: Materializer

  override val logger = LoggerFactory.getLogger(classOf[TodoBackendRouter])

  val getTodos: Route = (get & path("todo" / JavaUUID)) { listId ⇒
    trace("get todos") { implicit trace ⇒
      trace.log.debug("getting todos")
      rejectEmptyResponse {
        tracedComplete(todoDB.getTodoList(listId))
      }
    }
  }

  val addTodo: Route = (post & path("todo" / JavaUUID) & entity(as[TodoItem])) { (listId, todoItem) ⇒
    trace("add todo") { implicit trace ⇒
      trace.log.info(s"adding a todo item: $todoItem", ("todoListId" → listId) :: Nil)
      tracedComplete(todoDB.addTodoItem(listId, todoItem))
    }
  }

  override def routes = Seq(getTodos, addTodo)
}

object TodoBackendRouter {
  class TodoBackendRouterImpl(val todoDB: TodoDB)(implicit val executionContext: ExecutionContext, val materializer: Materializer) extends TodoBackendRouter
  def apply(todoDB: TodoDB)(implicit executionContext: ExecutionContext, materializer: Materializer): TodoBackendRouter = new TodoBackendRouterImpl(todoDB)
}