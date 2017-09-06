package io.carrier.rpc.codec

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.carrier.rpc.Response
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import org.msgpack.jackson.dataformat.MessagePackFactory
import java.io.ByteArrayOutputStream

class ResponseEncoder : MessageToByteEncoder<Response>() {
    private val mapper = ObjectMapper(MessagePackFactory()).registerModule(KotlinModule())

    override fun encode(ctx: ChannelHandlerContext, msg: Response, out: ByteBuf) {
        val stream = ByteArrayOutputStream()
        mapper.writeValue(stream, msg)
        val data = stream.toByteArray()
        out.writeInt(data.size)
        out.writeBytes(data)
    }
}