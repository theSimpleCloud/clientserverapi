package eu.thesimplecloud.clientserverapi.lib.directorywatch

import java.io.File

interface IDirectoryWatch {

    /**
     * Returns the directory this watcher is watching to.
     */
    fun getDirectory(): File

    /**
     * Adds a [IDirectoryWatchListener] to this [IDirectoryWatch]
     */
    fun addWatchListener(watchListener: IDirectoryWatchListener)

    /**
     * Removed a [IDirectoryWatchListener] from this [IDirectoryWatch]
     */
    fun removeWatchListener(watchListener: IDirectoryWatchListener)

}