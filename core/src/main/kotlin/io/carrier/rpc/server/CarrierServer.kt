package io.carrier.rpc.server

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Module
import com.google.inject.util.Modules
import io.carrier.rpc.server.http.HttpServer
import io.carrier.rpc.server.rpc.RpcServer
import java.net.URI

class CarrierServer(module: Module? = null, packages: List<String>) {
    constructor(module: Module, vararg packages: String) : this(module, packages.toList())
    constructor(vararg packages: String) : this(null, packages.toList())

    private val scan = ComponentScan(packages)
    private var httpServer: HttpServer? = null
    private var rpcServer: RpcServer? = null

    private val mod = object : AbstractModule() {
        @SuppressWarnings("unchecked")
        override fun configure() {
            scan.components.forEach {
                bind(it.first).to(it.second as Class<out Nothing>)
            }
        }
    }

    val injector = Guice.createInjector(module?.let { Modules.combine(it, mod) } ?: mod)

    fun rpc(uri: URI): CarrierServer {
        this.rpcServer = RpcServer(injector, uri)
        return this
    }

    fun http(uri: URI): CarrierServer {
        httpServer = HttpServer(injector, uri, scan)
        return this
    }

    fun start() {
        rpcServer?.start()
        httpServer?.start()
    }

    fun shutdown() {
        rpcServer?.shutdown()
        httpServer?.shutdown()
    }

    fun join() {
        rpcServer?.join()
        httpServer?.join()
    }
}