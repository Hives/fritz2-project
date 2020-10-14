import dev.fritz2.binding.RootStore
import dev.fritz2.binding.const
import dev.fritz2.binding.each
import dev.fritz2.binding.handledBy
import dev.fritz2.dom.html.render
import dev.fritz2.dom.mount
import dev.fritz2.dom.values

fun main() {
    val input = object : RootStore<String>("", id = "input") {
        val save = handleAndOffer<String> { input ->
            offer(input)
            input
        }
    }

    val todos = object : RootStore<List<String>>(emptyList(), id = "todos") {
        val addTodo = handle<String> { list, todo ->
            list + todo
        }
        val deleteTodo = handle<String> { list, todo ->
            list.minus(todo)
        }
    }

    input.save handledBy todos.addTodo

    render {
        div {
            ul {
                todos.data.each().render { todo ->
                    li {
                        div {
                            text(todo)
                        }
                        a {
                            href = const("#")
                            text("remove")
                            clicks.map { todo } handledBy todos.deleteTodo
                        }
                    }
                }.bind()
            }
            div("form-group") {
                input {
                    placeholder = const("What do you want to do?")
                    value = input.data

                    changes.values() handledBy input.update
                }
                button {
                    text("Save")
                    clicks handledBy input.save
                }
            }
        }
    }.mount("target")
}