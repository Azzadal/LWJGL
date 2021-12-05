package engine.graphics;

import static org.lwjgl.opengl.GL46C.*;

import engine.utils.FileUtils;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import java.util.HashMap;
import java.util.Map;

public class Shader {
	private final String vertexFile;
	private final String fragmentFile;
	private int vertexID, fragmentID, programID;
	private final Map<String, Integer> uniforms;

	public Shader(String vertexPath, String fragmentPath) {
		vertexFile = FileUtils.loadAsString(vertexPath);
		fragmentFile = FileUtils.loadAsString(fragmentPath);
		uniforms = new HashMap<>();
	}

	public void createUniform(String uniformName) throws Exception {
		int uniformLocation = glGetUniformLocation(programID, uniformName);
		if (uniformLocation < 0) {
			throw new Exception("Could not find uniform:" + uniformName);
		}
		uniforms.put(uniformName, uniformLocation);
	}

	public void create() {
		programID = glCreateProgram();
		vertexID = glCreateShader(GL_VERTEX_SHADER);

		glShaderSource(vertexID, vertexFile);
		glCompileShader(vertexID);

		if (glGetShaderi(vertexID, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println("Vertex Shader: " + glGetShaderInfoLog(vertexID));
			return;
		}

		fragmentID = glCreateShader(GL_FRAGMENT_SHADER);

		glShaderSource(fragmentID, fragmentFile);
		glCompileShader(fragmentID);

		if (glGetShaderi(fragmentID, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println("Fragment Shader: " + glGetShaderInfoLog(fragmentID));
			return;
		}

		glAttachShader(programID, vertexID);
		glAttachShader(programID, fragmentID);

		glLinkProgram(programID);
		if (glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE) {
			System.err.println("Program Linking: " + glGetProgramInfoLog(programID));
			return;
		}

		glValidateProgram(programID);
		if (glGetProgrami(programID, GL_VALIDATE_STATUS) == GL_FALSE) {
			System.err.println("Program Validation: " + glGetProgramInfoLog(programID));
			return;
		}
	}

	public void bind() {
		glUseProgram(programID);
	}

	void unbind() {
		glUseProgram(0);
	}

	public void setUniformInt(String name, int value){
//		glUniform1i(glGetUniformLocation(programID, name), value);
		glUniform1i(uniforms.get(name), value);
	}

	public void setUniformInt2(String name, int x, int y){
		glUniform2i(glGetUniformLocation(programID, name), x, y);
	}

	public void setUniformInt3(String name, int x, int y, int z){
		glUniform3i(glGetUniformLocation(programID, name), x, y, z);
	}

	public void setUniformInt4(String name, int x, int y, int z, int w){
		glUniform4i(glGetUniformLocation(programID, name), x, y, z, w);
	}

	public void setUniformFloat(String name, float value){
		glUniform1f(uniforms.get(name), value);
	}

	public void setUniformFloat2(String name, float x, float y){
		glUniform2f(glGetUniformLocation(programID, name), x, y);
	}

	public void setUniformFloat3(String name, float x, float y, float z){
		glUniform3f(glGetUniformLocation(programID, name), x, y, z);
	}

	public void setUniformFloat4(String name, float x, float y, float z, float w){
		glUniform4f(glGetUniformLocation(programID, name), x, y, z, w);
	}

	public void setUniformBoolean(String name, boolean value){
		glUniform1i(glGetUniformLocation(programID, name), value ? 1 : 0);
	}

	public void setUniformVec2(String name, Vector2f value){
		glUniform2f(glGetUniformLocation(programID, name), value.x, value.y);
	}

	public void setUniformVec3(String name, Vector3f value){
		glUniform3f(uniforms.get(name), value.x, value.y, value.z);
	}

	public void setUniformVec4(String name, Vector4f value){
		glUniform4f(uniforms.get(name), value.x, value.y, value.z, value.w);
	}

	public void setUniformPointLight(String name, PointLight pointLight){
		setUniformVec3(name + ".colour", pointLight.getColor() );
		setUniformVec3(name + ".position", pointLight.getPosition());
		setUniformFloat(name + ".intensity", pointLight.getIntensity());
		PointLight.Attenuation att = pointLight.getAttenuation();
		setUniformFloat(name + ".att.constant", att.getConstant());
		setUniformFloat(name + ".att.linear", att.getLinear());
		setUniformFloat(name + ".att.exponent", att.getExponent());
	}

	public void setUniformDirectionalLight(String uniformName, DirectionalLight dirLight) {
		setUniformVec3(uniformName + ".colour", dirLight.getColor() );
		setUniformVec3(uniformName + ".direction", dirLight.getDirection());
		setUniformFloat(uniformName + ".intensity", dirLight.getIntensity());
	}

	public void setUniformMaterial(String name, Material material){
		setUniformVec4(name + ".ambient", material.getAmbientColour());
		setUniformVec4(name + ".diffuse", material.getDiffuseColour());
		setUniformVec4(name + ".specular", material.getSpecularColour());
		setUniformInt(name + ".hasTexture", material.isTextured() ? 1 : 0);
		setUniformFloat(name + ".reflectance", material.getReflectance());
	}

    public void setUniformFog(String name, Fog fog) {
        setUniformInt(name + ".activeFog", fog.isActive() ? 1 : 0);
        setUniformVec3(name + ".colour", fog.getColour() );
        setUniformFloat(name + ".density", fog.getDensity());
    }

	public void createPointLightUniform(String uniformName) throws Exception {
		createUniform(uniformName + ".colour");
		createUniform(uniformName + ".position");
		createUniform(uniformName + ".intensity");
		createUniform(uniformName + ".att.constant");
		createUniform(uniformName + ".att.linear");
		createUniform(uniformName + ".att.exponent");
	}

	public void createDirectionalLightUniform(String uniformName) throws Exception {
		createUniform(uniformName + ".colour");
		createUniform(uniformName + ".direction");
		createUniform(uniformName + ".intensity");
	}

	public void createMaterialUniform(String uniformName) throws Exception {
		createUniform(uniformName + ".ambient");
		createUniform(uniformName + ".diffuse");
		createUniform(uniformName + ".specular");
		createUniform(uniformName + ".hasTexture");
		createUniform(uniformName + ".reflectance");
	}

    public void createFogUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".activeFog");
        createUniform(uniformName + ".colour");
        createUniform(uniformName + ".density");
    }

    public void setUniformMat4(String name, Matrix4f value)
    {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			glUniformMatrix4fv(uniforms.get(name), false,
					value.get(stack.mallocFloat(16)));
		}
//        FloatBuffer buffer = BufferUtils.createFloatBuffer(4 * 4);
//		value.get(buffer);
//
//        glUniformMatrix4fv(glGetUniformLocation(programID, name), false, buffer);
    }

	public void destroy() {
		glDetachShader(programID, vertexID);
		glDetachShader(programID, fragmentID);
		glDeleteProgram(programID);
	}

    public int getProgramID() {
        return programID;
    }
}
