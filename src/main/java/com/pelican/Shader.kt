package com.pelican

import com.pelican.IOUtil.ioResourceToByteBuffer
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.ARBShaderObjects.*
import java.io.File
import java.io.IOException

/**
 * @author Ryan Benasutti
 * @author Zhang Hai
 * @since 05-24-2017
 * License terms: https://www.github.com/Octogonapus/Pelican/blob/master/LICENSE.md
 */
object Shader {

    @Throws(IOException::class)
    internal fun createShader(resource: String, type: Int): Int {
        val shader = glCreateShaderObjectARB(type)
        val source = ioResourceToByteBuffer(File(Shader.javaClass
                .getResource(resource)
                .path)
                .absolutePath,
                1024)
        val strings = BufferUtils.createPointerBuffer(1)
        val lengths = BufferUtils.createIntBuffer(1)
        strings.put(0, source)
        lengths.put(0, source.remaining())

        glShaderSourceARB(shader, strings, lengths)
        glCompileShaderARB(shader)
        val compiled = glGetObjectParameteriARB(shader, GL_OBJECT_COMPILE_STATUS_ARB)

        val shaderLog = glGetInfoLogARB(shader)
        if (shaderLog.trim { it <= ' ' }.isNotEmpty()) {
            System.err.println(shaderLog)
        }
        if (compiled == 0) {
            throw AssertionError("Could not compile shader")
        }

        return shader
    }

}
