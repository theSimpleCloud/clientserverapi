package eu.thesimplecloud.clientserverapi.lib.packet

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import eu.thesimplecloud.clientserverapi.lib.ByteBufStringHelper
import eu.thesimplecloud.clientserverapi.lib.json.JsonData

class PacketEncoder : MessageToByteEncoder<WrappedPacket>() {

    override fun encode(ctx: ChannelHandlerContext, wrappedPacket: WrappedPacket, byteBuf: ByteBuf) {
        val jsonData = JsonData()
        val packetData = PacketData(wrappedPacket.packetData.uniqueId, wrappedPacket.packetData.id)
        jsonData.append("data", packetData)
        ByteBufStringHelper.writeString(byteBuf, jsonData.asJsonString)
        wrappedPacket.packet.write(byteBuf)
    }
}