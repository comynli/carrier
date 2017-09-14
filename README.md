## define service interface

```kotlin
package io.carrier.example.spi

import io.carrier.rpc.Service
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
interface HelloService: Service {
    @GET
    @Path("/say/{name}")
    fun sayHello(@PathParam("name") name: String): String
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
```

## client call

```kotlin
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
```

## http call
```bash
curl http://127.0.0.1:8080/say/comyn
```