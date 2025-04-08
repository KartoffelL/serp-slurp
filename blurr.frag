#version 450 core

layout(location = 0) in vec2 tex_coords;

layout(location = 0) out vec4 outColor;

layout(binding = 0) uniform sampler2D texSamplr;

void main() {
	vec4 tcol = texture(texSamplr, tex_coords);
	outColor = tcol;
}