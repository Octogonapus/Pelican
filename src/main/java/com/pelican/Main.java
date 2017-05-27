package com.pelican;

import java.io.IOException;

/**
 * @author Ryan Benasutti
 * @since 05-24-2017
 * License terms: https://www.github.com/Octogonapus/Pelican/blob/master/LICENSE.md
 */
public class Main {
    public static void main(String[] args) {
        CoreDispatch core = new CoreDispatch(480, 360, 144);
        RenderingEngine renderingEngine;
        try {
            renderingEngine = new RenderingEngine(core.getWindowHandle(), 45);
            renderingEngine.addEntity(new Entity().loadModelFromFile("/model/test.stl"));
            core.start(renderingEngine);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
