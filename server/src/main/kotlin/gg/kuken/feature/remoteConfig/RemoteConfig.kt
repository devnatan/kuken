package gg.kuken.feature.remoteConfig

@JvmInline
value class RemoteConfigKey(
    val name: String,
)

object RemoteConfig {
    val OrganizationName = RemoteConfigKey("org.name")
}
