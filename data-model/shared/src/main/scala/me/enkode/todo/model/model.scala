package me.enkode.todo.model

import java.util.UUID

case class TodoItem(id: UUID, name: String, completed: Boolean = false) {
  def this(name: String) = this(UUID.randomUUID(), name, false)
}

case class TodoList(id: UUID, items: List[TodoItem]) {
  def this() = this(UUID.randomUUID(), Nil)

  def withItem(item: TodoItem): TodoList = {
    this.copy(items = item :: this.items)
  }
}
