package com.pelican

import org.lwjgl.BufferUtils
import org.lwjgl.assimp.AIMesh
import org.lwjgl.assimp.AIVector3D
import org.lwjgl.opengl.ARBVertexBufferObject.*

/**
 * @author Ryan Benasutti
 * @since 05-24-2017
 * License terms: https://www.github.com/Octogonapus/Pelican/blob/master/LICENSE.md
 */
class Mesh(val mesh: AIMesh) {
    var vertexArrayBuffer: Int = 0
    var normalArrayBuffer: Int = 0
    var textureBuffer: Int = 0
    var elementArrayBuffer: Int = 0
    var elementCount: Int = 0

    init {

        vertexArrayBuffer = glGenBuffersARB()
        glBindBufferARB(GL_ARRAY_BUFFER_ARB, vertexArrayBuffer)
        val vertices = mesh.mVertices()
        nglBufferDataARB(GL_ARRAY_BUFFER_ARB, (AIVector3D.SIZEOF * vertices.remaining()).toLong(),
                vertices.address(), GL_STATIC_DRAW_ARB)

        normalArrayBuffer = glGenBuffersARB()
        glBindBufferARB(GL_ARRAY_BUFFER_ARB, normalArrayBuffer)
        val normals = mesh.mNormals()
        nglBufferDataARB(GL_ARRAY_BUFFER_ARB, (AIVector3D.SIZEOF * normals.remaining()).toLong(),
                normals.address(), GL_STATIC_DRAW_ARB)

        val faceCount = mesh.mNumFaces()
        elementCount = faceCount * 3
        val elementArrayBufferData = BufferUtils.createIntBuffer(elementCount)
        val facesBuffer = mesh.mFaces()
        for (i in 0 until faceCount) {
            val face = facesBuffer.get(i)
            if (face.mNumIndices() != 3) {
                throw IllegalStateException("AIFace.mNumIndices() != 3")
            }
            elementArrayBufferData.put(face.mIndices())
        }
        elementArrayBufferData.flip()
        elementArrayBuffer = glGenBuffersARB()
        glBindBufferARB(GL_ELEMENT_ARRAY_BUFFER_ARB, elementArrayBuffer)
        glBufferDataARB(GL_ELEMENT_ARRAY_BUFFER_ARB, elementArrayBufferData,
                GL_STATIC_DRAW_ARB)
    }
}
