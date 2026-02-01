package gg.kuken.feature.blueprint

import gg.kuken.feature.blueprint.processor.BlueprintConverter
import gg.kuken.feature.blueprint.processor.ResolveBlueprintInputDefinitions
import gg.kuken.feature.blueprint.processor.ResolvedBlueprint
import kotlinx.serialization.ExperimentalSerializationApi
import org.pkl.core.ModuleSource
import org.pkl.core.resource.ResourceReader

@OptIn(ExperimentalSerializationApi::class)
class BlueprintProcessor(
    val blueprintConverter: BlueprintConverter,
) {
    fun process(
        input: String,
        readers: List<ResourceReader>,
    ): ResolvedBlueprint =
        blueprintConverter.convert(
            source = ModuleSource.text(input),
            readers = readers,
        )

    fun processPartial(
        input: String,
        readers: List<ResourceReader>,
    ): ResolveBlueprintInputDefinitions =
        blueprintConverter.convertPartial(
            source = ModuleSource.text(input),
            readers = readers,
        )
}
