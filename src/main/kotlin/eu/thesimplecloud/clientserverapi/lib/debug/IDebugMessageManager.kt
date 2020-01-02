package eu.thesimplecloud.clientserverapi.lib.debug

interface IDebugMessageManager {


    /**
     * Returns whether the specified [debugMessage] is active
     */
    fun isActive(debugMessage: DebugMessage): Boolean

    /**
     * Enables the specified [debugMessage]
     */
    fun enable(debugMessage: DebugMessage)

    /**
     * Disables the specified [debugMessage]
     */
    fun disable(debugMessage: DebugMessage)

}