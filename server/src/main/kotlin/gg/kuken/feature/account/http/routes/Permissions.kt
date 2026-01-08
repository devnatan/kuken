package gg.kuken.feature.account.http.routes

import gg.kuken.feature.account.http.AccountRoutes
import gg.kuken.feature.rbac.http.getCurrentAccount
import gg.kuken.feature.rbac.model.PermissionAction
import gg.kuken.feature.rbac.model.PermissionName
import gg.kuken.feature.rbac.model.PermissionPolicy
import gg.kuken.feature.rbac.model.PermissionSource
import gg.kuken.feature.rbac.model.ResourceType
import gg.kuken.feature.rbac.service.PermissionService
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

@Serializable
data class Response(
    val permission: PermissionName,
    val resource: ResourceType,
    val policy: PermissionPolicy?,
    val action: PermissionAction,
    val source: Source?,
) {
    @Serializable
    data class Source(
        val id: Uuid,
        val type: PermissionSource,
        val name: String,
    )
}

fun Route.permissions() {
    val permissionService by inject<PermissionService>()

    get<AccountRoutes.Permissions> {
        val accountId = call.getCurrentAccount().id
        val permissions = permissionService.getEffectivePermissionsWithSource(accountId)

        val response =
            permissions.map { (perm, result) ->
                Response(
                    permission = perm.name,
                    resource = perm.resource,
                    policy = result.policy,
                    action = perm.action,
                    source =
                        result.sourceId?.let {
                            Response.Source(
                                id = result.sourceId,
                                type = result.source!!,
                                name = result.sourceName!!,
                            )
                        },
                )
            }

        call.respond(response)
    }
}
