package io.carrier.rpc.server.http

import com.google.inject.Injector
import org.glassfish.hk2.api.ServiceLocator
import org.jvnet.hk2.guice.bridge.api.GuiceBridge
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge
import javax.inject.Inject
import javax.ws.rs.container.*
import javax.ws.rs.ext.Provider

@Provider
@PreMatching
class ApplyGuiceContextFilter @Inject constructor(locator: ServiceLocator,
                                                  injector: Injector): ContainerRequestFilter, ContainerResponseFilter {
    init {
        GuiceBridge.getGuiceBridge().initializeGuiceBridge(locator)
        locator.getService(GuiceIntoHK2Bridge::class.java).bridgeGuiceInjector(injector)
    }

    override fun filter(requestContext: ContainerRequestContext?) {
    }

    override fun filter(requestContext: ContainerRequestContext?, responseContext: ContainerResponseContext?) {
    }
}