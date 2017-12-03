#version 330 core

layout (location = 0) in vec4 aVertex;
layout (location = 1) in vec3 aNormal;
layout (location = 2) in vec2 aTexCoord;

uniform mat4 model;
uniform mat4 viewProj;
uniform mat3 normal;

out vec3 vPosition;
out vec3 vNormal;
out vec2 TexCoords;

void main() {
    vec4 modelPosition = model * aVertex;
    gl_Position = viewProj * modelPosition;
    vPosition = modelPosition.xyz;
    vNormal = normal * aNormal;
    TexCoords = aTexCoord;
}