package eu.thesimplecloud.clientserverapi.lib.filetransfer.directory

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.directorywatch.IDirectoryWatchManager
import java.io.File
import kotlin.concurrent.thread

class DirectorySyncManager(private val directoryWatchManager: IDirectoryWatchManager) : IDirectorySyncManager {

    private val directorySyncList: MutableList<DirectorySync> = ArrayList()
    @Volatile
    var tmpZipDir: File = File("tmpZip/")
        private set

    init {
        startThread()
    }

    private fun startThread() {
        thread(start = true, isDaemon = true) {
            while (true) {
                this.directorySyncList.forEach { it.checkForReZip() }
                Thread.sleep(1000)
            }
        }
    }


    override fun createDirectorySync(directory: File, toDirectory: String): IDirectorySync {
        val directorySync = DirectorySync(directory, toDirectory, this.tmpZipDir, this.directoryWatchManager.createDirectoryWatch(directory))
        directorySyncList.add(directorySync)
        return directorySync
    }

    override fun deleteDirectorySync(directory: File) {
        val directorySync = getDirectorySync(directory)
        if (directorySync !is DirectorySync) return
        this.directorySyncList.remove(directorySync)
    }

    override fun getDirectorySync(directory: File): IDirectorySync? = this.directorySyncList.firstOrNull { it.getDirectory() == directory }

    override fun setTmpZipDirectory(tmpZipDir: File) {
        if (this.directorySyncList.isNotEmpty()) throw IllegalStateException("There must be no registered DirectorySyncs when calling this")
        this.tmpZipDir = tmpZipDir
    }

    fun removeFromDirectorySyncs(connection: IConnection) {
        directorySyncList.forEach { it.syncNoLonger(connection) }
    }
}