package com.pelican;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static com.pelican.IOUtil.ioResourceToByteBuffer;
import static org.lwjgl.opengl.ARBShaderObjects.*;

/**
 * @author Ryan Benasutti
 * @author Zhang Hai
 * @since 05-24-2017
 * License terms: https://www.github.com/Octogonapus/Pelican/blob/master/LICENSE.md
 */
public class Shader {
    private static Shader ourInstance;

    private Shader() {
    }

    public static Shader getInstance() {
        if (ourInstance == null) {
            ourInstance = new Shader();
        }

        return ourInstance;
    }

    protected static int createShader(String resource, int type) throws IOException {
        int shader = glCreateShaderObjectARB(type);
        ByteBuffer source = ioResourceToByteBuffer(new File(getInstance().getClass()
                                                                         .getResource(resource)
                                                                         .getPath())
                                                           .getAbsolutePath(),
                                                   1024);
        PointerBuffer strings = BufferUtils.createPointerBuffer(1);
        IntBuffer lengths = BufferUtils.createIntBuffer(1);
        strings.put(0, source);
        lengths.put(0, source.remaining());

        glShaderSourceARB(shader, strings, lengths);
        glCompileShaderARB(shader);
        int compiled = glGetObjectParameteriARB(shader, GL_OBJECT_COMPILE_STATUS_ARB);

        String shaderLog = glGetInfoLogARB(shader);
        if (shaderLog.trim().length() > 0) {
            System.err.println(shaderLog);
        }
        if (compiled == 0) {
            throw new AssertionError("Could not compile shader");
        }

        return shader;
    }
}
