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
fun <T : Any> ICommunicationPromise<out ICommunicationPromise<T>>.flatten(additionalTimeout: Long = 0): ICommunicationPromise<T> {
    val newPromise = CommunicationPromise<T>(this.getTimeout() + additionalTimeout, this.isTimeoutEnabled())
    this.addCompleteListener {
        if (it.isSuccess) {
            val innerPromise = it.get()
            newPromise.copyStateFromOtherPromise(innerPromise)
        } else {
            newPromise.tryFailure(it.cause())
        }
    }
    return newPromise
}

fun <T : Any> List<ICommunicationPromise<T>>.toListPromise(): ICommunicationPromise<List<T>> {
    return this.combineAllPromises().then { this.map { it.getNow() } }
}

fun <T : Any> ICommunicationPromise<List<ICommunicationPromise<T>>>.toListPromise(): ICommunicationPromise<List<T>> {
    return this.then { list -> list.toListPromise()  }.flatten(400)
}