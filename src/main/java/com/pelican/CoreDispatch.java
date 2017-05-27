package com.pelican;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author Ryan Benasutti
 * @since 05-24-2017
 * License terms: https://www.github.com/Octogonapus/Pelican/blob/master/LICENSE.md
 */
public class CoreDispatch {
    private int[] windowDims = new int[2];
    private float[] mousePos = new float[2];
    private boolean isRunning;
    private double frameTime;
    private long windowHandle;

    private RenderingEngine renderingEngine;

    private GLFWErrorCallback errorCallback;
    private GLFWFramebufferSizeCallback fbCallback;
    private GLFWCursorPosCallback cpCallback;

    public CoreDispatch(int width, int height, double frameCap) {
        windowDims[0] = width;
        windowDims[1] = height;
        isRunning = false;
        frameTime = 1.0 / frameCap;

        //Error callback to print to error stream
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

        //Init GLFW
        if (!glfwInit()) {
            throw new Error("Unable to initialize GLFW!");
        }

        //Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        //Create the window
        windowHandle = glfwCreateWindow(width, height, "Pelican", NULL, NULL);
        if (windowHandle == NULL) {
            throw new Error("Failed to create the GLFW Window!");
        }

        //Keyboard key press and release handler
        KeyboardHandler.init(windowHandle);

        glfwSetFramebufferSizeCallback(windowHandle, fbCallback = new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                if (width > 0 && height > 0) {
                    windowDims[0] = width;
                    windowDims[1] = height;
                }
            }
        });

        glfwSetCursorPosCallback(windowHandle, cpCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                mousePos[0] = (float) xpos / windowDims[0];
                mousePos[1] = (float) ypos / windowDims[1];
            }
        });

        //New frame on thread stack
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1),
                    pHeight = stack.mallocInt(1);

            glfwGetWindowSize(windowHandle, pWidth, pHeight);
            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(windowHandle,
                             (vidMode.width() - pWidth.get(0)) / 2,
                             (vidMode.height() - pHeight.get(0)) / 2);
        } //Stack frame is automatically popped

        //Use the window as our new context
        glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        glfwMakeContextCurrent(windowHandle);
        glfwSwapInterval(0);
        glfwShowWindow(windowHandle);
    }

    public void start(RenderingEngine renderingEngine) {
        if (isRunning) {
            return;
        }

        isRunning = true;
        this.renderingEngine = renderingEngine;
        loop();
    }

    public void stop() {
        isRunning = false;
    }

    private void loop() {
        int frames = 0;
        long frameCounter = 0;

        double lastTime = glfwGetTime(),
                unprocessedTime = 0;

        boolean render;
        double startTime, passedTime;
        while (isRunning) {
            render = false;

            startTime = glfwGetTime();
            passedTime = startTime - lastTime;
            lastTime = startTime;

            unprocessedTime += passedTime;
            frameCounter += passedTime;

            while (unprocessedTime > frameTime) {
                render = true;
                unprocessedTime -= frameTime;

                //poll input
                glfwPollEvents();

                if (glfwWindowShouldClose(windowHandle)) {
                    isRunning = false;
                }

                if (frameCounter >= 1.0) {
                    frames = 0;
                    frameCounter = 0;
                }
            }

            if (render) {
                //render new frame
                renderingEngine.render();
                frames++;
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public long getWindowHandle() {
        return windowHandle;
    }
}
