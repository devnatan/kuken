package gg.kuken.feature.instance.http.routes

import gg.kuken.feature.instance.InstanceFileService
import gg.kuken.feature.instance.http.InstanceRoutes
import gg.kuken.feature.instance.http.dto.UpdateFileRequest
import gg.kuken.http.util.receiveValid
import io.ktor.server.resources.patch
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject

fun Route.renameFile() {
    val instanceFileService by inject<InstanceFileService>()

    patch<InstanceRoutes.File> { parameters ->
        val payload = call.receiveValid<UpdateFileRequest>()
        val updates = mutableListOf<String>()
        when {
            payload.newName != null -> {
                instanceFileService.renameFile(
                    instanceId = parameters.instanceId,
                    filePath = parameters.path,
                    newName = payload.newName,
                )
                updates.add("fileName")
            }
        }

        call.respond(
            mapOf(
                "updates" to updates,
            ),
        )
    }
}
