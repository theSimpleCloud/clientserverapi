package eu.thesimplecloud.clientserverapi.lib.packet

import eu.thesimplecloud.clientserverapi.lib.bootstrap.ICommunicationBootstrap
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import io.netty.buffer.ByteBuf

interface IPacket : IPacketHelperMethods {


    /**
     * This method is used to read the content of the [ByteBuf] and save it in the packet
     * Note: [read] will be called before [handle]
     * @param byteBuf the [ByteBuf] to read from
     * @param communicationBootstrap the [ICommunicationBootstrap] of this side
     */
    fun read(byteBuf: ByteBuf, communicationBootstrap: ICommunicationBootstrap)

    /**
     * This method is used to write the packets content into the [ByteBuf]
     * @param byteBuf the [ByteBuf] to write to
     * @param communicationBootstrap the [ICommunicationBootstrap] of this side
     */
    fun write(byteBuf: ByteBuf, communicationBootstrap: ICommunicationBootstrap)

    /**
     * Calls to handle a packet
     * @return the [ICommunicationPromise] that will be completed with the result or a failure.
     * The result will be sent as response.
     */
    suspend fun handle(connection: IConnection): ICommunicationPromise<out Any>

}