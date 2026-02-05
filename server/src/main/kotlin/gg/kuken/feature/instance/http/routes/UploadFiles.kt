package gg.kuken.feature.instance.http.routes

import gg.kuken.KukenConfig
import gg.kuken.feature.instance.InstanceFileService
import gg.kuken.feature.instance.http.InstanceRoutes
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.request.receiveMultipart
import io.ktor.server.resources.put
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyAndClose
import org.koin.ktor.ext.get
import org.koin.ktor.ext.inject
import kotlin.io.path.Path
import kotlin.io.path.pathString

fun Route.uploadFiles() {
    val instanceFileService by inject<InstanceFileService>()

    put<InstanceRoutes.File> { parameters ->
        val fileUploadLimit = get<KukenConfig>().http.fileUploadLimitBytes
        val payload = call.receiveMultipart(formFieldLimit = fileUploadLimit)
        payload.forEachPart { part ->
            try {
                if (part !is PartData.FileItem) {
                    return@forEachPart
                }

                val fileName = part.originalFileName as String

                val relativePath = Path(parameters.path).resolve(fileName)
                instanceFileService.touchFile(parameters.instanceId, relativePath.pathString)

                val absolutePath =
                    instanceFileService.resolve(
                        parameters.instanceId,
                        relativePath.pathString,
                    )

                part.provider().copyAndClose(absolutePath.toFile().writeChannel())
            } finally {
                part.dispose()
            }
        }

        call.respond(HttpStatusCode.Created)
    }
}
