package io.carrier.rpc.codec

import io.carrier.rpc.Request
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import io.protostuff.LinkedBuffer
import io.protostuff.ProtostuffIOUtil
import io.protostuff.runtime.RuntimeSchema

class RequestEncoder : MessageToByteEncoder<Request>() {
    private val schema = RuntimeSchema.createFrom(Request::class.java)

    override fun encode(ctx: ChannelHandlerContext, msg: Request, out: ByteBuf) {
        val buffer = LinkedBuffer.allocate()
        val data = ProtostuffIOUtil.toByteArray(msg, schema, buffer)
        out.writeInt(data.size)
        out.writeBytes(data)
    }
}