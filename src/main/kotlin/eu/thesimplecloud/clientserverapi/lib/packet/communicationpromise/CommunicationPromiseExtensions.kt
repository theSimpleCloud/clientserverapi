package eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise

fun ICommunicationPromise<Unit>.combineAll(list: Iterable<ICommunicationPromise<*>>): ICommunicationPromise<Unit> {
    list.forEach { promises ->
        promises.addResultListener {
            if (list.all { it.isDone }) {
                this.trySuccess(Unit)
            }
        }
    }
    return this
}