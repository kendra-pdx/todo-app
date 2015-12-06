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
  import upickle.default._
  object items extends JsonSetColumn[TodoListTable, TodoList, TodoItem](this) {
    override def fromJson(obj: String) = read[TodoItem](obj)
    override def toJson(obj: TodoItem) = write(obj)
  }

  override def fromRow(row: Row) = {
    TodoList(id(row), items(row).toList.sortWith(_.createdAt >= _.createdAt))
  }
}
