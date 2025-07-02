#type vertex
#version 330 core
// layouts are used to specify variable location in gpu buffer array efficiently
layout (location=0) in vec3 aPos; // a stands for attribute
layout (location=1) in vec4 aColor;
layout (location=2) in vec2 aTexCoords;

// uniform are non chaning attributed that remains same over a buffer until changed so that we dont send them again and again
uniform mat4 uProjection;
uniform mat4 uView;

// export variable so that below shader can use this
out vec4 fColor; // f stand for fragment
out vec2 fTexCoords;

// main funtion
void main() {
    fColor = aColor;
    gl_Position = uProjection * uView *  vec4(aPos, 1.0);
    fTexCoords = aTexCoords;
}


#type fragment
#version 330 core

// take in variable so that above shader can send them
in vec4 fColor;
in vec2 fTexCoords;

uniform float uTime;
uniform sampler2D TEX_SAMPLER;

// since fragment are color pipeline color is expected from fragment
out vec4 color;

void main() {
//    float avg = (fColor.r + fColor.g + fColor.b) / 3;
//    float noise = fract(sin(dot(fColor.xy, vec2(12.9898, 78.233))) * 43758.5453); // noise
//    color = sin(uTime) * fColor; // pulse b/w black and color
//    color = vec4(avg, avg, avg, 1); // make black and white
    color = texture(TEX_SAMPLER, fTexCoords);
}