package gg.kuken.feature.instance

open class InstanceException : RuntimeException()

class InstanceNotFoundException : InstanceException()

class InstanceUnreachableRuntimeException : InstanceException()
