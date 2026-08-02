#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform vec2 BlurDir;
uniform float Radius;
uniform float Progress;

out vec4 fragColor;

void main() {
    float radius = floor(Radius * Progress);
    vec4 color = vec4(0.0);

    if(radius <= 0.0) {
        fragColor = texture(DiffuseSampler, texCoord);
        return;
    }

    float samples = 0.0;

    for(float r = -radius; r <= radius; r += 1.0) {

        color += texture(
            DiffuseSampler,
            texCoord + oneTexel * r * BlurDir
        );

        samples += 1.0;
    }

    fragColor = color / samples;
}