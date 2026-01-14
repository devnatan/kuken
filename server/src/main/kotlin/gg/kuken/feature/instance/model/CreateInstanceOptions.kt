package gg.kuken.feature.instance.model

data class CreateInstanceOptions(
    val address: HostPort,
    val image: String?,
    val inputs: Map<String, String>,
    val env: Map<String, String>,
)
