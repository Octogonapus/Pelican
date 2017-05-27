package com.pelican;

import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.ARBVertexBufferObject.*;

/**
 * @author Ryan Benasutti
 * @since 05-24-2017
 * License terms: https://www.github.com/Octogonapus/Pelican/blob/master/LICENSE.md
 */
public class Mesh {
    private AIMesh mesh;
    private int posVB, texVB, normVB, tanVB, indVB;
    private int elementCount;

    public Mesh(AIMesh mesh) {
        this.mesh = mesh;

        posVB = glGenBuffersARB();
        glBindBufferARB(GL_ARRAY_BUFFER_ARB, posVB);
        AIVector3D.Buffer vertices = mesh.mVertices();
        nglBufferDataARB(GL_ARRAY_BUFFER_ARB, AIVector3D.SIZEOF * vertices.remaining(), vertices.address(), GL_STATIC_DRAW_ARB);

//        texVB = glGenBuffersARB();
//        glBindBufferARB(GL_ARRAY_BUFFER_ARB, texVB);
//        AIVector3D.Buffer textureCoords = mesh.mTextureCoords()

        normVB = glGenBuffersARB();
        glBindBufferARB(GL_ARRAY_BUFFER_ARB, normVB);
        AIVector3D.Buffer normals = mesh.mNormals();
        nglBufferDataARB(GL_ARRAY_BUFFER_ARB, AIVector3D.SIZEOF * normals.remaining(), normals.address(), GL_STATIC_DRAW_ARB);

        tanVB = glGenBuffersARB();
        glBindBufferARB(GL_ARRAY_BUFFER_ARB, tanVB);
        AIVector3D.Buffer tangents = mesh.mTangents();
        nglBufferDataARB(GL_ARRAY_BUFFER_ARB, AIVector3D.SIZEOF * tangents.remaining(), tangents.address(), GL_STATIC_DRAW_ARB);

        final int faceCount = mesh.mNumFaces();
        elementCount = faceCount * 3;
        IntBuffer elemABD = MemoryUtil.memAllocInt(elementCount); //BufferUtils.createIntBuffer(elementCount);
        AIFace.Buffer faceBuf = mesh.mFaces();
        for (int i = 0; i < faceCount; i++) {
            AIFace face = faceBuf.get(i);
            if (face.mNumIndices() != 3) {
                throw new IllegalStateException("AIFace.mNumIndicies != 3");
            }
            elemABD.put(face.mIndices());
        }
        elemABD.flip();
        indVB = glGenBuffersARB();
        glBindBufferARB(GL_ELEMENT_ARRAY_BUFFER_ARB, indVB);
        glBufferDataARB(GL_ELEMENT_ARRAY_BUFFER_ARB, elemABD, GL_STATIC_DRAW_ARB);
    }

    public AIMesh getMesh() {
        return mesh;
    }

    public int getPosVB() {
        return posVB;
    }

    public int getTexVB() {
        return texVB;
    }

    public int getNormVB() {
        return normVB;
    }

    public int getTanVB() {
        return tanVB;
    }

    public int getIndVB() {
        return indVB;
    }

    public int getElementCount() {
        return elementCount;
    }
}
