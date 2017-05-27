package com.pelican;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;

import java.io.File;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.*;

/**
 * @author Ryan Benasutti
 * @author Zhang Hai
 * @since 05-24-2017
 * License terms: https://www.github.com/Octogonapus/Pelican/blob/master/LICENSE.md
 */
public class Model {
    private AIScene scene;
    private List<Mesh> meshes;
    private List<Material> materials;
    private Matrix4f modelMat;
    private FloatBuffer modelMatBuffer;

    public Model(String fileName) {
        scene = aiImportFile(new File(fileName).getAbsolutePath(), aiProcess_JoinIdenticalVertices |
                                                                   aiProcess_Triangulate |
                                                                   aiProcess_GenSmoothNormals |
                                                                   aiProcess_FlipUVs |
                                                                   aiProcess_CalcTangentSpace);
        if (scene == null) {
            throw new IllegalStateException("Failed to load entity from file:\n" + aiGetErrorString());
        }

        int meshCount = scene.mNumMeshes();
        meshes = new ArrayList<>(meshCount);
        PointerBuffer meshBuf = scene.mMeshes();
        for (int i = 0; i < meshCount; i++) {
            meshes.add(new Mesh(AIMesh.create(meshBuf.get(i))));
        }

        int matCount = scene.mNumMaterials();
        materials = new ArrayList<>(matCount);
        PointerBuffer matBuf = scene.mMaterials();
        for (int i = 0; i < matCount; i++) {
            materials.add(new Material(AIMaterial.create(matBuf.get(i))));
        }

        modelMat = new Matrix4f().rotateY(0.5f * (float) Math.PI).scale(1.5f, 1.5f, 1.5f);
        modelMatBuffer = BufferUtils.createFloatBuffer(16);
    }

    protected void render(int program) {
        meshes.forEach(mesh -> {
//            glBindBufferARB(GL_ARRAY_BUFFER_ARB, mesh.getPosVB());
//            glVertexAttribPointerARB(glGetAttribLocationARB(program, "aVertex"), 3, GL_FLOAT, false, 0, 0);
//            glBindBufferARB(GL_ARRAY_BUFFER_ARB, mesh.getNormVB());
//            glVertexAttribPointerARB(glGetAttribLocationARB(program, "aNormal"), 3, GL_FLOAT, false, 0, 0);
//
//            glUniformMatrix4fvARB(glGetUniformLocationARB(program, "uModelMatrix"), false, modelMat.get(modelMatBuffer));
//            glUniformMatrix4fvARB(glGetUniformLocationARB(program, "uViewProjectionMatrix"), false, camera.getViewProjMatBuf());
        });
    }
}
