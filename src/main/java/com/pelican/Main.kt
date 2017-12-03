package com.pelican

import java.io.IOException

/**
 * @author Ryan Benasutti
 * @since 05-24-2017
 * License terms: https://www.github.com/Octogonapus/Pelican/blob/master/LICENSE.md
 */
object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        val core = CoreDispatch(480, 360, 144.0)
        val renderingEngine: RenderingEngine
        try {
            renderingEngine = RenderingEngine(core.windowHandle, 45.0)
            renderingEngine.addEntity(Entity(Model("/model/nanosuit/nanosuit.obj")))
            core.start(renderingEngine)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

}
