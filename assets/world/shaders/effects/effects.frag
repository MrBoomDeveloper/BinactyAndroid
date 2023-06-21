precision mediump float;

uniform sampler2D u_texture;
uniform float flashProgress;

varying vec2 v_texCoords;

void main() {
	vec4 originalColor = texture2D(u_texture, v_texCoords);
	gl_FragColor = originalColor + vec4(
		(1.0 - originalColor.r) * flashProgress,
		(1.0 - originalColor.g) * flashProgress,
		(1.0 - originalColor.b) * flashProgress, 0);
}