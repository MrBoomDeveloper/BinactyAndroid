#ifdef GL_ES
	#define LOWP lowp
	precision mediump float;
#else
	#define LOWP
#endif

uniform sampler2D u_sampler2D;
uniform vec2 u_resolution;

varying vec4 v_color;
varying vec2 v_texCoord0;

void main() {
	vec4 color = texture2D(u_sampler2D, v_texCoord0) * v_color;
	
	vec2 relativePosition = gl_FragCoord.xy / u_resolution - 0.5;
	float vignette = smoothstep(0.6, 0.4, length(relativePosition));
	color.rgb = mix(color.rgb, color.rgb * vignette, 0.6);
	
	gl_FragColor = color;
}