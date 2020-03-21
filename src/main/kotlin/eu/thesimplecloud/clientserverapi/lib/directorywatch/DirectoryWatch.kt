package eu.thesimplecloud.clientserverapi.lib.directorywatch

import org.apache.commons.io.FileUtils
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchService

class DirectoryWatch(private val directoryWatchManager: IDirectoryWatchManager, private val directory: File) : IDirectoryWatch {

    lateinit var watchService: WatchService
    private val subDirectoryWatches = HashSet<IDirectoryWatch>()
    val listeners = ArrayList<IDirectoryWatchListener>()

    init {
        require(directory.isDirectory) { "Specified file must be a directory" }
        initWatchService()
        directory.listFiles().filter { it.isDirectory }.forEach {
            createSubDirectoryWatch(it)
        }

        addWatchListener(object : IDirectoryWatchListener {
            override fun fileCreated(file: File) {
                if (file.isDirectory) {
                    createSubDirectoryWatch(file)
                }
            }

            override fun fileModified(file: File) {
            }

            override fun fileDeleted(file: File) {
                removeSubDirectoryWatch(file)
            }

        })
    }

    private fun createSubDirectoryWatch(file: File) {
        val directoryWatch = this.directoryWatchManager.createDirectoryWatch(file)
        this.subDirectoryWatches.add(directoryWatch)
        getAllListenersExceptFirst().forEach { directoryWatch.addWatchListener(it) }
    }

    private fun removeSubDirectoryWatch(file: File) {
        val subDirectoryWatch = this.subDirectoryWatches.firstOrNull { it.getDirectory() == file } ?: return
        this.subDirectoryWatches.remove(subDirectoryWatch)
        this.directoryWatchManager.deleteDirectoryWatch(subDirectoryWatch)
    }

    fun initWatchService() {
        val path = directory.toPath()
        this.watchService = FileSystems.getDefault().newWatchService()
        path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY)
    }

    override fun addWatchListener(watchListener: IDirectoryWatchListener) {
        this.listeners.add(watchListener)
        this.subDirectoryWatches.forEach { it.addWatchListener(watchListener) }
    }

    override fun removeWatchListener(watchListener: IDirectoryWatchListener) {
        this.listeners.remove(watchListener)
        this.subDirectoryWatches.forEach { it.removeWatchListener(watchListener) }
    }

    fun getAllListenersExceptFirst() = this.listeners.drop(1)

    override fun getDirectory(): File = this.directory

}