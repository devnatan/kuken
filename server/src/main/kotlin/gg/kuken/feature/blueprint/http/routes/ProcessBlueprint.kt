package gg.kuken.feature.blueprint.http.routes

import gg.kuken.feature.blueprint.BlueprintService
import gg.kuken.feature.blueprint.http.BlueprintRoutes
import gg.kuken.feature.blueprint.http.dto.ProcessBlueprintRequest
import gg.kuken.feature.blueprint.processor.BlueprintResolutionContext
import gg.kuken.http.HttpError
import gg.kuken.http.util.ValidationErrorResponse
import gg.kuken.http.util.ValidationException
import gg.kuken.http.util.receiveValid
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject
import org.pkl.core.PklException

fun Route.processBlueprint() {
    val blueprintService by inject<BlueprintService>()

    post<BlueprintRoutes.Process> { params ->
        val payload = call.receiveValid<ProcessBlueprintRequest>()
        val blueprint =
            try {
                blueprintService.processBlueprint(
                    blueprintId = params.blueprintId,
                    resolutionContext =
                        BlueprintResolutionContext(
                            inputs = payload.inputs,
                            env = payload.env,
                        ),
                )
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
