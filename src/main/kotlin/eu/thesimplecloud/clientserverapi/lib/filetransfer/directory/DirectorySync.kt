package eu.thesimplecloud.clientserverapi.lib.filetransfer.directory

import eu.thesimplecloud.clientserverapi.lib.filetransfer.packets.PacketIOCreateDirectory
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.directorywatch.IDirectoryWatch
import eu.thesimplecloud.clientserverapi.lib.directorywatch.IDirectoryWatchListener
import eu.thesimplecloud.clientserverapi.lib.directorywatch.IDirectoryWatchManager
import eu.thesimplecloud.clientserverapi.lib.filetransfer.packets.PacketIODeleteFile
import eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise.combineAllPromises
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.IOFileFilter
import java.io.File
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.StandardWatchEventKinds

class DirectorySync(private val directory: File, private val toDirectory: String, directoryWatchManager: IDirectoryWatchManager) : IDirectorySync {

    private val receivers = ArrayList<IConnection>()

    val directoryWatch: IDirectoryWatch = directoryWatchManager.createDirectoryWatch(directory)

    init {
        require(this.directory.isDirectory) { "Specified file must be a directory." }

        directoryWatch.addWatchListener(object : IDirectoryWatchListener {
            override fun fileCreated(file: File) {
                receivers.forEach { connection -> sendFileOrDirectory(file, connection) }
            }

            override fun fileModified(file: File) {
                receivers.forEach { connection -> sendFileOrDirectory(file, connection) }
            }

            override fun fileDeleted(file: File) {
                val otherSidePath = getFilePathOnOtherSide(file)
                receivers.forEach { connection -> connection.sendQuery(PacketIODeleteFile(otherSidePath)) }
            }

        })
    }

    override fun getDirectory(): File = this.directory

    override fun syncDirectory(connection: IConnection): ICommunicationPromise<Unit> {
        this.receivers.add(connection)
        val filePromises = getAllFiles().map { file -> sendFileOrDirectory(file, connection) }
        return filePromises.combineAllPromises()
    }

    private fun sendFileOrDirectory(file: File, connection: IConnection): ICommunicationPromise<Unit> {
        return if (file.isDirectory)
            connection.sendQuery(PacketIOCreateDirectory(getFilePathOnOtherSide(file)))
        else
            connection.sendFile(file, getFilePathOnOtherSide(file))
    }

    override fun syncNoLonger(connection: IConnection) {
        this.receivers.remove(connection)
    }

    private fun getAllFiles(): Collection<File> {
        val acceptAllFilter = object : IOFileFilter {
            override fun accept(file: File?): Boolean {
                return true
            }

            override fun accept(dir: File?, name: String?): Boolean {
                return true
            }
        }
        return FileUtils.listFilesAndDirs(directory, acceptAllFilter, acceptAllFilter)
    }

    private fun getFilePathOnOtherSide(file: File) = toDirectory + file.path.replace(this.directory.path, "")


}