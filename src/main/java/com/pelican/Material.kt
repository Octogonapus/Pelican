package com.pelican

import org.lwjgl.assimp.AIColor4D
import org.lwjgl.assimp.AIMaterial
import org.lwjgl.assimp.Assimp.*

/**
 * @author Ryan Benasutti
 * @since 05-24-2017
 * License terms: https://www.github.com/Octogonapus/Pelican/blob/master/LICENSE.md
 */
class Material(mMaterial: AIMaterial) {
    var mAmbientColor: AIColor4D
    var mDiffuseColor: AIColor4D
    var mSpecularColor: AIColor4D

    init {

        val numProps = mMaterial.mNumProperties()
        val buf = mMaterial.mProperties()
        for (i in 0 until numProps) {
            println(buf.getStringASCII(i))
        }

        mAmbientColor = AIColor4D.create()
        if (aiGetMaterialColor(mMaterial, AI_MATKEY_COLOR_AMBIENT,
                aiTextureType_NONE, 0, mAmbientColor) != 0) {
            throw IllegalStateException(aiGetErrorString())
        }
        mDiffuseColor = AIColor4D.create()
        if (aiGetMaterialColor(mMaterial, AI_MATKEY_COLOR_DIFFUSE,
                aiTextureType_NONE, 0, mDiffuseColor) != 0) {
            throw IllegalStateException(aiGetErrorString())
        }
        mSpecularColor = AIColor4D.create()
        if (aiGetMaterialColor(mMaterial, AI_MATKEY_COLOR_SPECULAR,
                aiTextureType_NONE, 0, mSpecularColor) != 0) {
            throw IllegalStateException(aiGetErrorString())
        }
    }

}
