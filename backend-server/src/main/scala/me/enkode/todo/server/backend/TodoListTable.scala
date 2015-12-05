package me.enkode.todo.server.backend

import java.util.UUID

import com.websudos.phantom.CassandraTable
import com.websudos.phantom.column.JsonSetColumn
import com.websudos.phantom.connectors.Connector
import com.websudos.phantom.dsl._
import me.enkode.todo.model.{TodoList, TodoItem}

abstract class TodoListTable(override val tableName: String) extends CassandraTable[TodoListTable, TodoList] with Connector {
  implicit def space: KeySpace

  object id extends UUIDColumn(this) with PartitionKey[UUID]
  object items extends JsonSetColumn[TodoListTable, TodoList, TodoItem](this) {
    import upickle.default._
    override def fromJson(obj: String) = read[TodoItem](obj)
    override def toJson(obj: TodoItem) = write(obj)
  }

  override def fromRow(row: Row) = {
    TodoList(id(row), items(row).toList.sortBy(_.createdAt))
  }
}
