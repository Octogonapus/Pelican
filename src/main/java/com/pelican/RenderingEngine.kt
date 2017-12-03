package com.pelican

import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.ARBFragmentShader.GL_FRAGMENT_SHADER_ARB
import org.lwjgl.opengl.ARBShaderObjects.*
import org.lwjgl.opengl.ARBVertexShader.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL32.GL_DEPTH_CLAMP
import org.lwjgl.system.MemoryStack
import java.io.IOException
import java.nio.FloatBuffer
import java.util.*

/**
 * @author Ryan Benasutti
 * @author Zhang Hai
 * @since 05-24-2017
 * License terms: https://www.github.com/Octogonapus/Pelican/blob/master/LICENSE.md
 */
class RenderingEngine @Throws(IOException::class)
constructor(private val windowHandle: Long, private val fov: Double) {
    private val program: Int
    private val entities: MutableList<Entity>

    private var cameraMat: Matrix4f? = null
    private var cameraBuf: FloatBuffer? = null
    private var dir: Vector3f? = null
    private var right: Vector3f? = null
    private var pos: Vector3f? = null
    private var camRotX = 0f
    private var camRotY = 0f

    private val lastTime = System.nanoTime()

    private val projMatBuf = BufferUtils.createFloatBuffer(16)
    private val viewProjMatBuf = BufferUtils.createFloatBuffer(16)
    private val projMat = Matrix4f(projMatBuf)
    private val viewProjMat = Matrix4f(viewProjMatBuf)

    init {
        entities = ArrayList()

        //New frame on thread stack
        MemoryStack.stackPush().use { stack ->
            val pWidth = stack.mallocInt(1)
            val pHeight = stack.mallocInt(1)
            glfwGetWindowSize(windowHandle, pWidth, pHeight)

            cameraBuf = BufferUtils.createFloatBuffer(16)
            cameraMat = Matrix4f(cameraBuf!!)

            dir = Vector3f()
            right = Vector3f()
            pos = Vector3f(0f, 0f, 0f)

            GL.createCapabilities()
        } //Stack frame is automatically popped

        glClearColor(0.97f, 0.97f, 0.97f, 1.0f)
        //        glFrontFace(GL_CW);
        //        glCullFace(GL_BACK);
        //        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_LESS)
        glEnable(GL_DEPTH_CLAMP)
        //        glEnable(GL_TEXTURE_2D);

        program = glCreateProgramObjectARB()
        val vs = Shader.createShader("/shader/basic_vertex.vs", GL_VERTEX_SHADER_ARB)
        val fs = Shader.createShader("/shader/basic_fragment.fs", GL_FRAGMENT_SHADER_ARB)
        glAttachObjectARB(program, vs)
        glAttachObjectARB(program, fs)
        glLinkProgramARB(program)

        glEnableVertexAttribArrayARB(glGetAttribLocationARB(program, "aVertex"))
        glEnableVertexAttribArrayARB(glGetAttribLocationARB(program, "aNormal"))

        val linkStatus = glGetObjectParameteriARB(program, GL_OBJECT_LINK_STATUS_ARB)
        val programLog = glGetInfoLogARB(program)
        if (programLog.trim { it <= ' ' }.isNotEmpty()) {
            System.err.println(programLog)
        }
        if (linkStatus == 0) {
            throw AssertionError("Failed to link program")
        }

        glUseProgramObjectARB(program)
    }

    fun addEntity(entity: Entity) {
        entities.add(entity)
    }

    fun render(windowDims: IntArray, mousePos: FloatArray) {
        val dt = ((System.nanoTime() - lastTime) / 1E9).toFloat()
        val move = dt * 0.05f

        cameraMat!!.positiveZ(dir!!).negate().mul(move)
        dir!!.y = 0f
        cameraMat!!.positiveX(right!!).mul(move)

        if (KeyboardHandler[GLFW_KEY_W]) {
            pos!!.add(dir!!)
        }
        if (KeyboardHandler[GLFW_KEY_S]) {
            pos!!.sub(dir!!)
        }
        if (KeyboardHandler[GLFW_KEY_A]) {
            pos!!.sub(right!!)
        }
        if (KeyboardHandler[GLFW_KEY_D]) {
            pos!!.add(right!!)
        }

        //The rotation should be x->y, y->x
        camRotX = mousePos[1]
        camRotY = mousePos[0]

        projMat.setPerspective(Math.toRadians(fov).toFloat(), windowDims[0].toFloat() / windowDims[1], 0.01f, 100.0f).get(projMatBuf)

        cameraMat!!.identity()
                .rotateX(camRotX)
                .rotateY(camRotY)
                .translate(-pos!!.x, -pos!!.y, -pos!!.z)

        glUniformMatrix4fvARB(glGetUniformLocationARB(program, "viewProj"), false, projMat.mul(cameraMat, viewProjMat).get(viewProjMatBuf))

        glViewport(0, 0, windowDims[0], windowDims[1])
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        entities.forEach { entity -> entity.render(program, pos!!) }

        glfwSwapBuffers(windowHandle)
    }
}
