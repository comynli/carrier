package io.carrier.rpc.codec

import io.carrier.rpc.Response
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import io.protostuff.LinkedBuffer
import io.protostuff.ProtostuffIOUtil
import io.protostuff.runtime.RuntimeSchema

class ResponseEncoder : MessageToByteEncoder<Response>() {
    private val schema = RuntimeSchema.createFrom(Response::class.java)

    override fun encode(ctx: ChannelHandlerContext, msg: Response, out: ByteBuf) {
        val buffer = LinkedBuffer.allocate()
        val data = ProtostuffIOUtil.toByteArray(msg, schema, buffer)
        out.writeInt(data.size)
        out.writeBytes(data)
    }
}