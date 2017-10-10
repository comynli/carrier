package io.carrier.example.spi.context

import io.carrier.rpc.context.Context

val TEST = Context.Key.of<String>("test")

val TEST2 = Context.Key.of<Long>("test2")