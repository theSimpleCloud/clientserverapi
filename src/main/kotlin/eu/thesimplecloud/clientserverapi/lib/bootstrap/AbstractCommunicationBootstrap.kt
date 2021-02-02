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
import eu.thesimplecloud.clientserverapi.lib.access.AuthAccessHandler
import eu.thesimplecloud.clientserverapi.lib.access.IAccessHandler
import eu.thesimplecloud.clientserverapi.lib.debug.DebugMessage
import eu.thesimplecloud.clientserverapi.lib.debug.DebugMessageManager
import eu.thesimplecloud.clientserverapi.lib.debug.IDebugMessageManager
import eu.thesimplecloud.clientserverapi.lib.directorywatch.DirectoryWatchManager
import eu.thesimplecloud.clientserverapi.lib.directorywatch.IDirectoryWatchManager
import eu.thesimplecloud.clientserverapi.lib.filetransfer.ITransferFileManager
import eu.thesimplecloud.clientserverapi.lib.filetransfer.TransferFileManager
import eu.thesimplecloud.clientserverapi.lib.filetransfer.directory.DirectorySyncManager
import eu.thesimplecloud.clientserverapi.lib.filetransfer.directory.IDirectorySyncManager
import eu.thesimplecloud.clientserverapi.lib.handler.IConnectionHandler
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.packetmanager.IPacketManager
import eu.thesimplecloud.clientserverapi.lib.packetmanager.PacketManager
import eu.thesimplecloud.clientserverapi.lib.packetresponse.IPacketResponseManager
import eu.thesimplecloud.clientserverapi.lib.packetresponse.PacketResponseManager
import eu.thesimplecloud.clientserverapi.lib.util.Address
import org.reflections.Reflections

/**
 * Created by IntelliJ IDEA.
 * Date: 27.06.2020
 * Time: 13:38
 * @author Frederick Baier
 */
abstract class AbstractCommunicationBootstrap(
    private val address: Address,
    private val connectionHandler: IConnectionHandler,
    private val cluster: ICluster? = null
) : ICommunicationBootstrap {

    @Volatile
    private var packetSearchClassLoader: ClassLoader = this::class.java.classLoader

    @Volatile
    private var classLoaderToSearchObjectPacketClasses: ClassLoader = this::class.java.classLoader

    @Volatile
    private var packetClassConverter: (Class<out IPacket>) -> Class<out IPacket> = { it }

    @Volatile
    private var accessHandler: IAccessHandler = AuthAccessHandler()


    private val transferFileManager = TransferFileManager()
    private val directoryWatchManager = DirectoryWatchManager()
    private val directorySyncManager = DirectorySyncManager(directoryWatchManager)
    private val debugMessageManager = DebugMessageManager()
    private val packetManager = PacketManager()
    private val packetResponseManager = PacketResponseManager()

    init {
        this.directoryWatchManager.startThread()
    }


    override fun getAddress(): Address {
        return this.address
    }

    override fun addPacketsByPackage(vararg packages: String) {
        packages.forEach { packageName ->
            val reflections = Reflections(packageName, this.packetSearchClassLoader)
            val allClasses = reflections.getSubTypesOf(IPacket::class.java)
                .union(reflections.getSubTypesOf(JsonPacket::class.java))
                .union(reflections.getSubTypesOf(ObjectPacket::class.java))
                .union(reflections.getSubTypesOf(BytePacket::class.java))
                .filter { it != JsonPacket::class.java && it != BytePacket::class.java && it != ObjectPacket::class.java }
            allClasses.forEach { packetClass ->
                if (this.getDebugMessageManager()
                        .isActive(DebugMessage.REGISTER_PACKET)
                ) println("Registered packet: ${packetClass.simpleName}")
                this.packetManager.registerPacket(this.packetClassConverter(packetClass))
            }
        }
    }

    override fun setPacketSearchClassLoader(classLoader: ClassLoader) {
        this.packetSearchClassLoader = classLoader
    }

    override fun setClassLoaderToSearchObjectPacketClasses(classLoader: ClassLoader) {
        this.classLoaderToSearchObjectPacketClasses = classLoader
    }

    override fun getClassLoaderToSearchObjectPacketsClasses(): ClassLoader {
        return this.classLoaderToSearchObjectPacketClasses
    }

    override fun setAccessHandler(accessHandler: IAccessHandler) {
        this.accessHandler = accessHandler
    }

    override fun getAccessHandler(): IAccessHandler {
        return this.accessHandler
    }

    override fun getTransferFileManager(): ITransferFileManager {
        return this.transferFileManager
    }

    override fun getDirectorySyncManager(): IDirectorySyncManager {
        return this.directorySyncManager
    }

    override fun getDirectoryWatchManager(): IDirectoryWatchManager {
        return this.directoryWatchManager
    }

    override fun getDebugMessageManager(): IDebugMessageManager {
        return this.debugMessageManager
    }

    override fun getPacketResponseManager(): IPacketResponseManager {
        return this.packetResponseManager
    }

    override fun getPacketManager(): IPacketManager {
        return this.packetManager
    }

    override fun getConnectionHandler(): IConnectionHandler {
        return this.connectionHandler
    }

    override fun setPacketClassConverter(function: (Class<out IPacket>) -> Class<out IPacket>) {
        this.packetClassConverter = function
    }

    override fun getCluster(): ICluster? {
        return this.cluster
    }
}