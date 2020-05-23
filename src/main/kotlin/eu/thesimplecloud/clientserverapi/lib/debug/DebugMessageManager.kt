package eu.thesimplecloud.clientserverapi.lib.debug

import java.util.concurrent.CopyOnWriteArraySet

class DebugMessageManager : IDebugMessageManager {

    private val activeDebugMessages = CopyOnWriteArraySet<DebugMessage>()

    override fun isActive(debugMessage: DebugMessage): Boolean {
        return this.activeDebugMessages.contains(debugMessage)
    }

    override fun enable(debugMessage: DebugMessage) {
        this.activeDebugMessages.add(debugMessage)
    }

    override fun disable(debugMessage: DebugMessage) {
        this.activeDebugMessages.remove(debugMessage)
    }
}