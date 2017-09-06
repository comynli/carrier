package io.carrier.rpc

class Request(
        val id: String,
        val service: String,
        val method: String,
        val parameters: Array<Class<*>>,
        val arguments: Array<out Any>
)