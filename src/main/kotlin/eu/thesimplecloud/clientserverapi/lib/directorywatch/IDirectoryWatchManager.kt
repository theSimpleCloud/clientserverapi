package eu.thesimplecloud.clientserverapi.lib.directorywatch

import java.io.File

interface IDirectoryWatchManager {

    /**
     * Creates a [IDirectoryWatch]
     * @return the created [IDirectoryWatch]
     */
    fun createDirectoryWatch(directory: File): IDirectoryWatch

    /**
     * Deletes the specified [IDirectoryWatch]
     */
    fun deleteDirectoryWatch(directoryWatch: IDirectoryWatch)

}