package eu.thesimplecloud.clientserverapi.lib.bootstrap

import eu.thesimplecloud.clientserverapi.lib.debug.IDebugMessageManager
import eu.thesimplecloud.clientserverapi.lib.directorywatch.IDirectoryWatchManager
import eu.thesimplecloud.clientserverapi.lib.filetransfer.ITransferFileManager
import eu.thesimplecloud.clientserverapi.lib.filetransfer.directory.IDirectorySyncManager
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.server.INettyServer

interface ICommunicationBootstrap : IBootstrap {

    /**
     * Registers all packets in the specified packages
     */
    fun addPacketsByPackage(vararg packages: String)

    /**
     * Adds the [ClassLoader]s to find the packets. If no class loader was given, the system classloader will be used.
     */
    fun addClassLoader(vararg classLoaders: ClassLoader)

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
     * Returns whether this is a server.
     */
    fun isServer() = this is INettyServer<*>

    /**
     * Sets the packet class converter.
     * The packet class converter is used to find packets with reflections and one class loader and
     * convert these classes later to an other class to avoid class loader bugs.
     */
    fun setPacketClassConverter(function: (Class<out IPacket>) -> Class<out IPacket>)

}