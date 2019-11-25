package eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise


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