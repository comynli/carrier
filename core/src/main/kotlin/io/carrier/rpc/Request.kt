package io.carrier.rpc

import io.carrier.rpc.context.Context

class Request(
        var id: String,
        var service: String,
        var method: String,
        var parameters: Array<Class<*>>,
        var arguments: Array<out Any>,
        var context: Context) {
    constructor() : this("", "", "", emptyArray(), emptyArray(), Context())
}