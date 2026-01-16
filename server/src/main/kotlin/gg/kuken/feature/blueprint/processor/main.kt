@file:Suppress("ktlint:standard:filename")

package gg.kuken.feature.blueprint.processor

import org.pkl.core.ModuleSource

fun main() {
    BlueprintConverter().use { converter ->
        val blueprint =
            converter.convert(
                ModuleSource.path("D:\\IdeaProjects\\kuken\\blueprints\\blueprints\\games\\hytale\\hytale.pkl"),
            )

        println("=== Blueprint ===")
        println(blueprint)
        println("===")
    }
}
