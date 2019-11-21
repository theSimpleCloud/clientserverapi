package eu.thesimplecloud.clientserverapi.lib.filetransfer.directory

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.directorywatch.DirectoryWatchManager
import eu.thesimplecloud.clientserverapi.lib.directorywatch.IDirectoryWatchManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class DirectorySyncManager(val directoryWatchManager: IDirectoryWatchManager) : IDirectorySyncManager {

    private val directorySyncList: MutableList<DirectorySync> = ArrayList()


    override fun createDirectorySync(directory: File, toDirectory: String): IDirectorySync {
        val directorySync = DirectorySync(directory, toDirectory, directoryWatchManager)
        directorySyncList.add(directorySync)
        return directorySync
    }

    override fun deleteDirectorySync(directory: File) {
        val directorySync = getDirectorySync(directory)
        if (directorySync !is DirectorySync) return
        this.directorySyncList.remove(directorySync)
    }

    override fun getDirectorySync(directory: File): IDirectorySync? = this.directorySyncList.firstOrNull { it.getDirectory() == directory }

    fun removeFromDirectorySyncs(connection: IConnection){
        directorySyncList.forEach { it.syncNoLonger(connection) }
    }
}