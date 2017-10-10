package io.carrier.example.client

import com.google.inject.AbstractModule
import com.google.inject.Guice
import io.carrier.example.spi.HelloService
import io.carrier.example.spi.context.TEST
import io.carrier.example.spi.context.TEST2
import io.carrier.rpc.client.Client
import io.carrier.rpc.ProviderRegistry
import io.carrier.rpc.Registry
import io.carrier.rpc.context.Context

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
            Context.current().withValue(TEST, "test").run {
                println(service.sayHello("comynli"))
            }

//            Context.current().withValue(TEST2, 123456L).run {
//                println(service.sayHello("comyn"))
//            }

            registry.shutdown()
        }
    }
}