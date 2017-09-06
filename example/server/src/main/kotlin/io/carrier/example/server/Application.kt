package io.carrier.example.server

import com.google.inject.Guice
import io.carrier.rpc.server.Server
import io.carrier.rpc.server.ServerModule

class Application {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val injector = Guice.createInjector(ServerModule(Application::class.java.`package`.name))
            val server = injector.getInstance(Server::class.java)

            server.start(8080)
            Runtime.getRuntime().addShutdownHook(Thread({ server.shutdown() }))
            server.await()
        }
    }
}