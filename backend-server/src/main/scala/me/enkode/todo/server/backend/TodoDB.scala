package me.enkode.todo.server.backend

import java.util.UUID

import com.websudos.phantom.dsl.{context ⇒ _, _}
import me.enkode.todo.model.{TodoItem, TodoList}
import me.enkode.todo.server.common.Logging
import me.enkode.trace.Trace
import org.slf4j.LoggerFactory

import scala.concurrent._, duration._

trait TodoDB {
  def getTodoList(listId: UUID)(implicit trace: Trace, ec: ExecutionContext): Future[Option[TodoList]]

  def addTodoItem(listId: UUID, todoItem: TodoItem)(implicit trace: Trace, ec: ExecutionContext): Future[TodoList]
}

class CassandraTodoDB(todoListTable: TodoListTable) extends TodoDB with Logging {
  import todoListTable.session
  import todoListTable.space

  override def logger = LoggerFactory.getLogger(classOf[CassandraTodoDB])

  override def getTodoList(listId: UUID)(implicit trace: Trace, ec: ExecutionContext): Future[Option[TodoList]] = {
    trace.log.withTraceMdc {
      todoListTable.select.where(_.id eqs listId).one()
    }
  }

  override def addTodoItem(listId: UUID, todoItem: TodoItem)(implicit trace: Trace, ec: ExecutionContext) = {
    getTodoList(listId) map { _.fold(TodoList(listId, todoItem :: Nil)) { tl ⇒
      tl.copy(items = tl.items :+ todoItem)
    }} flatMap { todoList ⇒
      trace.log.info(s"saving: $todoList")
      trace.log.withTraceMdc {
        todoListTable.insert
          .value(_.id, todoList.id)
          .value(_.items, todoList.items.toSet)
          .future() andThen { case result ⇒
          trace.log.info(s"addTodoItem result: $result")
        } map { _ ⇒ todoList }
      }
    }
  }

  def init()(implicit ec: ExecutionContext): Future[Unit] = {
    Await.ready(todoListTable.create.future(), 60.seconds) map { _ ⇒ }
  }
}
