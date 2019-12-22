package eu.thesimplecloud.clientserverapi.lib.packet

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import io.netty.buffer.ByteBuf
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

interface IPacket : IPacketHelperMethods {


    /**
     * This method is used to read the content of the [ByteBuf] and save it in the packet
     * Note: [read] will be called before [handle]
     */
    fun read(byteBuf: ByteBuf)

    /**
     * This method is used to write the packets content into the [ByteBuf]
     */
    fun write(byteBuf: ByteBuf)

    /**
     * Calls to handle a packet
     * @return the [ICommunicationPromise] that will be completed with the result or a failure.
     * The result will be sent as response.
     */
    suspend fun handle(connection: IConnection): ICommunicationPromise<out Any>

}