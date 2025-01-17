package com.learn.first.renderer;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;

public class Shader {

    private int shaderProgramId;
    private String vertexSource;
    private String fragmentSource;
    private final String filePath;
    private boolean beingUsed = false;

    public Shader(String filePath){
        this.filePath = filePath;
        try {
            String source = new String(Files.readAllBytes(Paths.get(filePath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            int index = source.indexOf("#type") + 6;
            int eol = source.indexOf("\n", index);
            String firstPattern = source.substring(index, eol).trim();

            index = source.indexOf("#type", eol) + 6;
            eol = source.indexOf("\n", index);
            String secondPattern = source.substring(index, eol).trim();

            if(firstPattern.equals("vertex")) {
                vertexSource = splitString[1];
            } else if(firstPattern.equals("fragment")) {
                fragmentSource = splitString[1];
            } else {
                throw new IOException("Unexpected token '" + firstPattern + "'");
            }

            if(secondPattern.equals("vertex")) {
                vertexSource = splitString[2];
            } else if(secondPattern.equals("fragment")) {
                fragmentSource = splitString[2];
            } else {
                throw new IOException("Unexpected token '" + secondPattern + "'");
            }
        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Error: cannot open file for shader <" + this.filePath + ">";
        }
    }

    /*
     * Compile and link the vertex and fragment shaders
     */
    public void compile() {
        int vertexId, fragmentId;
        // Compile and link shader
        // load and compile the vertex shader
        vertexId = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexId, vertexSource);
        glCompileShader(vertexId);

        // Check for errors in compilation
        int success = glGetShaderi(vertexId, GL_COMPILE_STATUS);
        if(success == GL_FALSE) {
            int len = glGetShaderi(vertexId, GL_INFO_LOG_LENGTH);
            System.out.println("Error: compiling vertex shader" + this.filePath);
            System.out.println(glGetShaderInfoLog(vertexId, len));
            assert false : "";
        }

        // load and compile the vertex shader
        fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentId, fragmentSource);
        glCompileShader(fragmentId);

        // Check for errors in compilation
        success = glGetShaderi(fragmentId, GL_COMPILE_STATUS);
        if(success == GL_FALSE) {
            int len = glGetShaderi(fragmentId, GL_INFO_LOG_LENGTH);
            System.out.println("Error: compiling fragment shader" + this.filePath);
            System.out.println(glGetShaderInfoLog(fragmentId, len));
            assert false : "";
        }

        // Link Shaders and check for errors
        shaderProgramId = glCreateProgram();
        glAttachShader(shaderProgramId, vertexId);
        glAttachShader(shaderProgramId, fragmentId);
        glLinkProgram(shaderProgramId);

        success = glGetProgrami(shaderProgramId, GL_LINK_STATUS);
        if(success == GL_FALSE) {
            int len = glGetProgrami(shaderProgramId, GL_INFO_LOG_LENGTH);
            System.out.println("Error: linking shaders" + this.filePath);
            System.out.println(glGetProgramInfoLog(shaderProgramId, len));
            assert false : "";
        }
    }

    public void use() {
        if(!beingUsed) {
            // Bind shader program
            glUseProgram(shaderProgramId);
            beingUsed = true;
        }
    }
    public void detach() {
        glUseProgram(0);
        beingUsed = false;
    }

    public void uploadMat4f(String name, Matrix4f mat4) {
        int varLocation = glGetUniformLocation(shaderProgramId, name);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    public void uploadMat3f(String name, Matrix3f mat3) {
        int varLocation = glGetUniformLocation(shaderProgramId, name);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        mat3.get(matBuffer);
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }

    public void uploadVec4f(String name, Vector4f vec) {
        int varLocation = glGetUniformLocation(shaderProgramId, name);
        use();
        glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
    }

    public void uploadVec3f(String name, Vector3f vec) {
        int varLocation = glGetUniformLocation(shaderProgramId, name);
        use();
        glUniform3f(varLocation, vec.x, vec.y, vec.z);
    }

    public void uploadVec2f(String name, Vector2f vec) {
        int varLocation = glGetUniformLocation(shaderProgramId, name);
        use();
        glUniform2f(varLocation, vec.x, vec.y);
    }

    public void uploadFloat(String name, float val) {
        int varLocation = glGetUniformLocation(shaderProgramId, name);
        use();
        glUniform1f(varLocation, val);
    }
    public void uploadInt(String name, int val) {
        int varLocation = glGetUniformLocation(shaderProgramId, name);
        use();
        glUniform1i(varLocation, val);
    }
}
