package io.carrier.example.spi

import io.carrier.rpc.Service

interface HelloService: Service {
    fun sayHello(name: String): String
}