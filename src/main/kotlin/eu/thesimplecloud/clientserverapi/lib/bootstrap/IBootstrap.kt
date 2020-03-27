package eu.thesimplecloud.clientserverapi.lib.bootstrap

import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

interface IBootstrap {

    /**
     * Starts this bootstrap
     * @return a promise that completes when the process is started
     */
    fun start(): ICommunicationPromise<Unit>

    /**
     * Stops this bootstrap
     * @return a promise that completes when the process is stopped
     */
    fun shutdown(): ICommunicationPromise<Unit>

    /**
     * Returns whether this bootstrap was started and is running
     */
    fun isActive(): Boolean

}