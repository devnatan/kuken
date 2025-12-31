package gg.kuken.feature.setup.http

import gg.kuken.http.HttpModule
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

object SetupHttpModule : HttpModule() {
    override fun install(app: Application) {
        app.routing {
            setup()
        }
    }
}
