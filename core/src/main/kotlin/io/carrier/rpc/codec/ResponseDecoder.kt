package io.carrier.rpc.codec

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import io.carrier.rpc.Response
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import org.msgpack.jackson.dataformat.MessagePackFactory

class ResponseDecoder : ByteToMessageDecoder() {
    private val mapper = ObjectMapper(MessagePackFactory()).registerModule(KotlinModule())

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
        val response = mapper.readValue<Response>(data)
        out.add(response)
    }
}