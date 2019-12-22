package eu.thesimplecloud.clientserverapi.lib.packet

import eu.thesimplecloud.clientserverapi.lib.packet.exception.MissingPacketContentException
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

interface IPacketHelperMethods {

    /**
     * Returns a promise succeeded with the specified [value]
     */
    fun <T> success(value: T) = CommunicationPromise(value)

    /**
     * Returns a promise completed with [Unit]
     */
    fun unit(): ICommunicationPromise<Unit> = CommunicationPromise(Unit)

    /**
     * Returns a promise failed with the specified [throwable]
     */
    fun <T> failed(throwable: Throwable) = CommunicationPromise<T>(throwable)

    /**
     * Returns a promise failed with [MissingPacketContentException]
     */
    fun <T> contentException(unavailableProperty: String) = failed<T>(MissingPacketContentException(unavailableProperty))

}