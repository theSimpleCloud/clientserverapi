package eu.thesimplecloud.clientserverapi.lib.packet

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import io.netty.buffer.ByteBuf
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

interface IPacket {


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
     * @return the object that should be sent as result. If it is null a response will be sent containing null.
     * If an exception is thrown it will be sent as response and the receiving promise will fail with it.
     * If a [ICommunicationPromise] is returned the result of it will be sent as response.
     */
    suspend fun handle(connection: IConnection): Any?

}