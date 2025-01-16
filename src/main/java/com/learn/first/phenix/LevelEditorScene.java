package com.learn.first.phenix;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;

public class LevelEditorScene extends Scene {

    private String vertexShaderSource = """
            #version 330 core
            layout (location=0) in vec3 aPos; // a stands for attribute
            layout (location=1) in vec4 aColor;
            
            out vec4 fColor; // f stand for fragment
            
            void main() {
                fColor = aColor;
                gl_Position = vec4(aPos, 1.0);
            }
            """;

    private String fragmentShaderSource = """
            #version 330 core
            
            in vec4 fColor;
            
            out vec4 color;
            
            void main() {
                color = fColor;
            }
            """;

    private int vertexId, fragementId, shaderProgram;

    private float[] vertexArr = {
            // position             // color
            -0.5f,  0.5f, 0.0f,       1.0f, 0.0f, 0.0f, 1.0f, // Top left     0
            -0.5f, -0.5f, 0.0f,       0.0f, 1.0f, 0.0f, 1.0f, // Bottom left  1
             0.5f,  0.5f, 0.0f,       0.0f, 0.0f, 1.0f, 1.0f, // Top right    2
             0.5f, -0.5f, 0.0f,       1.0f, 0.0f, 1.0f, 1.0f, // Bottom right 3

    };
    // IMPORTANT: Must be in counter-clockwise order
    private int[] elementArr = {
            3, 2, 0, // Top right triangle
            3, 1, 0  // bottom left triangle
    };

    public LevelEditorScene() {
    }

    private int vaoId, vboId, eboId;

    @Override
    public void init() {
        // Compile and link shader
        // load and compile the vertex shader
        vertexId = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexId, vertexShaderSource);
        glCompileShader(vertexId);

        // Check for errors in compilation
        int success = glGetShaderi(vertexId, GL_COMPILE_STATUS);
        if(success == GL_FALSE) {
            int len = glGetShaderi(vertexId, GL_INFO_LOG_LENGTH);
            System.out.println("Error: generating vertex shader");
            System.out.println(glGetShaderInfoLog(vertexId, len));
            assert false : "";
        }

        // load and compile the vertex shader
        fragementId = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragementId, fragmentShaderSource);
        glCompileShader(fragementId);

        // Check for errors in compilation
        success = glGetShaderi(fragementId, GL_COMPILE_STATUS);
        if(success == GL_FALSE) {
            int len = glGetShaderi(fragementId, GL_INFO_LOG_LENGTH);
            System.out.println("Error: generating fragment shader");
            System.out.println(glGetShaderInfoLog(fragementId, len));
            assert false : "";
        }

        // Link Shaders and check for errors
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexId);
        glAttachShader(shaderProgram, fragementId);
        glLinkProgram(shaderProgram);

        success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if(success == GL_FALSE) {
            int len = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
            System.out.println("Error: linking shaders");
            System.out.println(glGetProgramInfoLog(shaderProgram, len));
            assert false : "";
        }

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
        System.out.println((1.0f / dt) + " FPS");
        // Bind shader program
        glUseProgram(shaderProgram);
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
        glUseProgram(0);
    }
}
