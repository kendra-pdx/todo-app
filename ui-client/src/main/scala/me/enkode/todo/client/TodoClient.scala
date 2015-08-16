package me.enkode.todo.client

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom._

import scala.scalajs.js.annotation.JSExport

@JSExport("TodoClient")
class TodoClient(mountNode: Node) {
  val TodoList = ReactComponentB[List[String]]("TodoList").render(props ⇒ {
    def createItem(itemText: String) = <.li(itemText)
    <.ul(props map createItem)
  }).build

  case class State(items: List[String], text: String)

  class Backend($: BackendScope[Unit, State]) {
    def onChange(e: ReactEventI) =
      $.modState(_.copy(text = e.target.value))

    def handleSubmit(e: ReactEventI) = {
      e.preventDefault()
      $.modState(s ⇒ State(s.items :+ s.text, ""))
    }
  }

  val TodoApp = ReactComponentB[Unit]("TodoApp")
    .initialState(State(Nil, ""))
    .backend(new Backend(_))
    .render((_, state, backend) ⇒
    <.div(
      <.h3("TODO"),
      TodoList(state.items),
      <.form(^.onSubmit ==> backend.handleSubmit,
        <.input(^.onChange ==> backend.onChange, ^.value := state.text),
        <.button("Add #", state.items.length + 1)
      )
    )
    ).buildU

  React.render(TodoApp(), mountNode)
}
