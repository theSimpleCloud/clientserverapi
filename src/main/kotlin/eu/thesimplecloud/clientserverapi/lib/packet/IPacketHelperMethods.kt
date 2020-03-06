package eu.thesimplecloud.clientserverapi.lib.packet

import eu.thesimplecloud.clientserverapi.lib.packet.exception.MissingPacketContentException
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

interface IPacketHelperMethods {

    /**
     * Returns a promise succeeded with the specified [value]
     */
    fun <T : Any> success(value: T) = CommunicationPromise(value)

    /**
     * Returns a promise completed with [Unit]
     */
    fun unit(): ICommunicationPromise<Unit> = CommunicationPromise(Unit)

    /**
     * Returns a promise failed with the specified [throwable]
     */
    fun <T : Any> failure(throwable: Throwable) = CommunicationPromise<T>(throwable)

    /**
     * Returns a promise failed with [MissingPacketContentException]
     */
    fun <T : Any> contentException(unavailableProperty: String) = failure<T>(MissingPacketContentException(unavailableProperty))

}