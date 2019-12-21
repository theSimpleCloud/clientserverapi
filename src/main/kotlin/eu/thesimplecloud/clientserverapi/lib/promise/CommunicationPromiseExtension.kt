package eu.thesimplecloud.clientserverapi.lib.promise


fun ICommunicationPromise<Unit>.completeWhenAllCompleted(promises: List<ICommunicationPromise<*>>): ICommunicationPromise<Unit> {
    if (promises.isEmpty()) {
        trySuccess(Unit)
        return this
    }
    promises[0].combineAll(promises.drop(1)).addResultListener { this.trySuccess(Unit) }
    return this
}

fun Collection<ICommunicationPromise<*>>.combineAllPromises(): ICommunicationPromise<Unit> {
    return CommunicationPromise.combineAllToUnitPromise(this)
}

/**
 * Returns a new promise that will complete when the inner promise completes.
 * The new promise will complete with the same specifications.
 */
fun <T> ICommunicationPromise<out ICommunicationPromise<T>>.flatten(): ICommunicationPromise<T> {
    val newPromise = CommunicationPromise<T>(this.getTimeout())
    this.thenAccept { innerPromise ->
        innerPromise ?: newPromise.tryFailure(KotlinNullPointerException("Failed to flatten promise: Outer promise completed with null."))
        innerPromise?.let { newPromise.copyPromiseConfigurationOnComplete(it) }
    }
    return newPromise
}