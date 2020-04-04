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

    private var runningThread: Thread? = null


    fun startThread() {
        runningThread = thread(start = true, isDaemon = true) {
            while (true) {
                for (directoryWatch in list) {
                    if (!directoryWatch.getDirectory().exists()) continue
                    directoryWatch.tick()
                }
                try {
                    Thread.sleep(200)
                } catch (e: InterruptedException) {

                }
            }
        }
    }

    fun stopThread() {
        this.runningThread?.interrupt()
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