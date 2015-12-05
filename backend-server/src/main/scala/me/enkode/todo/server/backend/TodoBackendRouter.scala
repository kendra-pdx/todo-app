package me.enkode.todo.server.backend

import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.Materializer
import me.enkode.todo.model._
import me.enkode.todo.server.common.{Logging, Router, μPickleMarshallingSupport}
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext

trait TodoBackendRouter extends Router with Directives with μPickleMarshallingSupport with Logging {
  def todoDB: TodoDB
  implicit def executionContext: ExecutionContext
  implicit def materializer: Materializer

  override def logger = LoggerFactory.getLogger(classOf[TodoBackendRouter])

  val getTodos: Route = (get & path("todo" / JavaUUID)) { listId ⇒
    rejectEmptyResponse {
      complete(todoDB.getTodoList(listId))
    }
  }

  val addTodo: Route = (post & path("todo" / JavaUUID) & entity(as[TodoItem])) { (listId, todoItem) ⇒
    info(s"adding a todo item: $todoItem", ("todoListId" → listId) :: Nil)
    complete(todoDB.addTodoItem(listId, todoItem))
  }

  override def routes = Seq(getTodos, addTodo)
}

object TodoBackendRouter {
  class TodoBackendRouterImpl(val todoDB: TodoDB)(implicit val executionContext: ExecutionContext, val materializer: Materializer) extends TodoBackendRouter
  def apply(todoDB: TodoDB)(implicit executionContext: ExecutionContext, materializer: Materializer): TodoBackendRouter = new TodoBackendRouterImpl(todoDB)
}