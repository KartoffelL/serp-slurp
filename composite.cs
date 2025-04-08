#version 460 core


layout(binding = 0, rgba32f) uniform image2D outputTextr;
layout(binding = 1) uniform sampler2D inputTextr;

layout (local_size_x = 256, local_size_y = 1, local_size_z = 1) in;

void main() {
	imageStore(outputTextr, ivec2(gl_GlobalInvocationID.xy), vec4(1, 0, 0, 1));
}