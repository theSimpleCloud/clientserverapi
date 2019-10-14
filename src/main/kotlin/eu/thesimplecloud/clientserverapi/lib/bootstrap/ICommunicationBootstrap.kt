package eu.thesimplecloud.clientserverapi.lib.bootstrap

import eu.thesimplecloud.clientserverapi.lib.filetransfer.ITransferFileManager
import eu.thesimplecloud.clientserverapi.lib.filetransfer.directory.IDirectorySyncManager
import eu.thesimplecloud.clientserverapi.lib.packet.connectionpromise.ConnectionPromise
import eu.thesimplecloud.clientserverapi.lib.packet.connectionpromise.IConnectionPromise
import io.netty.util.concurrent.EventExecutor
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
     * Returns a new [IConnectionPromise]
     */
    fun <T> newPromise(): IConnectionPromise<T> = ConnectionPromise(GlobalEventExecutor.INSTANCE)

    /**
     * Returns a new [IConnectionPromise]
     */
    fun <T> newSucceededPromise(value: T): IConnectionPromise<T> {
        val promise = newPromise<T>()
        promise.setSuccess(value)
        return promise
    }

}