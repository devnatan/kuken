package gg.kuken.feature.blueprint.http.routes

import gg.kuken.feature.blueprint.BlueprintService
import gg.kuken.feature.blueprint.BlueprintSpecSource
import gg.kuken.feature.blueprint.http.BlueprintRoutes
import gg.kuken.http.HttpError
import gg.kuken.http.util.ValidationErrorResponse
import gg.kuken.http.util.ValidationException
import gg.kuken.http.util.receiveValid
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.Route
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import org.pkl.core.PklException

@Serializable
data class ImportBlueprintRequest(
    val source: BlueprintSpecSource,
)

fun Route.importBlueprint() {
    val blueprintService by inject<BlueprintService>()

    post<BlueprintRoutes.Import> {
        val payload = call.receiveValid<ImportBlueprintRequest>()
        val blueprint =
            try {
                blueprintService.importBlueprint(payload.source)
            } catch (e: PklException) {
                throw ValidationException(
                    data =
                        ValidationErrorResponse(
                            code = HttpError.BlueprintParse.code,
                            message = e.message.orEmpty(),
                            details = setOf(),
                        ),
                )
            }

        call.respond(blueprint)
    }
}
