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

package eu.thesimplecloud.clientserverapi.testing.server

import eu.thesimplecloud.clientserverapi.lib.debug.IDebugMessageManager
import eu.thesimplecloud.clientserverapi.lib.directorywatch.IDirectoryWatchManager
import eu.thesimplecloud.clientserverapi.lib.filetransfer.ITransferFileManager
import eu.thesimplecloud.clientserverapi.lib.filetransfer.directory.IDirectorySyncManager
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packetmanager.IPacketManager
import eu.thesimplecloud.clientserverapi.lib.packetresponse.IPacketResponseManager
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.server.INettyServer
import eu.thesimplecloud.clientserverapi.server.client.clientmanager.IClientManager
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClientValue

/**
 * Created by IntelliJ IDEA.
 * Date: 11.06.2020
 * Time: 17:59
 * @author Frederick Baier
 */
class TestNettyServe<T : IConnectedClientValue> : INettyServer<T> {

    val cline

    override fun getClientManager(): IClientManager<T> {
    }

    override fun getPacketManager(): IPacketManager {
    }

    override fun isListening(): Boolean {
    }

    override fun getResponseManager(): IPacketResponseManager {
    }

    override fun addPacketsByPackage(vararg packages: String) {
    }

    override fun setPacketSearchClassLoader(classLoader: ClassLoader) {
    }

    override fun setClassLoaderToSearchObjectPacketClasses(classLoader: ClassLoader) {
    }

    override fun getClassLoaderToSearchObjectPacketsClasses(): ClassLoader {
    }

    override fun getTransferFileManager(): ITransferFileManager {
    }

    override fun getDirectorySyncManager(): IDirectorySyncManager {
    }

    override fun getDirectoryWatchManager(): IDirectoryWatchManager {
    }

    override fun getDebugMessageManager(): IDebugMessageManager {
    }

    override fun setPacketClassConverter(function: (Class<out IPacket>) -> Class<out IPacket>) {
    }

    override fun start(): ICommunicationPromise<Unit> {
    }

    override fun shutdown(): ICommunicationPromise<Unit> {
    }

    override fun isActive(): Boolean {
    }
}