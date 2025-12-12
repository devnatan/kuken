package gg.kuken.feature.instance

open class InstanceException : RuntimeException()

class InstanceNotFoundException : InstanceException()

class InstanceUnreachableRuntimeException : InstanceException()

class InvalidNetworkAssignmentException(
    override val message: String,
) : InstanceException()

class InstanceCreationException(
    override val message: String?,
    override val cause: Throwable?,
) : InstanceException()

class UnknownNetworkException(
    val network: String,
) : InstanceException()

class NetworkConnectionFailed(
    val network: String,
    override val cause: Throwable?,
) : InstanceException()
