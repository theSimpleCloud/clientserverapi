package eu.thesimplecloud.clientserverapi.lib.bootstrap

import eu.thesimplecloud.clientserverapi.lib.directorywatch.IDirectoryWatchManager
import eu.thesimplecloud.clientserverapi.lib.filetransfer.ITransferFileManager
import eu.thesimplecloud.clientserverapi.lib.filetransfer.directory.IDirectorySyncManager
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

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

}