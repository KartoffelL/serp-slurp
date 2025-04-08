#version 450 core

layout(location = 0) in vec2 tex_coords;

layout(location = 0) out vec4 outColor;

layout(binding = 0) uniform sampler2D texSamplr1;
layout(binding = 1) uniform sampler2D texSamplr2;

void main() {
	vec4 tcol = texture(texSamplr1, tex_coords);
	outColor = vec4(tcol.rg, 1, 1);
}