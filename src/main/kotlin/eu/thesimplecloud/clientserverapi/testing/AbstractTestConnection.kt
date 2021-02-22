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

package eu.thesimplecloud.clientserverapi.testing

import eu.thesimplecloud.clientserverapi.lib.connection.AbstractConnection
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.util.Address
import java.io.IOException
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by IntelliJ IDEA.
 * Date: 12.06.2020
 * Time: 09:20
 * @author Frederick Baier
 */

abstract class AbstractTestConnection(
    val port: Int
) : AbstractConnection() {


    @Volatile
    var otherSideConnection: IConnection? = null

    val receivedPackets = CopyOnWriteArrayList<WrappedPacket>()

    override fun incomingPacket(wrappedPacket: WrappedPacket) {
        this.receivedPackets.add(wrappedPacket)
        if (this.receivedPackets.size > 100)
            this.receivedPackets.dropLast(1)

        super.incomingPacket(wrappedPacket)
    }

    override fun sendPacket(wrappedPacket: WrappedPacket, promise: ICommunicationPromise<Any>) {
        if (!isOpen()) {
            val exception =
                IOException("Connection is closed. Packet to send was ${wrappedPacket.packetHeader.sentPacketName}. This: ${this::class.java.name}")
            promise.tryFailure(exception)
            return
        }
        NetworkTestManager.sendPacket(this, wrappedPacket)
    }

    override fun isOpen(): Boolean {
        return this.otherSideConnection != null
    }

    override fun closeConnection(): ICommunicationPromise<Unit> {
        NetworkTestManager.closeConnection(this)
        return CommunicationPromise.of(Unit)
    }

    override fun getAddress(): Address {
        if (!isOpen()) throw IllegalStateException("Connection is not open")
        return Address("127.0.0.1", port)
    }


}

