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
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.util.IAuthenticatable
import eu.thesimplecloud.clientserverapi.lib.util.ICommunicationBootstrapGetter

/**
 * Created by IntelliJ IDEA.
 * Date: 19/02/2021
 * Time: 15:38
 * @author Frederick Baier
 *
 * A packet sender for packets without a response object
 *
 */
interface IUnitPacketSender : ICommunicationBootstrapGetter, IAuthenticatable {

    /**
     * Sends a query to the connection and returns a [ICommunicationPromise].
     * @return a [ICommunicationPromise] to wait for the result.
     */
    fun sendUnitQuery(packet: IPacket, timeout: Long = 200): ICommunicationPromise<Unit>

    override fun isAuthenticated(): Boolean {
        return true
    }

}