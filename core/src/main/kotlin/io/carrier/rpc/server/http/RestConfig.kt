package io.carrier.rpc.server.http

import com.google.inject.Injector
import io.carrier.rpc.Service
import io.carrier.rpc.server.ComponentScan
import org.glassfish.hk2.utilities.binding.AbstractBinder
import org.glassfish.jersey.server.ResourceConfig

class RestConfig(private val injector: Injector, scan: ComponentScan) : ResourceConfig() {
    init {
        //packages(*scan.packages.toTypedArray())
        register(ApplyGuiceContextFilter::class.java)
        scan.components.forEach {
            registerClasses(it.first)
        }

        register(object : AbstractBinder() {
            override fun configure() {
                bind(injector).to(Injector::class.java)
                scan.components.forEach {
                    println(it.first)
                    println(injector.getInstance(it.first))
                    bind(injector.getInstance(it.first)).to(it.first as Class<in Service>)
                }
            }
        })
    }
}