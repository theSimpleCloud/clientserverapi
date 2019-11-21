package eu.thesimplecloud.clientserverapi.lib.bootstrap

import eu.thesimplecloud.clientserverapi.lib.directorywatch.IDirectoryWatchManager
import eu.thesimplecloud.clientserverapi.lib.filetransfer.ITransferFileManager
import eu.thesimplecloud.clientserverapi.lib.filetransfer.directory.IDirectorySyncManager
import eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise.ICommunicationPromise
import io.netty.util.concurrent.GlobalEventExecutor

interface ICommunicationBootstrap : IBootstrap {

    /**
     * Registers all packets in the specified packages
     */
    fun addPacketsByPackage(vararg packages: String)

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
     * Returns a new [ICommunicationPromise]
     */
    fun <T> newPromise(): ICommunicationPromise<T> = CommunicationPromise()

    /**
     * Returns a new succeeded [ICommunicationPromise]
     */
    fun <T> newSucceededPromise(value: T): ICommunicationPromise<T> {
        val promise = newPromise<T>()
        promise.setSuccess(value)
        return promise
    }

    /**
     * Returns a new failed [ICommunicationPromise]
     */
    fun <T> newFailedPromise(throwable: Throwable): ICommunicationPromise<T> {
        val promise = newPromise<T>()
        promise.setFailure(throwable)
        return promise
    }

    /**
     * Returns a new failed [ICommunicationPromise] containing [Unit]
     */
    fun newSucceededUnitPromise(): ICommunicationPromise<Unit> {
        val promise = newPromise<Unit>()
        promise.setSuccess(Unit)
        return promise
    }

    /**
     * Returns a new failed [ICommunicationPromise] containing [Unit]
     */
    fun newFailedUnitPromise(throwable: Throwable): ICommunicationPromise<Unit> {
        val promise = newPromise<Unit>()
        promise.setFailure(throwable)
        return promise
    }

}