package eu.thesimplecloud.clientserverapi.lib.packet.packettype

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import io.netty.buffer.ByteBuf
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.IPacketSender


abstract class ObjectPacket<T : Any>(val typeParameterClass: Class<out T>) : JsonPacket() {

    var value: T? = null

    constructor(value: T) : this(value::class.java)

    override fun read(byteBuf: ByteBuf) {
        super.read(byteBuf)
        value = this.jsonData.getObject("data", typeParameterClass)
    }

    override fun write(byteBuf: ByteBuf) {
        this.jsonData.append("data", value)
        super.write(byteBuf)
    }

    companion object {

        fun getNewEmptyObjectPacket(): ObjectPacket<Any> = getNewEmptyObjectPacket(Any::class.java)

        fun <T : Any> getNewEmptyObjectPacket(type: Class<T>) = object : ObjectPacket<T>(type) {
            override suspend fun handle(connection: IConnection): IPacket? {
                return null
            }
        }

        fun <T : Any> getNewObjectPacketWithContent(value: T): ObjectPacket<T> {
            val objectPacket = getNewEmptyObjectPacket(value::class.java) as ObjectPacket<T>
            objectPacket.value = value
            return objectPacket
        }
    }

}