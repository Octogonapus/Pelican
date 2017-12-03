#version 330 core

out vec4 fragColor;

in vec2 TexCoords;

uniform sampler2D texture_diffuse1;

struct Material {
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float shininess;
};

struct Light {
    vec3 position;
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

in vec3 vPosition;
in vec3 vNormal;

uniform vec3 viewPos;
uniform Material material;
uniform Light light;

void main() {
    vec3 ambient = light.ambient * material.ambient;

    vec3 norm = normalize(vNormal);
    vec3 lightDir = normalize(light.position - vPosition);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = light.diffuse * (diff * material.diffuse);

    vec3 viewDir = normalize(viewPos - vPosition);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    vec3 specular = light.specular * (spec * material.specular);

    vec3 result = ambient + diffuse + specular;
    fragColor = texture(texture_diffuse1, TexCoords) + vec4(1.0f,1.0f,1.0f,1.0f)*0.3; //vec4(result, 1.0f);
}