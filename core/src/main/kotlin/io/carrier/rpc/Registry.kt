package io.carrier.rpc

import io.carrier.rpc.client.ClientHandler

interface Registry {
    fun pick(service: String): ClientHandler
    fun shutdown()
    fun start()
}