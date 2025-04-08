#version 450 core

layout(location = 0) in vec2 tex_coords;
layout(location = 1) in vec4 vert_color;

layout(location = 0) out vec4 outColor;

layout(binding = 0) uniform sampler2D texSamplr;

layout (binding = 0, set =1, std140) uniform MatrixBlock
{
  vec4 color;
};

void main() {
	vec4 tcol = texture(texSamplr, tex_coords);
	outColor = tcol*vert_color*color;
}