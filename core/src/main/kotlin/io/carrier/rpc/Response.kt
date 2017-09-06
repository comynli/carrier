package io.carrier.rpc

class Response (
        val id: String,
        val result: Any? = null,
        val exception: Exception? = null
)