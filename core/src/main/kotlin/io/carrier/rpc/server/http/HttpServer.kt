package io.carrier.rpc.server.http

import com.google.inject.Injector
import io.carrier.rpc.server.ComponentScan
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import java.net.URI

class HttpServer(injector: Injector, uri: URI, scan: ComponentScan) {
    private val config = RestConfig(injector, scan)
    private val server = GrizzlyHttpServerFactory.createHttpServer(uri, config, false)

    fun start() {
        server.start()
    }

    fun shutdown() {
        server.shutdown()
    }

    fun join() {
        Thread.currentThread().join()
    }
}