package gg.kuken.feature.auth.http.routes

import gg.kuken.feature.auth.http.dto.VerifyResponse
import gg.kuken.feature.rbac.http.getCurrentAccount
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Route

fun Route.verify() {
    get<AuthRoutes.Verify> {
        val account = call.getCurrentAccount()
        call.respond(VerifyResponse(account))
    }
}
