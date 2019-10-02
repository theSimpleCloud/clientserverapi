package eu.thesimplecloud.clientserverapi.lib.packet.packettype

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket

abstract class BytePacket : IPacket {

    companion object {
        fun getNewEmptyBytePacket() = object: BytePacket() {
            override suspend fun handle(connection: IConnection): IPacket? {
                return null
            }
        }
        fun getNewBytePacketWithContent(byteArray: ByteArray): BytePacket {
            val bytePacket = getNewEmptyBytePacket()
            bytePacket.buffer = Unpooled.copiedBuffer(byteArray)
            return bytePacket
        }
        fun getNewBytePacketWithContent(byteBuf: ByteBuf): BytePacket {
            val bytePacket = getNewEmptyBytePacket()
            bytePacket.buffer = Unpooled.copiedBuffer(byteBuf)
            return bytePacket
        }

    }

    var buffer = Unpooled.buffer()

    override fun read(byteBuf: ByteBuf) {
        val byteArray = ByteArray(byteBuf.readInt())
        byteBuf.readBytes(byteArray)
        this.buffer = Unpooled.copiedBuffer(byteArray)
    }

    override fun write(byteBuf: ByteBuf) {
        val byteArray = this.buffer.array()
        byteBuf.writeInt(byteArray.size)
        byteBuf.writeBytes(byteArray)
    }

}