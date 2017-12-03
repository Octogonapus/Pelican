package com.pelican

import org.lwjgl.BufferUtils.createByteBuffer
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Taken from LWJGL util demo
 * @author Spasi
 * @author kappaOne
 * @author apostolos
 */
object IOUtil {

    private fun resizeBuffer(buffer: ByteBuffer, newCapacity: Int): ByteBuffer {
        val newBuffer = createByteBuffer(newCapacity)
        buffer.flip()
        newBuffer.put(buffer)
        return newBuffer
    }

    /**
     * Reads the specified resource and returns the raw data as a ByteBuffer.
     *
     * @param resource   the resource to read
     * @param bufferSize the initial buffer size
     * @return the resource data
     * @throws IOException if an IO error occurs
     */
    @Throws(IOException::class)
    fun ioResourceToByteBuffer(resource: String, bufferSize: Int): ByteBuffer {
        val path = Paths.get(resource)

        if (Files.isReadable(path)) {
            Files.newByteChannel(path).use { fc ->
                val buffer = createByteBuffer(fc.size().toInt() + 1)
                while (fc.read(buffer) != -1) {
                }
                buffer.flip()
                return buffer
            }
        } else {
            IOUtil::class.java.classLoader.getResourceAsStream(resource).use({ source ->
                Channels.newChannel(source).use({ rbc ->
                    var buffer = createByteBuffer(bufferSize)

                    while (true) {
                        val bytes = rbc.read(buffer)
                        if (bytes == -1) {
                            break
                        }
                        if (buffer.remaining() == 0) {
                            buffer = resizeBuffer(buffer, buffer.capacity() * 2)
                        }
                    }

                    buffer.flip()
                    return buffer
                })
            })
        }
    }

}
