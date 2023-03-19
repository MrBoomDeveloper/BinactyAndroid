#ifdef GL_FRAGMENT_PRECISION_HIGH
	precision highp float;
#else
	precision mediump float;
#endif

uniform float time;
uniform int pointerCount;
uniform vec3 pointers[10];
uniform vec2 resolution;
uniform sampler2D test;

void main(void) {
	vec3 color = texture2D(u_texture, v_texCoords).rgb;
	float gray = (color.r + color.g + color.b) / 3.0;
	vec3 grayscale = vec3(gray);

	gl_FragColor = vec4(grayscale, 1.0);
}