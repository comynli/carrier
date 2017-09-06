package io.carrier.rpc.server

import com.google.inject.AbstractModule
import io.carrier.rpc.Export
import org.reflections.Reflections

class ServerModule(private val packages: List<String>): AbstractModule() {
    constructor(vararg packages: String): this(packages.toList())

    @SuppressWarnings("unchecked")
    override fun configure() {
        packages.forEach {
            Reflections(it).getTypesAnnotatedWith(Export::class.java).forEach {
                val service = it.getAnnotation(Export::class.java).value.java
                bind(service).to(it as Class<out Nothing>)
            }
        }
    }
}