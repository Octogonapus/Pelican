package com.pelican;

/**
 * @author Ryan Benasutti
 * @since 05-24-2017
 * License terms: https://www.github.com/Octogonapus/Pelican/blob/master/LICENSE.md
 */
public class Main {
    public static void main(String[] args) {
        CoreDispatch core = new CoreDispatch(480, 360, 9999);
        core.start();
    }
}
