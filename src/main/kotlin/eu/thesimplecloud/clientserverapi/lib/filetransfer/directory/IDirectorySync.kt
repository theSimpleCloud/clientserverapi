package eu.thesimplecloud.clientserverapi.lib.filetransfer.directory

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import java.io.File

interface IDirectorySync {

    /**
     * Returns the directory to sync from
     */
    fun getDirectory(): File

    /**
     * Synchronizes the content of the directory with the content of the directory on the other side.
     * It also will update all changes to the specified connection
     */
    fun syncDirectory(connection: IConnection)

    /**
     * Removes the connection from the list with connections to sync the directory to.
     * The connection will no longer receive updates of this directory.
     */
    fun syncNoLonger(connection: IConnection)

}