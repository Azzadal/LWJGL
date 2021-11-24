package engine.graphics;

import engine.io.Window;
import engine.objects.Camera;
import engine.objects.GameObject;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL46C.*;

public class Renderer {
	private final Shader shader;
	private final Shader shaderSkybox;
	private final Window window;
	private final PointLight pointLight;

	public Renderer(Window window, Shader shader, Shader shaderSkybox) {
		this.window = window;
		this.shader = shader;
		this.shaderSkybox = shaderSkybox;
		Vector3f lightColour = new Vector3f(1, 1, 1);
		Vector3f lightPosition = new Vector3f(0, 0, 1);
		float lightIntensity = 1.0f;
		pointLight = new PointLight(lightColour, lightPosition, lightIntensity);
		PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
		pointLight.setAttenuation(att);
	}

	public void renderMesh(GameObject gameObject, Camera camera, DirectionalLight directionalLight, Vector3f ambientLight, Fog fog) {
		glBindVertexArray(gameObject.getMesh().getVAO());
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(3);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, gameObject.getMesh().getIBO());
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, gameObject.getMesh().getTexture().getTextureID());
		shader.bind();
		glUniform3f(glGetUniformLocation(shader.getProgramID(), "camera_pos"), camera.getPosition().x, camera.getPosition().y, camera.getPosition().z);
		Matrix4f viewMatrix = camera.view(camera.getPosition(), camera.getRotation());
		shader.setUniformPointLight("pointLight", pointLight);
		shader.setUniformMat4("u_ModelMatrix", gameObject.getModelMatrix());
		shader.setUniformMat4("view", viewMatrix);
		shader.setUniformMat4("projection", window.projection);

		DirectionalLight currDirLight = new DirectionalLight(directionalLight);
		Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
		dir.mul(viewMatrix);
		currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
		shader.setUniformDirectionalLight("directionalLight", currDirLight);
        shader.setUniformVec3("ambientLight", ambientLight);

		shader.setUniformInt("texture_sampler", 0);
		shader.setUniformMaterial("material", gameObject.getMesh().getMaterial());
		shader.setUniformFog("fog", fog);

		glDrawElements(GL_TRIANGLES, gameObject.getMesh().getIndices().length, GL_UNSIGNED_INT, 0);

		shader.unbind();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glDisableVertexAttribArray(3);
		glBindVertexArray(0);
//        System.out.println("Камера x " + camera.getPosition().x);
//        System.out.println("Камера y " + camera.getPosition().y);
//        System.out.println("Камера z " + camera.getPosition().z);
	}

	public void renderSkyBox(GameObject gameObject, Camera camera){
		glBindVertexArray(gameObject.getMesh().getVAO());
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(3);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, gameObject.getMesh().getIBO());
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, gameObject.getMesh().getTexture().getTextureID());
		shaderSkybox.bind();

        Matrix4f viewMatrix = camera.view(camera.getPosition(), camera.getRotation());

		viewMatrix.m30(0);
		viewMatrix.m31(0);
		viewMatrix.m32(0);
        shaderSkybox.setUniformInt("texture_sampler", 0);
        Matrix4f projectionMatrix = window.projection;
        shaderSkybox.setUniformMat4("projectionMatrix", projectionMatrix);
		shaderSkybox.setUniformMat4("modelViewMatrix", gameObject.getModelMatrix());
		shaderSkybox.setUniformVec3("ambientLight", new Vector3f(1,1,1));

		glDrawElements(GL_TRIANGLES, gameObject.getMesh().getIndices().length, GL_UNSIGNED_INT, 0);

		shaderSkybox.unbind();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(2);
		glDisableVertexAttribArray(3);
		glBindVertexArray(0);
	}
}
