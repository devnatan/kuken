package gg.kuken.feature.blueprint

import gg.kuken.core.KukenException

open class BlueprintException : KukenException()

class BlueprintNotFoundException : BlueprintException()

class NoMatchingBlueprintSpecProviderException : BlueprintException()

class BlueprintSpecNotFound : BlueprintException()

class UnsupportedBlueprintSpecSource : BlueprintException()
