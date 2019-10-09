package eu.thesimplecloud.clientserverapi.lib.bootstrap

import eu.thesimplecloud.clientserverapi.lib.filetransfer.ITransferFileManager
import eu.thesimplecloud.clientserverapi.lib.filetransfer.directory.IDirectorySyncManager
import eu.thesimplecloud.clientserverapi.lib.packet.connectionpromise.ConnectionPromise
import eu.thesimplecloud.clientserverapi.lib.packet.connectionpromise.IConnectionPromise
import io.netty.util.concurrent.EventExecutor

interface ICommunicationBootstrap : IBootstrap {

    /**
     * Returns the [ITransferFileManager] to transfer files
     */
    fun getTransferFileManager(): ITransferFileManager

    /**
     * Returns the [IDirectorySyncManager] to synchronize directories between server and client
     */
    fun getDirectorySyncManager(): IDirectorySyncManager

    /**
     * Returns the [EventExecutor]
     */
    fun getEventExecutor(): EventExecutor

    /**
     * Returns a new [IConnectionPromise]
     */
    fun <T> newPromise(): IConnectionPromise<T> = ConnectionPromise(getEventExecutor())

}