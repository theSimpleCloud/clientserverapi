package eu.thesimplecloud.clientserverapi.lib.packet.packettype

import eu.thesimplecloud.clientserverapi.lib.bootstrap.ICommunicationBootstrap
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import io.netty.buffer.ByteBuf


abstract class ObjectPacket<T : Any>() : JsonPacket() {

    var value: T? = null

    constructor(value: T?) : this() {
        this.value = value
    }

    override fun read(byteBuf: ByteBuf, communicationBootstrap: ICommunicationBootstrap) {
        super.read(byteBuf, communicationBootstrap)
        val className = this.jsonData.getString("className")
        //return because the value to sent was null in this case.
        if (className.isNullOrBlank()) return
        val clazz = Class.forName(
                className,
                true,
                communicationBootstrap.getClassLoaderToSearchObjectPacketsClasses()
        ) as Class<T>
        this.value = this.jsonData.getObject("data", clazz)
    }

    override fun write(byteBuf: ByteBuf, communicationBootstrap: ICommunicationBootstrap) {
        if (this.value != null) {
            this.jsonData.append("className", value!!::class.java.name)
            this.jsonData.append("data", value)
        }
        super.write(byteBuf, communicationBootstrap)
    }

    companion object {

        fun <T : Any> getNewEmptyObjectPacket() = object : ObjectPacket<T>() {
            override suspend fun handle(connection: IConnection): ICommunicationPromise<Any> {
                return success(Unit)
            }
        }

        fun <T : Any> getNewObjectPacketWithContent(value: T?): ObjectPacket<T> {
            val objectPacket = getNewEmptyObjectPacket<T>()
            objectPacket.value = value
            return objectPacket
        }
    }

}