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

package eu.thesimplecloud.clientserverapi.cluster.packets.forward

import eu.thesimplecloud.clientserverapi.lib.bootstrap.ICommunicationBootstrap
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import io.netty.buffer.ByteBuf
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * Date: 20/02/2021
 * Time: 13:49
 * @author Frederick Baier
 */
class PacketIOForward : BytePacket {

    constructor(): super()

    @Volatile
    private var forwardComponentId: UUID? = null
    @Volatile
    private var packet: IPacket? = null

    constructor(forwardComponentId: UUID, packet: IPacket) : super() {
        this.forwardComponentId = forwardComponentId
        this.packet = packet

    }

    override fun write(byteBuf: ByteBuf, communicationBootstrap: ICommunicationBootstrap) {
        val mostSignificantBits = forwardComponentId!!.mostSignificantBits
        val leastSignificantBits = forwardComponentId!!.leastSignificantBits
        this.buffer.writeLong(mostSignificantBits)
        this.buffer.writeLong(leastSignificantBits)
        packet!!.write(this.buffer, communicationBootstrap)
    }

    override fun read(byteBuf: ByteBuf, communicationBootstrap: ICommunicationBootstrap) {

    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Any> {
        TODO("Not yet implemented")
    }
}