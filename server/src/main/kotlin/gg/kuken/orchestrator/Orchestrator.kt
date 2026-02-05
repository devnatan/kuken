package gg.kuken.orchestrator

import gg.kuken.orchestrator.model.Node
import io.lettuce.core.RedisClient

class Orchestrator(
    val redisClient: RedisClient,
) {
    private val _connectedNodes = mutableMapOf<String, Node>()
    val connectedNodes get() = _connectedNodes.toList()

    fun onNodeConnected(node: Node) {
    }

    fun onNodeDisconnected(node: Node) {
    }
}
