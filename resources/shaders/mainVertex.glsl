#version 410

layout (location = 0) in vec3 inposition;
layout (location = 1) in vec3 color;
layout (location = 2) in vec2 textureCoord;
layout (location = 3) in vec3 vnormal;

out vec3 mvVertexPos;
out vec3 mvVertexNormal;
out vec3 passColor;
out vec2 passTextureCoord;

uniform mat4 u_ModelMatrix;
uniform mat4 view;
uniform mat4 projection;
uniform vec3 camerapos;

void main() {
	vec4 mvPos = u_ModelMatrix * vec4(inposition, 1.0);
	mvVertexPos = mvPos.xyz;
	passColor = color;
	passTextureCoord = textureCoord;
	gl_Position = projection * view * u_ModelMatrix * vec4(inposition, 1.0f);
	mvVertexNormal = normalize(u_ModelMatrix * vec4(vnormal, 0.0)).xyz;
}