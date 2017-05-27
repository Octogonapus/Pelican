package com.pelican;

import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Ryan Benasutti
 * @since 05-27-2017
 */
public class KeyboardHandler {
    private static boolean[] keyDown = new boolean[GLFW_KEY_LAST + 1];
    private static GLFWKeyCallback keyCallback;

    static void init(long windowHandle) {
        glfwSetKeyCallback(windowHandle, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                {
                    glfwSetWindowShouldClose(windowHandle, true);
                }

                keyDown[key] = action == GLFW_PRESS || action == GLFW_RELEASE;
            }
        });
    }

    static boolean get(int key) {
        return keyDown[key];
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        keyCallback.free();
    }
}
