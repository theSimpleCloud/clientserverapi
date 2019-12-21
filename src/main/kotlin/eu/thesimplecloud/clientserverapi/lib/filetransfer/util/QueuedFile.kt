package eu.thesimplecloud.clientserverapi.lib.filetransfer.util

import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.io.File

class QueuedFile(val file: File, val savePath: String, val promise: ICommunicationPromise<Unit>) {
}