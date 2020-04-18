package eu.thesimplecloud.clientserverapi.lib.packet.packettype

import eu.thesimplecloud.clientserverapi.lib.ByteBufStringHelper
import eu.thesimplecloud.clientserverapi.lib.bootstrap.ICommunicationBootstrap
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import io.netty.buffer.ByteBuf

abstract class JsonPacket : IPacket {

    companion object {
        fun getNewEmptyJsonPacket() = object: JsonPacket() {
            override suspend fun handle(connection: IConnection): ICommunicationPromise<Any> {
                return success(Unit)
            }
        }
        fun getNewJsonPacketWithContent(jsonData: JsonData): JsonPacket {
            val jsonPacket = getNewEmptyJsonPacket()
            jsonPacket.jsonData = jsonData
            return jsonPacket
        }
    }

    var jsonData: JsonData = JsonData()

    override fun read(byteBuf: ByteBuf, communicationBootstrap: ICommunicationBootstrap) {
        jsonData = JsonData.fromJsonString(ByteBufStringHelper.nextString(byteBuf))

    }

    override fun write(byteBuf: ByteBuf, communicationBootstrap: ICommunicationBootstrap) {
        jsonData.getAsJsonString().let { ByteBufStringHelper.writeString(byteBuf, it) }
    }
}