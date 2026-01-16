package gg.kuken.feature.blueprint

import gg.kuken.feature.blueprint.processor.BlueprintConverter
import gg.kuken.feature.blueprint.processor.ResolvedBlueprint
import kotlinx.serialization.ExperimentalSerializationApi
import org.pkl.core.ModuleSource

@OptIn(ExperimentalSerializationApi::class)
class BlueprintProcessor {
    val converter = BlueprintConverter()

    fun process(
        input: String,
        source: String,
    ): ResolvedBlueprint = converter.convert(ModuleSource.text(input))
}
