package com.pelican;

import org.lwjgl.glfw.GLFWErrorCallback;
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
    private boolean isRunning;
    private double frameTime;
    private long windowHandle;
    private RenderingEngine renderingEngine;

    public CoreDispatch(int width, int height, double frameCap) {
        isRunning = false;
        frameTime = 1.0 / frameCap;

        //Error callback to print to error stream
        GLFWErrorCallback.createPrint(System.err).set();

        //Init GLFW
        if (!glfwInit()) {
            throw new Error("Unable to initialize GLFW!");
        }

        //Configure GLFW
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        //Create the window
        windowHandle = glfwCreateWindow(width, height, "Pelican", NULL, NULL);
        if (windowHandle == NULL) {
            throw new Error("Failed to create the GLFW Window!");
        }

        //Escape key callback to close window
        glfwSetKeyCallback(windowHandle, ((window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
            }
        }));

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
        glfwMakeContextCurrent(windowHandle);
        glfwSwapInterval(0);
        glfwShowWindow(windowHandle);

        //Init rendering engine
        renderingEngine = new RenderingEngine(windowHandle);
    }

    public void start() {
        if (isRunning) {
            return;
        }

        isRunning = true;
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

                //tick physics

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
}
