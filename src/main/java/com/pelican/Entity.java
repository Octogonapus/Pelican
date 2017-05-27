package com.pelican;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.ARBShaderObjects.*;
import static org.lwjgl.opengl.ARBVertexBufferObject.*;
import static org.lwjgl.opengl.ARBVertexShader.glGetAttribLocationARB;
import static org.lwjgl.opengl.ARBVertexShader.glVertexAttribPointerARB;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author Ryan Benasutti
 * @since 05-24-2017
 * License terms: https://www.github.com/Octogonapus/Pelican/blob/master/LICENSE.md
 */
public class Entity {
    private Model model;

    Matrix4f modelMatrix = new Matrix4f().rotateY(0.5f * (float) Math.PI).scale(1.5f, 1.5f, 1.5f);
    Matrix4f viewProjectionMatrix = new Matrix4f();
    Vector3f lightPosition = new Vector3f(-5f, 5f, 5f);

    private FloatBuffer modelMatrixBuffer = BufferUtils.createFloatBuffer(4 * 4);
    private FloatBuffer viewProjectionMatrixBuffer = BufferUtils.createFloatBuffer(4 * 4);
    private Matrix3f normalMatrix = new Matrix3f();
    private FloatBuffer normalMatrixBuffer = BufferUtils.createFloatBuffer(3 * 3);
    private FloatBuffer lightPositionBuffer = BufferUtils.createFloatBuffer(3);
    private FloatBuffer viewPositionBuffer = BufferUtils.createFloatBuffer(3);

    public Entity() {
    }

    public Entity loadModelFromFile(String fileName) {
        model = new Model(fileName);
        return this;
    }

    protected void render(int program, Vector3f viewPos, Matrix4f viewMat, Matrix4f projMat) {
        projMat.mul(viewMat, viewProjectionMatrix);
        for (Mesh mesh : model.meshes) {
            glBindBufferARB(GL_ARRAY_BUFFER_ARB, mesh.vertexArrayBuffer);
            glVertexAttribPointerARB(glGetAttribLocationARB(program, "aVertex"), 3, GL_FLOAT, false, 0, 0);
            glBindBufferARB(GL_ARRAY_BUFFER_ARB, mesh.normalArrayBuffer);
            glVertexAttribPointerARB(glGetAttribLocationARB(program, "aNormal"), 3, GL_FLOAT, false, 0, 0);

            glUniformMatrix4fvARB(glGetUniformLocationARB(program, "uModelMatrix"), false, modelMatrix.get(modelMatrixBuffer));
            glUniformMatrix4fvARB(glGetUniformLocationARB(program, "uViewProjectionMatrix"), false,
                                  viewProjectionMatrix.get(viewProjectionMatrixBuffer));
            normalMatrix.set(modelMatrix).invert().transpose();
            glUniformMatrix3fvARB(glGetUniformLocationARB(program, "uNormalMatrix"), false, normalMatrix.get(normalMatrixBuffer));
            glUniform3fvARB(glGetUniformLocationARB(program, "uLightPosition"), lightPosition.get(lightPositionBuffer));
            glUniform3fvARB(glGetUniformLocationARB(program, "uViewPosition"), viewPos.get(viewPositionBuffer));

            Material material = model.materials.get(mesh.mesh.mMaterialIndex());
            nglUniform3fvARB(glGetUniformLocationARB(program, "uAmbientColor"), 1, material.mAmbientColor.address());
            nglUniform3fvARB(glGetUniformLocationARB(program, "uDiffuseColor"), 1, material.mDiffuseColor.address());
            nglUniform3fvARB(glGetUniformLocationARB(program, "uSpecularColor"), 1, material.mSpecularColor.address());

            glBindBufferARB(GL_ELEMENT_ARRAY_BUFFER_ARB, mesh.elementArrayBuffer);
            glDrawElements(GL_TRIANGLES, mesh.elementCount, GL_UNSIGNED_INT, 0);
        }
    }
}
