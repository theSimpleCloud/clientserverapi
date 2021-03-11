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

package eu.thesimplecloud.clientserverapi.lib.defaultpackets

import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.IPacketSender
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*

class PacketIOCreateFileTransfer() : JsonPacket() {

    constructor(uuid: UUID, path: String, lastModified: Long): this() {
        this.jsonLib.append("uuid", uuid).append("path", path).append("lastModified", lastModified)
    }

    override suspend fun handle(sender: IPacketSender): ICommunicationPromise<Unit> {
        val uuid = this.jsonLib.getObject("uuid", UUID::class.java) ?: return contentException("uuid")
        val path = this.jsonLib.getString("path") ?: return contentException("path")
        val lastModified = this.jsonLib.getLong("lastModified") ?: return contentException("lastModified")
        sender.getCommunicationBootstrap().getTransferFileManager().creteFileTransfer(uuid, path, lastModified)
        return unit()
    }
}