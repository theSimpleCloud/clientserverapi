package eu.thesimplecloud.clientserverapi.lib.bootstrap

interface IBootstrap {

    /**
     * Starts this bootstrap
     */
    fun start()

    /**
     * Stops this bootstrap
     */
    fun shutdown()

    /**
     * Returns whether this bootstrap was started
     */
    fun isActive(): Boolean

}