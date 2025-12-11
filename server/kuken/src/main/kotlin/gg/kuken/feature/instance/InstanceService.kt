package gg.kuken.feature.instance

import gg.kuken.feature.instance.model.Instance
import kotlin.uuid.Uuid

interface InstanceService {
    suspend fun getInstance(instanceId: Uuid): Instance

    suspend fun runInstanceCommand(
        instanceId: Uuid,
        commandToRun: String,
    )

    suspend fun startInstance(instanceId: Uuid)

    suspend fun stopInstance(instanceId: Uuid)

    suspend fun deleteInstance(instanceId: Uuid)
}
