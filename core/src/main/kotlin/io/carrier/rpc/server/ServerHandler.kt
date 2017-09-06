package io.carrier.rpc.server

import com.google.inject.Injector
import io.carrier.rpc.Request
import io.carrier.rpc.Response
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import net.sf.cglib.reflect.FastClass

class ServerHandler(private val injector: Injector): SimpleChannelInboundHandler<Request>() {
    override fun channelRead0(ctx: ChannelHandlerContext, msg: Request) {
        val response = try {
            Response(msg.id, handle(msg))
        } catch (e: Exception) {
            Response(msg.id, null, e)
        }
        ctx.writeAndFlush(response)
    }

    private fun handle(request: Request): Any {
        val clazz = Class.forName(request.service)
        val service = injector.getInstance(clazz)
        val method = FastClass.create(clazz).getMethod(request.method, request.parameters)
        return method.invoke(service, request.arguments)
    }
}