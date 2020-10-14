import dev.fritz2.binding.RootStore
import dev.fritz2.binding.const
import dev.fritz2.binding.each
import dev.fritz2.binding.handledBy
import dev.fritz2.dom.html.render
import dev.fritz2.dom.mount
import dev.fritz2.dom.values
import dev.fritz2.identification.uniqueId

fun main() {
    data class Todo(
        val id: String = uniqueId(),
        val text: String
    )

    val inputStore = object : RootStore<String>("", id = "input") {
        val save = handleAndOffer<String> { input ->
            offer(input)
            input
        }
    }

    val todosStore = object : RootStore<List<Todo>>(emptyList(), id = "todos") {
        val addTodo = handle<String> { list, input ->
            list + Todo(text = input)
        }
        val deleteTodo = handle<String> { list, id ->
            list.filterNot { it.id == id }
        }
    }

    inputStore.save handledBy todosStore.addTodo

    render {
        div {
            ul {
                todosStore.data.each().render { todo ->
                    li {
                        div {
                            text(todo.text)
                        }
                        button {
                            text("remove")
                            clicks.map { todo.id } handledBy todosStore.deleteTodo
                        }
                    }
                }.bind()
            }
            div("form-group") {
                input {
                    placeholder = const("What do you want to do?")
                    value = inputStore.data

                    changes.values() handledBy inputStore.update
                }
                button {
                    text("Save")
                    clicks handledBy inputStore.save
                }
            }
        }
    }.mount("target")
}