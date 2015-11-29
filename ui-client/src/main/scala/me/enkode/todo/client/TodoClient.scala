package me.enkode.todo.client

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import me.enkode.todo.model.{TodoItem, TodoList}
import org.scalajs.dom._
import org.scalajs.dom.ext.Ajax

import scala.scalajs.js.annotation.JSExport


@JSExport("TodoClient")
class TodoClient(mountNode: Node) {
  val TodoListComponent = ReactComponentB[TodoList]("TodoList").render_P(props ⇒ {
    def createItem(todoItem: TodoItem) = <.li(todoItem.name)
    <.ul(props.items map createItem)
  }).build

  case class State(todoList: TodoList, text: String)

  class Backend($: BackendScope[Unit, State]) {
    def onChange(e: ReactEventI) =
      $.modState(_.copy(text = e.target.value))

    def handleSubmit(e: ReactEventI) = {
      e.preventDefault()
      $ modState { s ⇒
        val todoList = s.todoList.withItem(new TodoItem(s.text))
        State(todoList, s.text)
      }
    }

    def render(state: State) = {
      <.div(
        <.h3("TODO"),
        TodoListComponent(state.todoList),
        <.form(^.onSubmit ==> handleSubmit,
          <.input(^.onChange ==> onChange, ^.value := state.text),
          <.button("Add #", state.todoList.items.size + 1)
        )
      )

    }
  }

  val TodoApp = ReactComponentB[Unit]("TodoApp")
    .initialState(State(new TodoList, ""))
    .renderBackend[Backend]
    .componentDidMount(scope => Callback {
      import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
      Ajax.get(s"/todo/${scope.state.todoList.id}") map { xhr =>
        import upickle.default._
        read[TodoList](xhr.responseText)
      } foreach { todoList =>
        scope.modState(_.copy(todoList = todoList)).runNow()
      }
    })
    .buildU

  ReactDOM.render(TodoApp(), mountNode)
}
