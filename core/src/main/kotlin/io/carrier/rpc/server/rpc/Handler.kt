package io.carrier.rpc.server.rpc

import com.google.inject.Injector
import io.carrier.rpc.Request
import io.carrier.rpc.Response
import io.carrier.rpc.context.Context
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import net.sf.cglib.reflect.FastClass

class Handler(private val injector: Injector): SimpleChannelInboundHandler<Request>() {
    override fun channelRead0(ctx: ChannelHandlerContext, msg: Request) {
        val response = try {
            Response(msg.id, handle(msg))
        } catch (e: Exception) {
            Response(msg.id, null, e)
        }
        ctx.writeAndFlush(response)
    }

    private fun handle(request: Request): Any {
        Context.attach(request.context)
        val clazz = Class.forName(request.service)
        val service = injector.getInstance(clazz)
        val method = FastClass.create(clazz).getMethod(request.method, request.parameters)
        return method.invoke(service, request.arguments)
    }
}