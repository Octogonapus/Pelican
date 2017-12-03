package com.pelican

import org.lwjgl.assimp.AIMaterial
import org.lwjgl.assimp.AIMesh
import org.lwjgl.assimp.AIScene
import org.lwjgl.assimp.Assimp.*
import java.io.File
import java.io.IOException
import java.util.*

/**
 * @author Ryan Benasutti
 * @author Zhang Hai
 * @since 05-24-2017
 * License terms: https://www.github.com/Octogonapus/Pelican/blob/master/LICENSE.md
 */
class Model(fileName: String) {
    var scene: AIScene = aiImportFile(
            File(javaClass.getResource(fileName).path).absolutePath,
            aiProcess_JoinIdenticalVertices
                    or aiProcess_Triangulate
                    or aiProcess_GenNormals
                    or aiProcess_CalcTangentSpace
                    or aiProcess_FlipUVs)

    var meshes: MutableList<Mesh>
    var materials: MutableList<Material>

    init {
        // Assimp will be able to find the corresponding mtl file if we call aiImportFile this way.

        val meshCount = scene.mNumMeshes()
        val meshesBuffer = scene.mMeshes()
        meshes = ArrayList()
        for (i in 0 until meshCount) {
            try {
                meshes.add(Mesh(AIMesh.create(meshesBuffer.get(i))))
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        val materialCount = scene.mNumMaterials()
        val materialsBuffer = scene.mMaterials()
        materials = ArrayList()
        for (i in 0 until materialCount) {
            materials.add(Material(AIMaterial.create(materialsBuffer.get(i))))
        }
    }

    protected fun finalize() {
//        super.finalize()
        aiReleaseImport(scene)
        println("finalize done")
//        scene = null
//        meshes = null
//        materials = null
    }
}
