/*
 * MIT License
 *
 * Copyright (C) 2020 Frederick Baier
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.clientserverapi.lib.packet.packettype

import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.IPacketSender
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

abstract class BytePacket : IPacket {

    companion object {
        fun getNewEmptyBytePacket() = object: BytePacket() {
            override suspend fun handle(sender: IPacketSender): ICommunicationPromise<Unit> {
                return unit()
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

    override fun read(byteBuf: ByteBuf, sender: IPacketSender) {
        val byteArray = ByteArray(byteBuf.readInt())
        byteBuf.readBytes(byteArray)
        this.buffer = Unpooled.copiedBuffer(byteArray)
    }

    override fun write(byteBuf: ByteBuf, sender: IPacketSender) {
        val byteArray = this.buffer.array()
        byteBuf.writeInt(byteArray.size)
        byteBuf.writeBytes(byteArray)
    }

}