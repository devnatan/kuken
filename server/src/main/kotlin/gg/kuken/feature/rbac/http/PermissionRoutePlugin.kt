package gg.kuken.feature.rbac.http

import gg.kuken.feature.account.http.AccountPrincipal
import gg.kuken.feature.account.model.Account
import gg.kuken.feature.auth.http.exception.InvalidAccessTokenException
import gg.kuken.feature.rbac.exception.InsufficientPermissionsException
import gg.kuken.feature.rbac.model.PermissionCheckResult
import gg.kuken.feature.rbac.model.PermissionName
import gg.kuken.feature.rbac.service.PermissionService
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.auth.AuthenticationChecked
import io.ktor.server.auth.principal
import io.ktor.server.routing.Route
import io.ktor.server.routing.RouteSelector
import io.ktor.server.routing.RouteSelectorEvaluation
import io.ktor.server.routing.RoutingResolveContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun ApplicationCall.getCurrentAccount(): Account =
    principal<AccountPrincipal>()
        ?.account
        ?: throw InvalidAccessTokenException()

typealias GetResourceId = (ApplicationCall) -> Uuid?

class PermissionRoutePluginConfig {
    lateinit var permissions: List<PermissionName>
    var getResourceId: GetResourceId? = null
}

val PermissionRoutePlugin =
    createRouteScopedPlugin(
        name = "gg.kuken.feature.rbac.http.PermissionRoutePlugin",
        createConfiguration = ::PermissionRoutePluginConfig,
    ) {
        on(AuthenticationChecked) { call ->
            val permissionService by call.inject<PermissionService>()
            val accountId = call.getCurrentAccount().id
            val resourceId = pluginConfig.getResourceId?.invoke(call)

            val hasPermission: Boolean

            if (pluginConfig.permissions.size > 1) {
                hasPermission =
                    coroutineScope {
                        val deferredList =
                            pluginConfig.permissions.map { permission ->
                                async {
                                    permissionService.hasPermission(
                                        accountId = accountId,
                                        permissionName = permission,
                                        resourceId = resourceId,
                                    )
                                }
                            }

                        deferredList.awaitAll().any {
                            it // is allowed
                        }
                    }
            } else {
                hasPermission =
                    permissionService.hasPermission(
                        accountId = accountId,
                        permissionName = pluginConfig.permissions.first(),
                        resourceId = resourceId,
                    )
            }

            if (!hasPermission) {
                throw InsufficientPermissionsException()
            }
        }
    }

suspend fun ApplicationCall.checkPermissionWithDetails(
    permissionName: String,
    resourceId: Uuid? = null,
): PermissionCheckResult {
    val permissionService by inject<PermissionService>()
    val accountId = getCurrentAccount().id

    return permissionService.checkPermission(accountId, permissionName, resourceId)
}

inline fun Route.withPermission(
    vararg permission: PermissionName,
    noinline getResourceId: GetResourceId? = null,
    build: Route.() -> Unit,
) {
    val route =
        createChild(
            object : RouteSelector() {
                override suspend fun evaluate(
                    context: RoutingResolveContext,
                    segmentIndex: Int,
                ): RouteSelectorEvaluation = RouteSelectorEvaluation.Transparent
            },
        )

    route.install(PermissionRoutePlugin) {
        this.permissions = permission.toList()
        this.getResourceId = getResourceId
    }

    build(route)
}
