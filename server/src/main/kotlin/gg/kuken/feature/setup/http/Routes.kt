package gg.kuken.feature.setup.http

import gg.kuken.feature.setup.SetupService
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.koin.ktor.ext.inject
import kotlin.getValue

fun Route.setup() {
    val setupService by inject<SetupService>()

    get("/setup") {
        val state = setupService.state()
        if (state.completed) {
            call.response.status(HttpStatusCode.Locked)
            return@get
        }

        call.respond(state)
    }
}
