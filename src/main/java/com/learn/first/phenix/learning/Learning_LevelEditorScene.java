package com.learn.first.phenix.learning;

import com.learn.first.phenix.Camera;
import com.learn.first.phenix.Scene;
import com.learn.first.renderer.Shader;
import com.learn.first.util.Time;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;

public class Learning_LevelEditorScene extends Scene {
    private Shader defaultShader;

    private final float[] vertexArr = {
            // position             // color
            -100.0f,  100.0f, 0.0f,       1.0f, 0.0f, 0.0f, 1.0f, // Top left     0
            -100.0f, -100.0f, 0.0f,       0.0f, 1.0f, 0.0f, 1.0f, // Bottom left  1
             100.0f,  100.0f, 0.0f,       0.0f, 0.0f, 1.0f, 1.0f, // Top right    2
             100.0f, -100.0f, 0.0f,       1.0f, 0.0f, 1.0f, 1.0f, // Bottom right 3
    };
    // IMPORTANT: Must be in counter-clockwise order
    private final int[] elementArr = {
            3, 2, 0, // Top right triangle
            1, 3, 0  // bottom left triangle
    };

    public Learning_LevelEditorScene() {
    }

    private int vaoId, vboId, eboId;

    @Override
    public void init() {
        this.camera = new Camera(new Vector2f());
        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compile();
        // Generate VAO, VBO and EBO buffer objects and send to GPU
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArr.length);
        vertexBuffer.put(vertexArr).flip();

        // Create VBO, upload the vertex buffer
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Create the indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArr.length);
        elementBuffer.put(elementArr).flip();

        eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Add the vertex attribute pointers
        int positionsSize = 3;
        int colorSize = 4;
        int floatSizeBytes = 4;
        int vertexSizeBytes = (positionsSize + colorSize) * floatSizeBytes;

        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * floatSizeBytes);
        glEnableVertexAttribArray(1);

    }

    @Override
    public void update(float dt) {
        camera.position.x -= dt * 50.0f;
        camera.position.y -= dt * 20.0f;
        System.out.println((1.0f / dt) + " FPS");
        // Bind shader program
        defaultShader.use();
        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
        defaultShader.uploadFloat("uTime", Time.getTime());
        // Bind the vao
        glBindVertexArray(vaoId);

        // enable the vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArr.length, GL_UNSIGNED_INT, 0);

        // unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);
        defaultShader.detach();
    }
}
