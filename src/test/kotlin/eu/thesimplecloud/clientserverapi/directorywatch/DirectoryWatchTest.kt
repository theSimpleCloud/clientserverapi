package eu.thesimplecloud.clientserverapi.directorywatch

import eu.thesimplecloud.clientserverapi.lib.directorywatch.DirectoryWatchManager
import eu.thesimplecloud.clientserverapi.lib.directorywatch.IDirectoryWatchListener
import org.junit.Test
import java.io.File

class DirectoryWatchTest {

    /*

    @Test
    fun test() {
        val directoryWatchManager = DirectoryWatchManager()
        directoryWatchManager.startThread()
        val directoryWatch = directoryWatchManager.createDirectoryWatch(File("dirSyncTest/"))
        directoryWatch.addWatchListener(object : IDirectoryWatchListener {
            override fun fileCreated(file: File) {
                println("file created ${file.absolutePath}")
            }

            override fun fileModified(file: File) {
                println("file modified ${file.absolutePath}")
            }

            override fun fileDeleted(file: File) {
                println("file deleted ${file.absolutePath}")
            }

        })
        while (true) {

        }
    }

    */

}