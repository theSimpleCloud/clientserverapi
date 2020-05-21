package eu.thesimplecloud.clientserverapi.directorysync

class DirectorySyncLoopTest {

    /*

    var nettyServer = NettyServer<TestConnectedClientValue>("127.0.0.1", 1919)
    var nettyClient = NettyClient("127.0.0.1", 1919)

    @Test
    fun test() {
        GlobalScope.launch {
            nettyServer.start()
        }
        while (!nettyServer.isListening()) {
            Thread.sleep(10)
        }
        GlobalScope.launch {
            nettyClient.start()
        }

        while (!nettyClient.isOpen()) {
            Thread.sleep(10)
        }
        Thread.sleep(1000)
        val dir = File("templates/")
        dir.mkdirs()
        nettyServer.getDirectorySyncManager().setTmpZipDirectory(File("storage/zippedTemplates/"))
        val directorySync = nettyServer.getDirectorySyncManager().createDirectorySync(dir, "templatesOtherSide/")
        val clientOnServerSide = this.nettyServer.clientManager.getClients().firstOrNull()!!
        val promise = directorySync.syncDirectory(clientOnServerSide)
        promise.awaitUninterruptibly()
        promise.cause()?.let { throw it }
        println("finished")

        Thread.sleep(3000)
    }

    */

}