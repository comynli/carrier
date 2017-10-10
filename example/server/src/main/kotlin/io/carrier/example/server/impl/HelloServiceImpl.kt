package io.carrier.example.server.impl

import io.carrier.example.spi.HelloService
import io.carrier.example.spi.context.TEST
import io.carrier.rpc.Export
import io.carrier.rpc.context.Context
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Singleton
@Export(HelloService::class)
class HelloServiceImpl : HelloService {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun sayHello(name: String): String {
        logger.info(TEST.get())
        return "hello $name"
    }
}