package com.pelican;

/**
 * @author Ryan Benasutti
 * @since 05-24-2017
 * License terms: https://www.github.com/Octogonapus/Pelican/blob/master/LICENSE.md
 */
public class Entity {
    private Model model;

    public Entity() {
    }

    public Entity loadModelFromFile(String fileName) {
        model = new Model(fileName);
        return this;
    }

    protected void render(int program) {
//        model.render(program);
    }
}
