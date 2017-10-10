package io.carrier.rpc.codec

import io.carrier.rpc.Request
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import io.protostuff.ProtostuffIOUtil
import io.protostuff.runtime.RuntimeSchema

class RequestDecoder: ByteToMessageDecoder() {
    private val schema = RuntimeSchema.createFrom(Request::class.java)

    override fun decode(ctx: ChannelHandlerContext, input: ByteBuf, out: MutableList<Any>) {
        if (input.readableBytes() < 4) {
            return
        }
        input.markReaderIndex()
        val length = input.readInt()
        if (input.readableBytes() < length) {
            input.resetReaderIndex()
            return
        }
        val data = ByteArray(length)
        input.readBytes(data)
        val request = Request()
        ProtostuffIOUtil.mergeFrom(data, request, schema)
        out.add(request)
    }
}