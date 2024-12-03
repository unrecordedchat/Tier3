package com.unrecorded

import com.unrecorded.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }

    @Test
    fun tasksCanBeFoundByPriority() = testApplication {
        application {
            module()
        }
        val response = client.get("/tasks/Medium")
        val body = response.bodyAsText()
        assertEquals(HttpStatusCode.OK, response.status)
        assertContains(body, "Mow the lawn")
        assertContains(body, "Paint the fence")
    }

    @Test
    fun invalidPriorityProduces400() = testApplication {
        application {
            module()
        }
        val response = client.get("/tasks/Invalid")
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun unusedPriorityProduces404() = testApplication {
        application {
            module()
        }
        val response = client.get("/tasks/Vital")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun newTasksCanBeAdded() = testApplication {
        application {
            module()
        }
        val response = client.post("/tasks") {
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody(listOf("name" to "swimming", "description" to "Go to the beach", "priority" to "Low").formUrlEncode())
        }
        assertEquals(HttpStatusCode.NoContent, response.status)
        val response2 = client.get("/tasks")
        assertEquals(HttpStatusCode.OK, response2.status)
        val body = response2.bodyAsText()
        assertContains(body, "swimming")
        assertContains(body, "Go to the beach")
    }
}
