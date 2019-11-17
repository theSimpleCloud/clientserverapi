package eu.thesimplecloud.clientserverapi.lib.directorywatch

import java.io.File

interface IDirectoryWatchListener {

    /**
     * Called when a file was created.
     */
    fun fileCreated(file: File)

    /**
     * Called when a file was modified.
     */
    fun fileModified(file: File)

    /**
     * Called when a file was deleted.
     */
    fun fileDeleted(file: File)

}