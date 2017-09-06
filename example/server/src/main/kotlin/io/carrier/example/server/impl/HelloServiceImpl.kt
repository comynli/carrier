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