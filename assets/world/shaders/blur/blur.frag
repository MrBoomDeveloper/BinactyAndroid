#ifdef GL_ES
	#define LOWP lowp
	precision mediump float;
#else
	#define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

uniform float u_time;
uniform float u_damage;
uniform sampler2D u_texture;

void main() {
	// Определяем смещение текстуры
	vec2 offset = vec2(u_damage * sin(u_time), u_damage * cos(u_time));
	// Получаем цвет пикселя из текстуры
	vec4 color = texture2D(u_texture, v_texCoords + offset);
	// Устанавливаем альфа-канал цвета
	gl_FragColor = vec4(color.rgb, 0.5);
}