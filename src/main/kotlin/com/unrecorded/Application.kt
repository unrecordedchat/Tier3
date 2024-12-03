package com.unrecorded

import com.unrecorded.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSockets()
    configureAdministration()
    configureSerialization()
    configureRouting()
}