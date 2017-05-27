package com.pelican;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.ARBFragmentShader.GL_FRAGMENT_SHADER_ARB;
import static org.lwjgl.opengl.ARBShaderObjects.*;
import static org.lwjgl.opengl.ARBVertexShader.GL_VERTEX_SHADER_ARB;
import static org.lwjgl.opengl.ARBVertexShader.glEnableVertexAttribArrayARB;
import static org.lwjgl.opengl.ARBVertexShader.glGetAttribLocationARB;
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
    private Vector3f dir, right, pos;
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

            dir = new Vector3f();
            right = new Vector3f();
            pos = new Vector3f(0, 0, 0);

            GL.createCapabilities();
        } //Stack frame is automatically popped

        glClearColor(0.97f, 0.97f, 0.97f, 1.0f);
//        glFrontFace(GL_CW);
//        glCullFace(GL_BACK);
//        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
        glEnable(GL_DEPTH_CLAMP);
//        glEnable(GL_TEXTURE_2D);

        program = glCreateProgramObjectARB();
        int vs = Shader.createShader("/shader/basic_vertex.vs", GL_VERTEX_SHADER_ARB),
                fs = Shader.createShader("/shader/basic_fragment.fs", GL_FRAGMENT_SHADER_ARB);
        glAttachObjectARB(program, vs);
        glAttachObjectARB(program, fs);
        glLinkProgramARB(program);

        glEnableVertexAttribArrayARB(glGetAttribLocationARB(program, "aVertex"));
        glEnableVertexAttribArrayARB(glGetAttribLocationARB(program, "aNormal"));

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

    private long lastTime = System.nanoTime();

    protected void render(int[] windowDims, float[] mousePos) {
        float dt = (float) ((System.nanoTime() - lastTime) / 1E9);
        float move = dt * 0.01f;

        cameraMat.positiveZ(dir).negate().mul(move);
        dir.y = 0;
        cameraMat.positiveX(right).mul(move);

        if (KeyboardHandler.get(GLFW_KEY_W)) {
            pos.add(dir);
        }
        if (KeyboardHandler.get(GLFW_KEY_S)) {
            pos.sub(dir);
        }
        if (KeyboardHandler.get(GLFW_KEY_A)) {
            pos.sub(right);
        }
        if (KeyboardHandler.get(GLFW_KEY_D)) {
            pos.add(right);
        }

        //The rotation should be x->y, y->x
        camRotX = mousePos[1];
        camRotY = mousePos[0];

        glMatrixMode(GL_PROJECTION);
        cameraMat.setPerspective((float) Math.toRadians(45), (float) windowDims[0] / windowDims[1], 0.01f, 100.0f).get(cameraBuf);

        glMatrixMode(GL_MODELVIEW);
        cameraMat.identity()
                 .rotateX(camRotX)
                 .rotateY(camRotY)
                 .translate(-pos.x, -pos.y, -pos.z);
        glLoadMatrixf(cameraMat.get(cameraBuf));

        glViewport(0, 0, windowDims[0], windowDims[1]);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        entities.forEach(entity -> {
            entity.render(program);
        });
//        renderGrid();

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
