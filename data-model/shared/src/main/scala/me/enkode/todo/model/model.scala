package me.enkode.todo.model

import java.util.UUID

case class TodoItem(id: UUID, name: String, completed: Boolean = false)
case class TodoList(id: UUID, items: Seq[TodoItem])
