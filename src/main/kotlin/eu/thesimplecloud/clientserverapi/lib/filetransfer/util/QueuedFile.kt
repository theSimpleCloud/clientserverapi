package eu.thesimplecloud.clientserverapi.lib.filetransfer.util

import eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise.ICommunicationPromise
import java.io.File

class QueuedFile(val file: File, val savePath: String, val promise: ICommunicationPromise<Unit>) {
}