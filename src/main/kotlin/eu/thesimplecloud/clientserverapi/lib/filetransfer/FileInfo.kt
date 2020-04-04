package eu.thesimplecloud.clientserverapi.lib.filetransfer

import java.io.File

data class FileInfo(val relativePath: String, val lastModified: Long) {

    companion object {
        fun fromFile(file: File): FileInfo {
            return FileInfo(file.path, file.lastModified())
        }
    }

}


