package main;

import engine.graphics.*;
import engine.io.Input;
import engine.io.ModelLoader;
import engine.io.Window;
import engine.objects.Camera;
import engine.objects.GameObject;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class Main implements Runnable {
	private Window window;
	private Renderer renderer;
	private Shader shader;
	private Shader shaderSkybox;
	private Vector3f ambientLight;
	private final Mesh airplaneModel = ModelLoader.loadModel(
			"resources/models/6/airplane_v2_L2.123c71795678-4b63-46c4-b2c6-549c45f4c806/my.obj",
			"resources/models/6/airplane_v2_L2.123c71795678-4b63-46c4-b2c6-549c45f4c806/" +
					"airplane_body_diffuse_v1_my.jpg");
	private final Mesh barnModel = ModelLoader.loadModel(
			"resources/models/saray/my/saray.obj", "resources/models/saray/my/cottage_diffuse.png");
	private final Mesh houseModel = ModelLoader.loadModel(
			"resources/models/2/45-cottage_free_other/Cottage_FREE.3DS",
			"resources/models/2/82-textures_cottage_dirt/Cottage_Dirt/Cottage_Dirt_Base_Color.png");
	private final Mesh skyBoxModel = ModelLoader.loadModel("resources/models/skybox/untitled.obj",
			"resources/models/skybox/3.png");
	private final Mesh dodecaedrModel = ModelLoader.loadModel("resources/models/dodecaedr/dod.obj",
			"resources/models/dodecaedr/dod.jpg");
	private final GameObject airplane = new GameObject(
			new Vector3f(-97f, -460f, -3.3f),
			new Vector3f(0, 0, 0),
			new Vector3f(0.1f, 0.1f, 0.1f),
			airplaneModel);
	private final GameObject barn = new GameObject(
			new Vector3f(30f, 10.5f, 7f),
			new Vector3f(1.54f, -0.08f, 0),
			new Vector3f(2f, 2f, 2f),
			barnModel);
	private final GameObject house = new GameObject(
			new Vector3f(36.2f, -64.2f, 6.55f),
			new Vector3f(1.57f, 0, 0),
			new Vector3f(0.05f, 0.05f, 0.05f),
			houseModel);
	private final GameObject skyBox = new GameObject(
			new Vector3f(0, 0, 0),
			new Vector3f(0,0,0),
			new Vector3f(1000f, 1000f, 1000f),
			skyBoxModel);
	private final GameObject dodecaedr = new GameObject(
			new Vector3f(50, 50, 4),
			new Vector3f(0,0,0),
			new Vector3f(1f, 1f, 1f),
			dodecaedrModel);
	private final Camera camera = new Camera(new Vector3f(0, 0, 1), new Vector3f(0, 0, 0));
	private DirectionalLight directionalLight;
	private Fog fog;

	private void start() {
		Thread game = new Thread(this, "game");
		game.start();
	}

	private void init() {
		int WIDTH = 1280;
		int HEIGHT = 760;
		fog = new Fog(true, new Vector3f(0.5f, 0.5f, 0.5f), 0.3f);
		window = new Window(WIDTH, HEIGHT, "Курсовая");
		shader = new Shader("/shaders/mainVertex.glsl", "/shaders/mainFragment.glsl");
		shaderSkybox = new Shader("/shaders/skyboxVertex.glsl", "/shaders/skyboxFragment.glsl");

		Vector3f lightColour = new Vector3f(1, 1, 1);
		Vector3f lightPosition = new Vector3f(0, 0, 1);
		float lightIntensity = 1.0f;
		directionalLight = new DirectionalLight(lightColour, lightPosition, lightIntensity);

		ambientLight = new Vector3f(.3f, .3f, .3f);
		renderer = new Renderer(window, shader, shaderSkybox);

		window.setBackgroundColor(1.0f, 0.5f, 0);
		window.create();

		airplaneModel.setMaterial(new Material(airplaneModel.getTexture(), 1f));
		barnModel.setMaterial(new Material(barnModel.getTexture(), 1f));
		houseModel.setMaterial(new Material(houseModel.getTexture(), 1f));
		skyBoxModel.setMaterial(new Material(skyBoxModel.getTexture(), 1f));
		dodecaedrModel.setMaterial(new Material(dodecaedrModel.getTexture(), 1f));

//		glEnable(GL_BLEND);

//		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		airplaneModel.create();
		barnModel.create();
		houseModel.create();
        skyBoxModel.create();
		dodecaedrModel.create();
		shader.create();
		shaderSkybox.create();
		try {
			shader.createUniform("texture_sampler");
			shader.createMaterialUniform("material");
			shader.createPointLightUniform("pointLight");
			shader.createDirectionalLightUniform("directionalLight");
			shader.createFogUniform("fog");
			shader.createUniform("ambientLight");
			shader.createUniform("specularPower");
			shader.createUniform("u_ModelMatrix");
			shader.createUniform("view");
			shader.createUniform("projection");

			shaderSkybox.createUniform("projectionMatrix");
			shaderSkybox.createUniform("modelViewMatrix");
			shaderSkybox.createUniform("texture_sampler");
			shaderSkybox.createUniform("ambientLight");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		init();
		while (!window.shouldClose() && !Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
			update();
			render();
			if (Input.isKeyDown(GLFW.GLFW_KEY_F11)) window.setFullscreen(!window.isFullscreen());
		}
		close();
	}

	private void update() {
	    float speed = 0.02f;
		window.update();
		camera.update();
		if (Input.isKeyDown(GLFW.GLFW_KEY_LEFT)) airplane.getPosition().x += 0.4f;
		if (Input.isKeyDown(GLFW.GLFW_KEY_RIGHT)) airplane.getPosition().x -= 0.4f;
		if (Input.isKeyDown(GLFW.GLFW_KEY_UP)) airplane.getPosition().y += 0.4f;
		if (Input.isKeyDown(GLFW.GLFW_KEY_DOWN)) airplane.getPosition().y -= 0.4f;
		if (Input.isKeyDown(GLFW.GLFW_KEY_V)) airplane.getPosition().z -= 0.4f;
		if (Input.isKeyDown(GLFW.GLFW_KEY_B)) airplane.getPosition().z += 0.4f;


//		if (Input.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) System.out.println("X: " + Input.getScrollX() + ", Y: " + Input.getScrollY());

		// Update directional light direction, intensity and colour
		float lightAngle = -85;
//		if (lightAngle > 90) {
//			directionalLight.setIntensity(0);
//			if (lightAngle >= 360) {
//				lightAngle = -90;
//			}
//		} else {
			float factor = 1 - (float) (Math.abs(lightAngle) - 80) / 10.0f;
			directionalLight.setIntensity(factor);
			directionalLight.getColor().y = Math.max(factor, 0.9f);
			directionalLight.getColor().z = Math.max(factor, 0.5f);

//		} else {
//			directionalLight.setIntensity(1);
//			directionalLight.getColor().x = 1;
//			directionalLight.getColor().y = 1;
//			directionalLight.getColor().z = 1;
//		}
		double angRad = Math.toRadians(lightAngle);
		directionalLight.getDirection().x = (float) Math.sin(angRad);
		directionalLight.getDirection().y = (float) Math.cos(angRad);
	}

	private void render() {
		renderer.renderMesh(airplane, camera, directionalLight, ambientLight, fog);
		renderer.renderMesh(barn, camera, directionalLight, ambientLight, fog);
		renderer.renderMesh(house, camera, directionalLight, ambientLight, fog);
		renderer.renderMesh(dodecaedr, camera, directionalLight, ambientLight, fog);
		renderer.renderSkyBox(skyBox, camera);
		window.swapBuffers();
	}

	private void close() {
		window.destroy();
		airplaneModel.destroy();
		barnModel.destroy();
		houseModel.destroy();
        skyBoxModel.destroy();
		dodecaedrModel.destroy();
		shader.destroy();
		shaderSkybox.destroy();
	}

	public static void main(String[] args) {
		new Main().start();
	}
}
