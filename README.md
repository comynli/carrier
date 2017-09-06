## define service interface

```kotlin
package io.carrier.example.spi

import io.carrier.rpc.Service

interface HelloService: Service {
    fun sayHello(name: String): String
}
```

## implement service

```kotlin
package io.carrier.example.server.impl

import io.carrier.example.spi.HelloService
import io.carrier.rpc.Export
import javax.inject.Singleton

@Singleton
@Export(HelloService::class)
class HelloServiceImpl : HelloService {
    override fun sayHello(name: String): String {
        return "hello $name"
    }
}
```

## start server

```kotlin
@JvmStatic
fun main(args: Array<String>) {
    val injector = Guice.createInjector(ServerModule(Application::class.java.`package`.name))
    val server = injector.getInstance(Server::class.java)

    server.start(8080)
    Runtime.getRuntime().addShutdownHook(Thread({ server.shutdown() }))
    server.await()
}
```

## client call

```kotlin
@JvmStatic
fun main(args: Array<String>) {
    val injector = Guice.createInjector(object :AbstractModule() {
        override fun configure() {
            bind(Registry::class.java).to(ProviderRegistry::class.java).asEagerSingleton()
        }
    })

    val registry = injector.getInstance(Registry::class.java) as ProviderRegistry
    registry.start()
    registry.register(HelloService::class.java.name, "127.0.0.1", 8080)
    val service = injector.getInstance(Client::class.java).create(HelloService::class.java)
    (0 until 10).forEach {
        println(service.sayHello("comyn"))
    }
    registry.shutdown()
}
```
