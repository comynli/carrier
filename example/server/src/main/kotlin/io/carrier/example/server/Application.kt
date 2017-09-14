package io.carrier.example.server

import io.carrier.rpc.server.CarrierServer
import java.net.URI

class Application {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val server = CarrierServer(Application::class.java.`package`.name, "io.carrier.example.spi")

            server.rpc(URI("rpc://0.0.0.0:10052")).http(URI("http://0.0.0.0:8080"))
            server.start()
            Runtime.getRuntime().addShutdownHook(Thread({ server.shutdown() }))
            server.join()
        }
    }
}