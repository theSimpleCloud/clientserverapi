package eu.thesimplecloud.clientserverapi.lib.directorywatch

import java.io.File

class DirectoryWatch(private val directoryWatchManager: IDirectoryWatchManager, private val directory: File) : IDirectoryWatch {

    private val subDirectoryWatches = HashSet<IDirectoryWatch>()
    private val listeners = ArrayList<IDirectoryWatchListener>()

    private var lastTickFiles = getAllCurrentFiles()


    //one tick lasts 200ms
    fun tick() {
        val allCurrentFiles = getAllCurrentFiles()
        val addedFiles = allCurrentFiles.toMutableList()
        addedFiles.removeAll(lastTickFiles)
        addedFiles.forEach { file -> this.listeners.forEach { it.fileCreated(file) } }
        val modifiedFiles = this.lastTickFiles.filter { (System.currentTimeMillis() - it.lastModified()) in 200..399 }
        modifiedFiles.forEach { file -> this.listeners.forEach { it.fileModified(file) } }
        val removedFiles = lastTickFiles.toMutableList()
        removedFiles.removeAll(allCurrentFiles)
        removedFiles.forEach { file -> this.listeners.forEach { it.fileDeleted(file) } }
        this.lastTickFiles = getAllCurrentFiles()
    }


    private fun getAllCurrentFiles(): List<File> {
        return this.directory.listFiles()?.toList() ?: emptyList()
    }

    init {
        require(directory.isDirectory) { "Specified file must be a directory" }
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