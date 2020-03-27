package eu.thesimplecloud.clientserverapi.lib.directorywatch

import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.thread

class DirectoryWatchManager : IDirectoryWatchManager {

    private val list = CopyOnWriteArrayList<DirectoryWatch>()

    private lateinit var runningThread: Thread

    fun startThread() {
        runningThread = thread(start = true, isDaemon = true) {
            while (true) {
                for (directoryWatch in list) {
                    if (!directoryWatch.getDirectory().exists()) continue
                    val watchKey = directoryWatch.watchService.poll() ?: continue
                    for (watchEvent in watchKey.pollEvents()) {
                        val path = watchEvent.context() as Path
                        val split = path.toAbsolutePath().toString().split("\\")
                        val fileName = split.last()
                        val file = File(directoryWatch.getDirectory(), fileName)
                        when (watchEvent.kind()) {
                            StandardWatchEventKinds.ENTRY_CREATE -> {
                                directoryWatch.listeners.forEach { it.fileCreated(file) }
                            }
                            StandardWatchEventKinds.ENTRY_DELETE -> {
                                directoryWatch.listeners.forEach { it.fileDeleted(file) }
                            }
                            StandardWatchEventKinds.ENTRY_MODIFY -> {
                                if (file.exists() && !file.isDirectory)
                                    directoryWatch.listeners.forEach { it.fileModified(file) }
                            }
                        }
                    }
                    directoryWatch.initWatchService()
                }
                try {
                    Thread.sleep(1000)
                } catch (ex: InterruptedException) {
                }

            }
        }
    }

    fun stopThread() {
        this.runningThread.interrupt()
    }

    override fun createDirectoryWatch(directory: File): IDirectoryWatch {
        val directoryWatch = DirectoryWatch(this, directory)
        this.list.add(directoryWatch)
        return directoryWatch
    }

    override fun deleteDirectoryWatch(directoryWatch: IDirectoryWatch) {
        this.list.remove(directoryWatch)
    }


}