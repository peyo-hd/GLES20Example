precision mediump float;
varying vec2 vTexCoord;
uniform sampler2D uSamplerTex;

void main() {
  gl_FragColor = texture2D(uSamplerTex, vTexCoord);
}
