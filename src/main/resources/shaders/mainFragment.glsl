#version 410

in vec3 passColor;
in vec2 passTextureCoord;
in vec3 mvVertexNormal;
in vec3 mvVertexPos;
out vec4 outColor;

vec4 textureColor;


vec4 ambientC;
vec4 diffuseC;
vec4 speculrC;

struct Attenuation
{
	float constant;
	float linear;
	float exponent;
};

struct PointLight
{
	vec3 colour;
	vec3 position;
	float intensity;
	Attenuation att;
};

struct DirectionalLight
{
	vec3 colour;
	vec3 direction;
	float intensity;
};

struct Material
{
	vec4 ambient;
	vec4 diffuse;
	vec4 specular;
	int hasTexture;
	float reflectance;
};

struct Fog
{
	int activeFog;
	vec3 colour;
	float density;
};

uniform sampler2D texture_sampler;
uniform vec4 u_Colour;

uniform vec3 ambientLight;
uniform vec3 camera_pos;
uniform float specularPower;
uniform Material material;
uniform PointLight pointLight;
uniform DirectionalLight directionalLight;
uniform Fog fog;

void setupColours(Material material, vec2 textCoord)
{
	if (material.hasTexture == 1)
	{
		ambientC = texture(texture_sampler, textCoord);
		diffuseC = ambientC;
		speculrC = ambientC;
	}
	else
	{
		ambientC = material.ambient;
		diffuseC = material.diffuse;
		speculrC = material.specular;
	}
}

vec4 calcPointLight(PointLight light, vec3 position, vec3 normal)
{
	vec4 diffuseColour = vec4(0, 0, 0, 0);
	vec4 specColour = vec4(0, 0, 0, 0);

	// Diffuse Light
	vec3 light_direction = light.position - position;
	vec3 to_light_source  = normalize(light_direction);
	float diffuseFactor = max(dot(normal, to_light_source ), 0.0);
	diffuseColour = diffuseC * vec4(light.colour, 1.0) * light.intensity * diffuseFactor;

	// Specular Light
	vec3 camera_direction = normalize(-position);
	vec3 from_light_source = -to_light_source;
	vec3 reflected_light = normalize(reflect(from_light_source, normal));
	float specularFactor = max( dot(camera_direction, reflected_light), 0.0);
	specularFactor = pow(specularFactor, specularPower);
	specColour = speculrC * specularFactor * material.reflectance * vec4(light.colour, 1.0);

	// Attenuation
	float distance = length(light_direction);
	float attenuationInv = light.att.constant + light.att.linear * distance +
	light.att.exponent * distance * distance;
	return (diffuseColour + specColour) / attenuationInv;
}

vec4 calcLightColour(vec3 light_colour, float light_intensity, vec3 position, vec3 to_light_dir, vec3 normal)
{
	vec4 diffuseColour = vec4(0, 0, 0, 0);
	vec4 specColour = vec4(0, 0, 0, 0);

	// Diffuse Light
	float diffuseFactor = max(dot(normal, to_light_dir), 0.0);
	diffuseColour = diffuseC * vec4(light_colour, 1.0) * light_intensity * diffuseFactor;

	// Specular Light
	vec3 camera_direction = normalize(-position);
	vec3 from_light_dir = -to_light_dir;
	vec3 reflected_light = normalize(reflect(from_light_dir , normal));
	float specularFactor = max( dot(camera_direction, reflected_light), 0.0);
	specularFactor = pow(specularFactor, specularPower);
	specColour = speculrC * light_intensity  * specularFactor * material.reflectance * vec4(light_colour, 1.0);

	return (diffuseColour + specColour);
}

vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal)
{
	return calcLightColour(light.colour, light.intensity, position, normalize(light.direction), normal);
}

vec4 calcFog(vec3 pos, vec4 colour, Fog fog, vec3 ambientLight, DirectionalLight dirLight)
{
	vec3 fogColor = fog.colour * (ambientLight + dirLight.colour * dirLight.intensity);
//	float distance = length(pos);
	float distance = length(exp(camera_pos / pos * 0.15));
	float fogFactor = 1.0 / exp( (distance * fog.density) * (distance * fog.density));
	fogFactor = clamp( fogFactor, 0.0, 1.0 );

	vec3 resultColour = mix(fogColor, colour.xyz, fogFactor);
	return vec4(resultColour.xyz, colour.w);
}


void main() {
	setupColours(material, passTextureCoord);

	vec4 diffuseSpecularComp = calcDirectionalLight(directionalLight, mvVertexPos, mvVertexNormal);
	diffuseSpecularComp += calcPointLight(pointLight, mvVertexPos, mvVertexNormal);

	outColor = ambientC * vec4(ambientLight, 1) + diffuseSpecularComp;

	if ( fog.activeFog == 1 )
	{
		outColor = calcFog(mvVertexPos, outColor, fog, ambientLight, directionalLight);
	}
}