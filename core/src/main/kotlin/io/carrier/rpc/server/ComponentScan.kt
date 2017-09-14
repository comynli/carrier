package io.carrier.rpc.server

import io.carrier.rpc.Export
import org.reflections.Reflections

class ComponentScan(val packages: List<String>) {
    constructor(vararg packages: String) : this(packages.toList())

    val components = packages.map {
        Reflections(it).getTypesAnnotatedWith(Export::class.java).map {
            val service = it.getAnnotation(Export::class.java).value.java
            service to it
        }
    }.flatten()
}