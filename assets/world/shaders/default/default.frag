#ifdef GL_ES
	#define LOWP lowp
	precision mediump float;
#else
	#define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
/*uniform vec2 u_textureSize;
uniform vec4 u_outlineColor;*/

void main() {
	/*if(texture2D(u_texture, v_texCoords).a == 0.0) {
		vec2 pixelSize = 1.0 / u_textureSize;
		if(texture2D(u_textureSize, v_texCoords + vec2(pixelSize.x, 0.0)).a > 0.0
		|| texture2D(u_textureSize, v_texCoords + vec2(-pixelSize.x, 0.0)).a > 0.0
		|| texture2D(u_textureSize, v_texCoords + vec2(pixelSize.y, 0.0)).a > 0.0
		|| texture2D(u_textureSize, v_texCoords + vec2(-pixelSize.y, 0.0)).a > 0.0) {
			gl_FragColor = u_outlineColor;
			return;
		}
	}
	
	gl_FragColor = vec4(u_outlineColor.rgb, 0.0);*/
	gl_FragColor = v_color * texture2D(u_texture, v_texCoords);
}