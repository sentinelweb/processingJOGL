// from orange book cubemap example
//
// Fragment shader for cube map environment mapping
//
// Author: Randi Rost
//
// Copyright (c) 2003-2006: 3Dlabs, Inc.
//
// See 3Dlabs-License.txt for license information
//
varying vec3  ReflectDir;
varying float LightIntensity;

uniform vec3  BaseColor;
uniform float MixRatio;
uniform samplerCube EnvMap;

void main() {
    // Look up environment map value in cube map

     vec3 envColor = vec3(textureCube(EnvMap, ReflectDir));

    // Add lighting to base color and mix

     vec3 base = LightIntensity * BaseColor;
     envColor  = mix(envColor, base, MixRatio);

    gl_FragColor = vec4(envColor, 1.0);

}