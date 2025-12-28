package gg.kuken.feature.rbac

@Suppress("ConstPropertyName")
object Permissions {
    const val Admin = "admin"

    const val ManageAccounts = "account.manage"
    const val ManageRoles = "role.manage"

    const val ManageInstances = "instance.manage"
    const val ReadInstance = "instance.read"

    const val ManageBlueprints = "blueprint.manage"
    const val ImportBlueprint = "blueprint.import"

    const val ManageUnits = "unit.manage"
    const val ListUnits = "unit.list"
}
