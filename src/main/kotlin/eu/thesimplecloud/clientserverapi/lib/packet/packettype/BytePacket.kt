package eu.thesimplecloud.clientserverapi.lib.packet.packettype

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.IPacketSender

abstract class BytePacket : IPacket {

    companion object {
        fun getNewEmptyBytePacket() = object: BytePacket() {
            override suspend fun handle(packetSender: IPacketSender): IPacket? {
                return null
            }
        }
        fun getNewBytePacketWithContent(byteArray: ByteArray): BytePacket {
            val bytePacket = getNewEmptyBytePacket()
            bytePacket.byteBuf = Unpooled.copiedBuffer(byteArray)
            return bytePacket
        }
        fun getNewBytePacketWithContent(byteBuf: ByteBuf): BytePacket {
            val bytePacket = getNewEmptyBytePacket()
            bytePacket.byteBuf = Unpooled.copiedBuffer(byteBuf)
            return bytePacket
        }

    }

    var byteBuf = Unpooled.buffer()

    override fun read(byteBuf: ByteBuf) {
        this.byteBuf = byteBuf.copy(byteBuf.readerIndex(), byteBuf.readableBytes())
    }

    override fun write(byteBuf: ByteBuf) {
        byteBuf.writeBytes(byteBuf)
    }

}