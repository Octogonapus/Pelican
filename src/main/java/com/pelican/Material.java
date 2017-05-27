package com.pelican;

import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIMaterial;

import static org.lwjgl.assimp.Assimp.*;

/**
 * @author Ryan Benasutti
 * @since 05-24-2017
 */
public class Material {
    private AIMaterial mat;
    private AIColor4D ambient, diffuse, specular;

    public Material(AIMaterial material) {
        this.mat = material;

        ambient = AIColor4D.create();
        if (aiGetMaterialColor(mat, AI_MATKEY_COLOR_AMBIENT, aiTextureType_NONE, 0, ambient) != 0) {
            throw new IllegalStateException(aiGetErrorString());
        }

        diffuse = AIColor4D.create();
        if (aiGetMaterialColor(mat, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, diffuse) != 0) {
            throw new IllegalStateException(aiGetErrorString());
        }

        specular = AIColor4D.create();
        if (aiGetMaterialColor(mat, AI_MATKEY_COLOR_SPECULAR, aiTextureType_NONE, 0, specular) != 0) {
            throw new IllegalStateException(aiGetErrorString());
        }
    }
}
