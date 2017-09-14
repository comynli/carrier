package io.carrier.rpc.server.rpc

import com.google.inject.Injector
import io.carrier.rpc.codec.RequestDecoder
import io.carrier.rpc.codec.ResponseEncoder
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import java.net.URI

class RpcServer(private val injector: Injector, private val uri: URI) {
    private val DEFAULT_PORT = 10052

    private val boss = NioEventLoopGroup()
    private val worker = NioEventLoopGroup()
    private val bootstrap = ServerBootstrap()
    private var future: ChannelFuture? = null


    init {
        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel) {
                        ch.pipeline()
                                .addLast(RequestDecoder())
                                .addLast(ResponseEncoder())
                                .addLast(Handler(injector))

                    }
                })
        .option(ChannelOption.SO_BACKLOG, 2048)
        .childOption(ChannelOption.SO_KEEPALIVE, true)
    }

    fun start() {
        val port = if (uri.port < 0) DEFAULT_PORT else uri.port
        future = bootstrap.bind(uri.host, port)
    }

    fun join() {
        future?.await()
    }

    fun shutdown() {
        future?.channel()?.closeFuture()?.sync()
    }
}