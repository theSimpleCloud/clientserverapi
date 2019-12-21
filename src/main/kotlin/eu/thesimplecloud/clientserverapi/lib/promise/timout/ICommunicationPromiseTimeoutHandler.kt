package eu.thesimplecloud.clientserverapi.lib.promise.timout

import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise


interface ICommunicationPromiseTimeoutHandler {

    /**
     * Handles the timeout of a promise
     */
    fun handleTimeout(communicationPromise: ICommunicationPromise<*>, timeout: Long)

}