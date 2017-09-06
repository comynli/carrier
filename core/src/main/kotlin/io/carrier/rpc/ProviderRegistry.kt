package io.carrier.rpc

import io.carrier.rpc.client.ClientHandler
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Singleton

@Singleton
class ProviderRegistry : Registry {
    private val services = ConcurrentHashMap<String, ClientHandler>()

    override fun pick(service: String): ClientHandler {
        return services[service]!!
    }

    override fun shutdown() {
        services.values.forEach { it.shutdown() }
    }

    override fun start() {
    }

    fun register(service: String, host: String, port: Int) {
        services[service] = ClientHandler(host, port)
    }
}