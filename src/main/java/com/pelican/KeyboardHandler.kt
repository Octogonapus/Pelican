package com.pelican

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWKeyCallback

/**
 * @author Ryan Benasutti
 * @since 05-27-2017
 * License terms: https://www.github.com/Octogonapus/Pelican/blob/master/LICENSE.md
 */
object KeyboardHandler {

    private val keyDown = BooleanArray(GLFW_KEY_LAST + 1)

    fun init(core: CoreDispatch) {
        glfwSetKeyCallback(core.windowHandle, object : GLFWKeyCallback() {
            override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                    core.stop()
                }

                keyDown[key] = action == GLFW_PRESS || action == GLFW_REPEAT
            }
        })
    }

    operator fun get(key: Int): Boolean {
        return keyDown[key]
    }

}
