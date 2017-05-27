package com.pelican;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.ARBFragmentShader.GL_FRAGMENT_SHADER_ARB;
import static org.lwjgl.opengl.ARBShaderObjects.*;
import static org.lwjgl.opengl.ARBVertexShader.GL_VERTEX_SHADER_ARB;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL32.GL_DEPTH_CLAMP;

/**
 * @author Ryan Benasutti
 * @author Zhang Hai
 * @since 05-24-2017
 * License terms: https://www.github.com/Octogonapus/Pelican/blob/master/LICENSE.md
 */
public class RenderingEngine {
    private long windowHandle;
    private int program;
    private List<Entity> entities;

    private Matrix4f cameraMat;
    private FloatBuffer cameraBuf;
    private float camPosX = 0, camPosY = 0, camPosZ = 0;
    private float camRotX = 0, camRotY = 0;

    public RenderingEngine(long windowHandle, float fov) throws IOException {
        this.windowHandle = windowHandle;
        entities = new ArrayList<>();

        //New frame on thread stack
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1),
                    pHeight = stack.mallocInt(1);
            glfwGetWindowSize(windowHandle, pWidth, pHeight);

            cameraBuf = BufferUtils.createFloatBuffer(16);
            cameraMat = new Matrix4f(cameraBuf);
            glMatrixMode(GL_PROJECTION);
            cameraMat.setPerspective((float) Math.toRadians(fov), (float) pWidth.get(0) / pHeight.get(0), 0.01f, 100.0f).get(cameraBuf);
        } //Stack frame is automatically popped

        GL.createCapabilities();

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glFrontFace(GL_CW);
        glCullFace(GL_BACK);
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_DEPTH_CLAMP);
        glEnable(GL_TEXTURE_2D);

        program = glCreateProgramObjectARB();
        int vs = Shader.createShader("/shader/basic_vertex.vs", GL_VERTEX_SHADER_ARB),
                fs = Shader.createShader("/shader/basic_frag.fs", GL_FRAGMENT_SHADER_ARB);
        glAttachObjectARB(program, vs);
        glAttachObjectARB(program, fs);
        glLinkProgramARB(program);

        int linkStatus = glGetObjectParameteriARB(program, GL_OBJECT_LINK_STATUS_ARB);
        String programLog = glGetInfoLogARB(program);
        if (programLog.trim().length() > 0) {
            System.err.println(programLog);
        }
        if (linkStatus == 0) {
            throw new AssertionError("Failed to link program");
        }

        glUseProgramObjectARB(program);
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    protected void render() {
        glMatrixMode(GL_MODELVIEW);
        cameraMat.identity()
                 .rotateX(camRotX)
                 .rotateY(camRotY)
                 .translate(-camPosX, -camPosY, -camPosZ);
        glLoadMatrixf(cameraMat.get(cameraBuf));

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

//        entities.forEach(entity -> {
//            entity.render(program);
//        });
        renderGrid();

        glfwSwapBuffers(windowHandle);
    }

    private int dl = -1;
    void renderGrid() {
        if (dl == -1) {
            dl = glGenLists(1);
            glNewList(dl, GL_COMPILE);
            glBegin(GL_LINES);
            glColor3f(0.2f, 0.2f, 0.2f);
            int gridSize = 40;
            for (int i = -gridSize; i <= gridSize; i++) {
                glVertex3f(-gridSize, 0.0f, i);
                glVertex3f(gridSize, 0.0f, i);
                glVertex3f(i, 0.0f, -gridSize);
                glVertex3f(i, 0.0f, gridSize);
            }
            glColor3f(0.5f, 0.5f, 0.5f);
            for (int i = -gridSize; i <= gridSize; i++) {
                float ceiling = 3.0f;
                glVertex3f(-gridSize, ceiling, i);
                glVertex3f(gridSize, ceiling, i);
                glVertex3f(i, ceiling, -gridSize);
                glVertex3f(i, ceiling, gridSize);
            }
            glEnd();
            glEndList();
        }
        glCallList(dl);
    }
}
