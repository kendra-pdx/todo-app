package me.enkode.todo.server.backend

import java.util.UUID

import me.enkode.todo.model.{TodoItem, TodoList}

import scala.concurrent.{ExecutionContext, Future}

trait TodoDB {
  implicit def executionContext: ExecutionContext

  def getTodoList(listId: UUID): Future[Option[TodoList]] = Future {
    Some(TodoList(listId, Nil))
  }

  def saveTodoItem(todoItem: TodoItem): Future[TodoItem] = Future {
    todoItem
  }

  def addTodoItem(listId: UUID, todoItem: TodoItem): Future[Option[TodoList]] = {
    getTodoList(listId) map { _.map(tl ⇒ tl.copy(items = tl.items :+ todoItem)) }
  }
}

object TodoDB {
  class TodoDBImpl()(implicit val executionContext: ExecutionContext) extends TodoDB
  def apply()(implicit executionContext: ExecutionContext): TodoDB = new TodoDBImpl()
}
