#version 450 core

layout(location = 0) in vec2 vertices;
layout(location = 1) in vec2 textures;
layout(location = 2) in vec4 color;


layout(location = 0) out vec2 tex_coords;
layout(location = 1) out vec4 vert_color;

layout (push_constant) uniform PushConstants {
    vec2 a;
} pushConstants;

const float worldWidth = 16;
const float worldHeight = 9;

void main() {
	tex_coords = textures;
	vert_color = color;
	gl_Position = vec4(vec2(vertices.x*pushConstants.a.x-vertices.y*pushConstants.a.y, vertices.y*pushConstants.a.x+vertices.x*pushConstants.a.y)/vec2(worldWidth, worldHeight), 0, 1);
}
