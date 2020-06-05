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

package eu.thesimplecloud.clientserverapi.lib.packet.packetsender

import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.flatten

interface IPacketSender {

    /**
     * Sends a query to the connection and returns a [ICommunicationPromise].
     * @return a [ICommunicationPromise] to wait for the result.
     */
    fun <T : Any> sendQuery(packet: IPacket, expectedResponseClass: Class<T>, timeout: Long = 200): ICommunicationPromise<T>

    /**
     * Sends a query asynchronously to the connection and returns a [ICommunicationPromise].
     * @return a [ICommunicationPromise] to wait for the result.
     */
    fun <T : Any> sendQueryAsync(packet: IPacket, expectedResponseClass: Class<T>, timeout: Long = 200): ICommunicationPromise<T> {
        return CommunicationPromise.runAsync { sendQuery(packet, expectedResponseClass, timeout) }.flatten()
    }

    /**
     * Sends a query to the connection and returns a [ICommunicationPromise].
     * @return a [ICommunicationPromise] to wait for the result.
     */
    fun sendUnitQuery(packet: IPacket, timeout: Long = 200): ICommunicationPromise<Unit> = sendQuery(packet, Unit::class.java, timeout)

    /**
     * Sends a query asynchronously to the connection and returns a [ICommunicationPromise].
     * @return a [ICommunicationPromise] to wait for the result.
     */
    fun sendUnitQueryAsync(packet: IPacket, timeout: Long = 200): ICommunicationPromise<Unit> = sendQueryAsync(packet, Unit::class.java, timeout)

}

/**
 * Sends a query to the connection and returns a [ICommunicationPromise].
 * @return a [ICommunicationPromise] to wait for the result.
 */
inline fun <reified T : Any> IPacketSender.sendQuery(packet: IPacket, timeout: Long = 200): ICommunicationPromise<T> = sendQuery(packet, T::class.java, timeout)

/**
 * Sends a query asynchronously to the connection and returns a [ICommunicationPromise].
 * @return a [ICommunicationPromise] to wait for the result.
 */
inline fun <reified T : Any> IPacketSender.sendQueryAsync(packet: IPacket, timeout: Long = 200): ICommunicationPromise<T> = sendQueryAsync(packet, T::class.java, timeout)