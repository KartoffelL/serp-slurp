#version 450 core

layout(location = 0) out vec2 tex_coords;

const vec2[] a = vec2[](vec2(1, 1), vec2(1, -1), vec2(-1, -1), vec2(-1, -1), vec2(-1, 1), vec2(1, 1));

void main() {
	tex_coords = a[gl_VertexIndex];
	gl_Position = vec4(tex_coords, 0, 1);
}
