package eu.thesimplecloud.clientserverapi.filetransfer.directory

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import java.io.File

interface IDirectorySync {

    fun getDirectory(): File

    fun syncDirectory(connection: IConnection)

    fun syncNoLonger(connection: IConnection)

}