package eu.thesimplecloud.clientserverapi.lib.promise.timout

import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.excpetion.PromiseTimeoutException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeoutException
import kotlin.concurrent.thread

class CommunicationPromiseTimeoutHandler private constructor() : ICommunicationPromiseTimeoutHandler {

    companion object {
        val INSTANCE: ICommunicationPromiseTimeoutHandler = CommunicationPromiseTimeoutHandler()
    }

    /**
     * This map contains a promise and its timeout timestamp in milliseconds
     */
    private val promiseToTimeout = ConcurrentHashMap<ICommunicationPromise<*>, Long>()

    init {
        startThread()
    }


    private fun startThread() {
        thread(start = true, isDaemon = true) {
            while (true) {
                val timedOutPromises = promiseToTimeout.entries.filter { it.value < System.currentTimeMillis() }.map { it.key }
                timedOutPromises.forEach { it.tryFailure(PromiseTimeoutException("Timed out", (it as CommunicationPromise<*>).creationError)) }
                timedOutPromises.forEach { promiseToTimeout.remove(it) }
                Thread.sleep(10)
            }
        }
    }

    override fun handleTimeout(communicationPromise: ICommunicationPromise<*>, timeout: Long) {
        this.promiseToTimeout[communicationPromise] = System.currentTimeMillis() + timeout
    }

}