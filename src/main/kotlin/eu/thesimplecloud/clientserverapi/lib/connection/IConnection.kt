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

package eu.thesimplecloud.clientserverapi.lib.connection

import eu.thesimplecloud.clientserverapi.client.INettyClient
import eu.thesimplecloud.clientserverapi.lib.bootstrap.ICommunicationBootstrap
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.IPacketSender
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.util.Address
import eu.thesimplecloud.clientserverapi.lib.util.IAuthenticatable
import eu.thesimplecloud.clientserverapi.server.INettyServer
import java.io.File

interface IConnection : IAuthenticatable, IPacketSender {


    /**
     * Returns whether the connection is open
     * @return true if the connection is open
     */
    fun isOpen(): Boolean

    /**
     * Return whether the connection close was intended
     */
    fun wasConnectionCloseIntended(): Boolean

    /**
     * Returns the [ICommunicationBootstrap] of this connection
     * [INettyServer] when this connection is instantiated on the server side
     * [INettyClient] when this connection is instantiated on the client side
     */
    fun getCommunicationBootstrap(): ICommunicationBootstrap

    /**
     * Sends a file via this connection
     * @param file the file to send
     * @param savePath the path where the file should be saved
     */
    fun sendFile(file: File, savePath: String, timeout: Long): ICommunicationPromise<Unit>

    /**
     * Closes this [IConnection]
     */
    fun closeConnection(): ICommunicationPromise<Unit>

    /**
     * Returns the address of this [IConnection]
     * @throws IllegalStateException if the connection is closed
     */
    fun getAddress(): Address


}