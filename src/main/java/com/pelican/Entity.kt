package com.pelican

import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.ARBShaderObjects.*
import org.lwjgl.opengl.ARBVertexBufferObject.*
import org.lwjgl.opengl.ARBVertexShader.glGetAttribLocationARB
import org.lwjgl.opengl.ARBVertexShader.glVertexAttribPointerARB
import org.lwjgl.opengl.GL11.*

/**
 * @author Ryan Benasutti
 * @since 05-24-2017
 * License terms: https://www.github.com/Octogonapus/Pelican/blob/master/LICENSE.md
 */
class Entity(private var model: Model) {

    private val modelMatrix = Matrix4f().rotateY(0.5f * Math.PI.toFloat()).scale(1.5f, 1.5f, 1.5f)
    private val lightPosition = Vector3f(-5f, 5f, 5f)

    private val modelMatrixBuffer = BufferUtils.createFloatBuffer(4 * 4)
    private val normalMatrix = Matrix3f()
    private val normalMatrixBuffer = BufferUtils.createFloatBuffer(3 * 3)
    private val lightPositionBuffer = BufferUtils.createFloatBuffer(3)
    private val viewPositionBuffer = BufferUtils.createFloatBuffer(3)

    fun loadModelFromFile(fileName: String): Entity {
        model = Model(fileName)
        return this
    }

    fun render(program: Int, viewPos: Vector3f) {
        for (mesh in model.meshes) {
            glBindBufferARB(GL_ARRAY_BUFFER_ARB, mesh.vertexArrayBuffer)
            glVertexAttribPointerARB(
                    glGetAttribLocationARB(program, "aVertex"),
                    3,
                    GL_FLOAT,
                    false,
                    0,
                    0)

            glBindBufferARB(GL_ARRAY_BUFFER_ARB, mesh.normalArrayBuffer)
            glVertexAttribPointerARB(
                    glGetAttribLocationARB(program, "aNormal"),
                    3,
                    GL_FLOAT,
                    false,
                    0,
                    0)

            glUniformMatrix4fvARB(
                    glGetUniformLocationARB(program, "model"),
                    false,
                    modelMatrix.get(modelMatrixBuffer))

            normalMatrix.set(modelMatrix).invert().transpose()
            glUniformMatrix3fvARB(
                    glGetUniformLocationARB(program, "normal"),
                    false,
                    normalMatrix.get(normalMatrixBuffer))
            glUniform3fvARB(
                    glGetUniformLocationARB(program, "uLightPosition"),
                    lightPosition.get(lightPositionBuffer))
            glUniform3fvARB(
                    glGetUniformLocationARB(program, "uViewPosition"),
                    viewPos.get(viewPositionBuffer))

            val material = model.materials[mesh.mesh.mMaterialIndex()]
            nglUniform3fvARB(
                    glGetUniformLocationARB(program, "material.ambient"),
                    1,
                    material.mAmbientColor.address())
            nglUniform3fvARB(
                    glGetUniformLocationARB(program, "material.diffuse"),
                    1,
                    material.mDiffuseColor.address())
            nglUniform3fvARB(
                    glGetUniformLocationARB(program, "material.specular"),
                    1,
                    material.mSpecularColor.address())

            glBindBufferARB(GL_ELEMENT_ARRAY_BUFFER_ARB, mesh.elementArrayBuffer)
            glDrawElements(GL_TRIANGLES, mesh.elementCount, GL_UNSIGNED_INT, 0)
        }
    }
}
