package eu.thesimplecloud.clientserverapi.lib

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class LogFile {

    private val messages: MutableList<String> = ArrayList()

    companion object {

    }

    fun addMessage(msg: String) {
        this.messages.add(msg)
    }

    init {

        GlobalScope.launch {
            while (true) {
                delay(5000)
                val file = File("clientserverapilogs.txt")
                if (!file.exists())
                    file.createNewFile()
                file.appendText(messages.joinToString("\n"))
                messages.clear()
            }
        }
    }

}