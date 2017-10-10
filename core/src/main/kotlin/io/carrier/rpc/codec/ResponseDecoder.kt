package io.carrier.rpc.codec

import io.carrier.rpc.Response
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import io.protostuff.ProtostuffIOUtil
import io.protostuff.runtime.RuntimeSchema

class ResponseDecoder : ByteToMessageDecoder() {
    private val schema = RuntimeSchema.createFrom(Response::class.java)

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
        val response = Response()
        ProtostuffIOUtil.mergeFrom(data, response, schema)
        out.add(response)
    }
}