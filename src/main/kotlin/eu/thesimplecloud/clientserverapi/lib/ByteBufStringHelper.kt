package eu.thesimplecloud.clientserverapi.lib

import io.netty.buffer.ByteBuf
import java.nio.charset.StandardCharsets


class ByteBufStringHelper {
    companion object {
        fun nextString(byteBuf: ByteBuf): String {
            val bytes = ByteArray(byteBuf.readInt())
            byteBuf.readBytes(bytes)
            return String(bytes, StandardCharsets.UTF_8)
        }

        fun writeString(byteBuf: ByteBuf, string: String) {
            val bytes = string.toByteArray(StandardCharsets.UTF_8)
            byteBuf.writeInt(bytes.size)
            byteBuf.writeBytes(bytes)
        }

    }

}
