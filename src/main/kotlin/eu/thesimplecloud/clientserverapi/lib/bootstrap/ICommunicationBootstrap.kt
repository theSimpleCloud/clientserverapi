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

package eu.thesimplecloud.clientserverapi.lib.bootstrap

import eu.thesimplecloud.clientserverapi.cluster.ICluster
import eu.thesimplecloud.clientserverapi.lib.access.IAccessHandler
import eu.thesimplecloud.clientserverapi.lib.debug.IDebugMessageManager
import eu.thesimplecloud.clientserverapi.lib.directorywatch.IDirectoryWatchManager
import eu.thesimplecloud.clientserverapi.lib.filetransfer.ITransferFileManager
import eu.thesimplecloud.clientserverapi.lib.filetransfer.directory.IDirectorySyncManager
import eu.thesimplecloud.clientserverapi.lib.handler.IConnectionHandler
import eu.thesimplecloud.clientserverapi.lib.list.manager.ISyncListManager
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.codec.IPacketDecoder
import eu.thesimplecloud.clientserverapi.lib.packet.codec.IPacketEncoder
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.packetmanager.IPacketManager
import eu.thesimplecloud.clientserverapi.lib.packetresponse.IPacketResponseManager
import eu.thesimplecloud.clientserverapi.lib.util.Address
import eu.thesimplecloud.clientserverapi.server.INettyServer

interface ICommunicationBootstrap : IBootstrap {

    /**
     * Returns the address
     */
    fun getAddress(): Address

    /**
     * Registers all packets in the specified packages
     */
    fun addPacketsByPackage(vararg packages: String)

    /**
     * Sets the [ClassLoader]s to find the packets. If no class loader was given, the system classloader will be used.
     */
    fun setPacketSearchClassLoader(classLoader: ClassLoader)

    /**
     * Sets the [ClassLoader]s used to find the classes in [ObjectPacket]s
     */
    fun setClassLoaderToSearchObjectPacketClasses(classLoader: ClassLoader)

    /**
     * Returns the [ClassLoader]s used to find the classes in [ObjectPacket]s
     */
    fun getClassLoaderToSearchObjectPacketsClasses(): ClassLoader

    /**
     * Sets the access handler
     */
    fun setAccessHandler(accessHandler: IAccessHandler)

    /**
     * Returns the [IAccessHandler]
     */
    fun getAccessHandler(): IAccessHandler

    /**
     * Returns the [ITransferFileManager] to transfer files
     */
    fun getTransferFileManager(): ITransferFileManager

    /**
     * Returns the [IDirectorySyncManager] to synchronize directories between server and client
     */
    fun getDirectorySyncManager(): IDirectorySyncManager

    /**
     * Returns the [IDirectoryWatchManager] to listen for directory changes
     */
    fun getDirectoryWatchManager(): IDirectoryWatchManager

    /**
     * Returns the [IDebugMessageManager] to handle debug messages.
     */
    fun getDebugMessageManager(): IDebugMessageManager

    /**
     * Returns the client-server [ISyncListManager]
     */
    fun getClientServerSyncListManager(): ISyncListManager

    /**
     * Returns the [IPacketResponseManager]
     */
    fun getPacketResponseManager(): IPacketResponseManager

    /**
     * Returns the [IPacketManager]
     */
    fun getPacketManager(): IPacketManager

    /**
     * Returns the connection handler
     */
    fun getConnectionHandler(): IConnectionHandler

    /**
     * Returns the [IPacketDecoder]
     */
    fun getPacketDecoder(): IPacketDecoder

    /**
     * Returns the [IPacketDecoder]
     */
    fun getPacketEncoder(): IPacketEncoder

    /**
     * Returns whether this is a server.
     */
    fun isServer() = this is INettyServer

    /**
     * Sets the packet class converter.
     * The packet class converter is used to find packets with reflections and one class loader and
     * convert these classes later to an other class to avoid class loader bugs.
     */
    fun setPacketClassConverter(function: (Class<out IPacket>) -> Class<out IPacket>)

    /**
     * Returns the cluster this bootstrap is part of
     */
    fun getCluster(): ICluster?

}