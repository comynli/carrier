package io.carrier.example.spi

import io.carrier.rpc.Service
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
interface HelloService: Service {
    @GET
    @Path("/say/{name}")
    fun sayHello(@PathParam("name") name: String): String
}