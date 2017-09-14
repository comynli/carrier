package io.carrier.example.client

import com.google.inject.AbstractModule
import com.google.inject.Guice
import io.carrier.example.spi.HelloService
import io.carrier.rpc.client.Client
import io.carrier.rpc.ProviderRegistry
import io.carrier.rpc.Registry

class Application {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val injector = Guice.createInjector(object :AbstractModule() {
                override fun configure() {
                    bind(Registry::class.java).to(ProviderRegistry::class.java).asEagerSingleton()
                }
            })

            val registry = injector.getInstance(Registry::class.java) as ProviderRegistry
            registry.start()
            registry.register(HelloService::class.java.name, "127.0.0.1", 10052)
            val service = injector.getInstance(Client::class.java).create(HelloService::class.java)
            (0 until 10).forEach {
                println(service.sayHello("comyn"))
            }
            registry.shutdown()
        }
    }
}