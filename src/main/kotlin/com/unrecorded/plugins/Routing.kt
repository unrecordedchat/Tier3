package com.unrecorded.plugins

import com.unrecorded.database.DBA
import com.unrecorded.model.Priority
import com.unrecorded.model.Task
import com.unrecorded.model.TaskRepository
import com.unrecorded.model.tasksAsTable
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receiveParameters
import io.ktor.server.request.receiveText
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.hibernate.Transaction

/**
 * Configures the routing for the Ktor application.
 */
fun Application.configureRouting() {
    routing {
        post("/echo") {
            try {
                val content = call.receiveText()
                if (content.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Content is required!")
                    return@post
                }
                val session = DBA.getSessionFactory().openSession()
                var transaction: Transaction? = null
                var echoed: List<EEcho>? = null
                try {
                    transaction = session.beginTransaction();
                    val echo = EEcho().apply {
                        this.content = content
                    }
                    session.persist(echo);
                    transaction.commit();
                    echoed = session.createQuery("SELECT a FROM EEcho a", EEcho::class.java).getResultList()
                } catch (ex: Exception) {
                    print("ERROR!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11")
                    ex.printStackTrace()
                    transaction?.rollback()
                    call.respond(HttpStatusCode.InternalServerError, "Error occurred while processing the request")
                    return@post
                } finally {
                    session.close()
                }
                call.respond(HttpStatusCode.Created, echoed ?: emptyList<EEcho>())
            } catch (_: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request format")
            }
        }
        get("/tasks") {
            val tasks = TaskRepository.allTasks()
            call.respondText(
                contentType = ContentType.parse("text/html"),
                text = tasks.tasksAsTable()
            )
        }
        get("/tasks/{priority}") {
            val priorityAsText = call.parameters["priority"]
            if (priorityAsText == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            try {
                val priority = Priority.valueOf(priorityAsText)
                val tasks = TaskRepository.tasksByPriority(priority)
                if (tasks.isEmpty()) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }
                call.respondText(contentType = ContentType.parse("text/html"), text = tasks.tasksAsTable())
            } catch (_: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }
        post("/tasks") {
            val formContent = call.receiveParameters()
            val params = Triple(formContent["name"] ?: "", formContent["description"] ?: "", formContent["priority"] ?: "")
            if (params.toList().any { it.isEmpty() }) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            try {
                val priority = Priority.valueOf(params.third)
                TaskRepository.addTask(Task(params.first, params.second, priority))
                call.respond(HttpStatusCode.NoContent)
            } catch (_: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest)
            } catch (_: IllegalStateException) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}
