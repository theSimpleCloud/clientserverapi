package eu.thesimplecloud.clientserverapi.lib.packet

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import io.netty.buffer.ByteBuf

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
     * @return the packet that should be sent as result. If the packet is null it will send a response containing null.
     */
    suspend fun handle(connection: IConnection): IPacket?

}