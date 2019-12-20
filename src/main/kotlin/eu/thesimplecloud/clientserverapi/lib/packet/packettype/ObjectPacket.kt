package eu.thesimplecloud.clientserverapi.lib.packet.packettype

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import io.netty.buffer.ByteBuf
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.IPacketSender
import io.netty.util.internal.StringUtil


abstract class ObjectPacket<T : Any>() : JsonPacket() {

    var value: T? = null

    constructor(value: T?) : this() {
        this.value = value
    }

    override fun read(byteBuf: ByteBuf) {
        super.read(byteBuf)
        val className = this.jsonData.getString("className")
        //return because the value to sent was null in this case.
        if (className.isNullOrBlank()) return
        val clazz = Class.forName(className) as Class<T>
        value = this.jsonData.getObject("data", clazz)
    }

    override fun write(byteBuf: ByteBuf) {
        if (this.value != null) {
            this.jsonData.append("className", value!!::class.java.name)
            this.jsonData.append("data", value)
        }
        super.write(byteBuf)
    }

    companion object {

        fun <T : Any> getNewEmptyObjectPacket() = object : ObjectPacket<T>() {
            override suspend fun handle(connection: IConnection): IPacket? {
                return null
            }
        }

        fun <T : Any> getNewObjectPacketWithContent(value: T?): ObjectPacket<T> {
            val objectPacket = getNewEmptyObjectPacket<T>()
            objectPacket.value = value
            return objectPacket
        }
    }

}