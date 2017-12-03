package com.pelican

import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWCursorPosCallback
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWFramebufferSizeCallback
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil.NULL

/**
 * @author Ryan Benasutti
 * @since 05-24-2017
 * License terms: https://www.github.com/Octogonapus/Pelican/blob/master/LICENSE.md
 */
class CoreDispatch(width: Int, height: Int, frameCap: Double) {
    private var isRunning: Boolean = false
    private val frameTime: Double
    val windowHandle: Long

    private var renderingEngine: RenderingEngine? = null

    private var windowDims = IntArray(2)
    private var mousePos = FloatArray(2)

    init {
        windowDims[0] = width
        windowDims[1] = height
        isRunning = false
        frameTime = 1.0 / frameCap

        //Error callback to print to error stream
        glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err))

        //Init GLFW
        if (!glfwInit()) {
            throw Error("Unable to initialize GLFW!")
        }

        //Configure GLFW
        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)

        //Create the window
        windowHandle = glfwCreateWindow(width, height, "Pelican", NULL, NULL)
        if (windowHandle == NULL) {
            throw Error("Failed to create the GLFW Window!")
        }

        //Keyboard key press and release handler
        KeyboardHandler.init(this)

        glfwSetFramebufferSizeCallback(windowHandle, object : GLFWFramebufferSizeCallback() {
            override fun invoke(window: Long, width: Int, height: Int) {
                if (width > 0 && height > 0) {
                    windowDims[0] = width
                    windowDims[1] = height
                }
            }
        })

        glfwSetCursorPosCallback(windowHandle, object : GLFWCursorPosCallback() {
            override fun invoke(window: Long, xpos: Double, ypos: Double) {
                mousePos[0] = xpos.toFloat() / windowDims[0]
                mousePos[1] = ypos.toFloat() / windowDims[1]
            }
        })

        //New frame on thread stack
        MemoryStack.stackPush().use { stack ->
            val pWidth = stack.mallocInt(1)
            val pHeight = stack.mallocInt(1)

            glfwGetWindowSize(windowHandle, pWidth, pHeight)
            val vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor())
            glfwSetWindowPos(windowHandle,
                    (vidMode.width() - pWidth.get(0)) / 2,
                    (vidMode.height() - pHeight.get(0)) / 2)
        } //Stack frame is automatically popped

        //Use the window as our new context
        glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_DISABLED)
        glfwMakeContextCurrent(windowHandle)
        glfwSwapInterval(0)
        glfwShowWindow(windowHandle)
    }

    fun start(renderingEngine: RenderingEngine) {
        if (isRunning) {
            return
        }

        isRunning = true
        this.renderingEngine = renderingEngine
        loop()
    }

    fun stop() {
        isRunning = false
    }

    private fun loop() {
        var frames = 0
        var frameCounter: Long = 0

        var lastTime = glfwGetTime()
        var unprocessedTime = 0.0
        var render: Boolean
        var startTime: Double
        var passedTime: Double

        while (isRunning) {
            render = false

            startTime = glfwGetTime()
            passedTime = startTime - lastTime
            lastTime = startTime

            unprocessedTime += passedTime
            frameCounter += passedTime.toLong()

            while (unprocessedTime > frameTime) {
                render = true
                unprocessedTime -= frameTime

                //poll input
                glfwPollEvents()

                if (glfwWindowShouldClose(windowHandle)) {
                    isRunning = false
                }

                if (frameCounter >= 1.0) {
                    frames = 0
                    frameCounter = 0
                }
            }

            if (render) {
                //render new frame
                renderingEngine!!.render(windowDims, mousePos, frameTime.toFloat() * 200)
                frames++
            } else {
                try {
                    Thread.sleep(1)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }
        }

        glfwFreeCallbacks(windowHandle)
        glfwDestroyWindow(windowHandle)
        glfwTerminate()
        glfwSetErrorCallback(null).free()
    }

}
