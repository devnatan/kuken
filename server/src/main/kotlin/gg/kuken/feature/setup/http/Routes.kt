package gg.kuken.feature.setup.http

import gg.kuken.feature.setup.SetupService
import gg.kuken.feature.setup.http.dto.SetupRequest
import gg.kuken.http.util.receiveValidating
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import jakarta.validation.Validator
import org.koin.ktor.ext.inject

fun Route.setup() {
    val setupService by inject<SetupService>()
    val validator by inject<Validator>()

    get("/setup") {
        val state = setupService.state()
        if (state.completed) {
            call.response.status(HttpStatusCode.Locked)
            return@get
        }

        call.respond(state)
    }

    post("/setup") {
        val currentState = setupService.state()
        if (currentState.completed) {
            call.response.status(HttpStatusCode.Locked)
            return@post
        }

        val payload = call.receiveValidating<SetupRequest>(validator)
        val state = setupService.tryComplete(payload)

        call.respond(state)
    }
}
