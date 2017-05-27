#version 330 core

layout (location = 0) in vec4 aVertex;
layout (location = 1) in vec3 aNormal;

uniform mat4 model;
uniform mat4 viewProj;
uniform mat3 normal;

out vec3 vPosition;
out vec3 vNormal;

void main() {
    vec4 modelPosition = model * aVertex;
    gl_Position = viewProj * modelPosition;
    vPosition = modelPosition.xyz;
    vNormal = normal * aNormal;
}