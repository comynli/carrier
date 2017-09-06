package io.carrier.rpc.server

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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Server @Inject constructor(private val injector: Injector) {
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
                                .addLast(ServerHandler(injector))

                    }
                })
                //.option(ChannelOption.SO_BACKLOG, 2048)
                //.childOption(ChannelOption.SO_KEEPALIVE, true)
    }

    fun start(port: Int) {
        future = bootstrap.bind(port).sync()
    }

    fun await() {
        future?.await()
    }

    fun shutdown() {
        future?.channel()?.closeFuture()?.sync()
    }
}