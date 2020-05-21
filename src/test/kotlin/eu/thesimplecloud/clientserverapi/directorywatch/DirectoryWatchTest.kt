package eu.thesimplecloud.clientserverapi.directorywatch

import eu.thesimplecloud.clientserverapi.lib.directorywatch.DirectoryWatchManager
import eu.thesimplecloud.clientserverapi.lib.directorywatch.IDirectoryWatch
import eu.thesimplecloud.clientserverapi.lib.directorywatch.IDirectoryWatchListener
import org.apache.commons.io.FileUtils
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.io.File

class DirectoryWatchTest {

    private lateinit var directoryWatch: IDirectoryWatch
    private lateinit var watchListener: IDirectoryWatchListener
    private lateinit var directoryWatchManager: DirectoryWatchManager
    private val testDir = File("dirSyncTest/")

    @Before
    fun before() {
        this.directoryWatchManager = DirectoryWatchManager()
        this.directoryWatchManager.startThread()

        testDir.mkdirs()
        this.directoryWatch = directoryWatchManager.createDirectoryWatch(testDir)
        this.watchListener = mock(IDirectoryWatchListener::class.java)
        directoryWatch.addWatchListener(watchListener)
    }

    @Test
    fun simple_create_file_test() {
        val testFile = File(this.testDir, "test.txt")
        testFile.createNewFile()
        Thread.sleep(500)
        verify(this.watchListener).fileCreated(testFile)
    }

    @Test
    fun simple_delete_file_test() {
        val testFile = File(this.testDir, "test.txt")
        testFile.createNewFile()
        Thread.sleep(500)
        testFile.delete()
        Thread.sleep(200)
        verify(this.watchListener).fileDeleted(testFile)
    }

    @Test
    fun folder_create_test() {
        val testFile = File(this.testDir, "test/")
        testFile.mkdirs()
        Thread.sleep(500)
        verify(this.watchListener).fileCreated(testFile)
    }

    @Test
    fun folder_delete_test() {
        val testFile = File(this.testDir, "test/")
        testFile.mkdirs()
        Thread.sleep(500)
        testFile.delete()
        Thread.sleep(200)
        verify(this.watchListener).fileDeleted(testFile)
    }

    @Test
    fun folder_with_content_create_test() {
        val testFolder = File(this.testDir, "test/")
        val testFile = File(this.testDir, "test/test.txt")
        testFolder.mkdirs()
        Thread.sleep(500)
        testFile.createNewFile()
        Thread.sleep(500)
        testFile.delete()
        Thread.sleep(250)
        verify(this.watchListener).fileCreated(testFolder)
        verify(this.watchListener).fileCreated(testFile)
        verify(this.watchListener).fileDeleted(testFile)
    }

    @Test
    fun folder_with_content_delete_test() {
        val testFolder = File(this.testDir, "test/")
        val testFile = File(this.testDir, "test/test.txt")
        testFolder.mkdirs()
        testFile.createNewFile()
        Thread.sleep(500)
        FileUtils.deleteDirectory(testFolder)
        Thread.sleep(250)
        verify(this.watchListener).fileCreated(testFolder)
        verify(this.watchListener).fileDeleted(testFolder)
        verify(this.watchListener, times(0)).fileDeleted(testFile)
    }

    @After
    fun after() {
        this.directoryWatchManager.stopThread()
        FileUtils.deleteDirectory(testDir)
    }




}