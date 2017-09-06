package io.carrier.rpc.client

import com.google.inject.Injector
import io.carrier.rpc.Request
import io.carrier.rpc.Response
import io.carrier.rpc.codec.RequestEncoder
import io.carrier.rpc.codec.ResponseDecoder
import io.netty.bootstrap.Bootstrap
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

class ClientHandler(host: String, port: Int) : SimpleChannelInboundHandler<Response>() {
    private val futures = ConcurrentHashMap<String, CompletableFuture<Response>>()
    private val group = NioEventLoopGroup()
    private val bootstrap = Bootstrap()
    private val future: ChannelFuture

    init {
        future = bootstrap.group(group)
                .channel(NioSocketChannel::class.java)
                .handler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel) {
                        val pipeline = ch.pipeline()
                        pipeline.addLast(RequestEncoder())
                        pipeline.addLast(ResponseDecoder())
                        pipeline.addLast(this@ClientHandler)
                    }
                })
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .connect(host, port).sync()
    }


    override fun channelRead0(ctx: ChannelHandlerContext?, msg: Response) {
        futures.remove(msg.id)?.complete(msg)

    }

    fun call(request: Request): CompletableFuture<Response> {
        futures[request.id] = CompletableFuture()
        future.channel().writeAndFlush(request)
        return futures[request.id]!!
    }

    fun shutdown() {
        future.channel().close()
        group.shutdownGracefully()
    }
}