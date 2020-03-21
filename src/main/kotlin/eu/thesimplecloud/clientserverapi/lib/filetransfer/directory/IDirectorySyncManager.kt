package eu.thesimplecloud.clientserverapi.lib.filetransfer.directory

import java.io.File

interface IDirectorySyncManager {

    /**
     * Creates a [IDirectorySync] with the specified parameters
     * @param directory the directory to sync from
     * @param toDirectory the directory on the other side to sync to
     */
    fun createDirectorySync(directory: File, toDirectory: String): IDirectorySync

    /**
     * Deletes the [IDirectorySync] found by the specified directory
     */
    fun deleteDirectorySync(directory: File)

    /**
     * Returns the [IDirectorySync] found by the specified directory
     */
    fun getDirectorySync(directory: File): IDirectorySync?

    /**
     * Sets the directory where zip files will be stored.
     * This function can only be called before creating the first [IDirectorySync]
     */
    fun setTmpZipDirectory(tmpZipDir: File)

}