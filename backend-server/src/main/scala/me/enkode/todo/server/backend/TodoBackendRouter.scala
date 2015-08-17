package me.enkode.todo.server.backend

import akka.http.scaladsl.server.{Route, Directives}
import me.enkode.todo.server.common.{μPickleMarshallingSupport, Router}
import me.enkode.todo.model._

trait TodoBackendRouter extends Router with Directives with μPickleMarshallingSupport {
  def todoDB: TodoDB

  val getTodos: Route = (get & path("todo" / JavaUUID)) { listId ⇒
    complete(TodoList(listId, Nil))
  }

  val updateTodo: Route = (put & path("todo" / JavaUUID / JavaUUID)) { (listId, todoId) ⇒
    complete(TodoList(listId, Nil))
  }

  val addTodo: Route = (post & path("todo" / JavaUUID)) { listId ⇒
    complete(TodoList(listId, Nil))
  }

  override def routes = Seq(getTodos, updateTodo)
}

object TodoBackendRouter {
  class TodoBackendRouterImpl(val todoDB: TodoDB) extends TodoBackendRouter
  def apply(todoDB: TodoDB): TodoBackendRouter = new TodoBackendRouterImpl(todoDB)
}