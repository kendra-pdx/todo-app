package me.enkode.todo.model

import java.util.UUID

case class TodoItem(id: UUID, name: String, createdAt: Long, completed: Boolean = false) {
  def this(name: String) = this(UUID.randomUUID(), name, System.currentTimeMillis(), false)
}

case class TodoList(id: UUID = UUID.randomUUID(), items: List[TodoItem] = Nil) {
  def withItem(item: TodoItem): TodoList = {
    this.copy(items = item :: this.items)
  }
}
