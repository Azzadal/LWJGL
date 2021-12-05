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
    private final Mesh platzModel = ModelLoader.loadModel(
            "resources/models/1/94-format/Container.obj", "resources/models/1/44-container_textures/textures_container/Container_DiffuseMap.jpg");
	private final Mesh houseModel = ModelLoader.loadModel(
			"resources/models/2/45-cottage_free_other/Cottage_FREE.3DS",
			"resources/models/2/82-textures_cottage_dirt/Cottage_Dirt/Cottage_Dirt_Base_Color.png");
	private final Mesh skyBoxModel = ModelLoader.loadModel("resources/models/skybox/untitled.obj",
			"resources/models/skybox/3.png");
	private final Mesh dodecaedrModel = ModelLoader.loadModel("resources/models/dodecaedr/dod.obj",
			"resources/models/dodecaedr/9.jpg");
	private final GameObject airplane = new GameObject(
			new Vector3f(-185f, -412f, 42f),
			new Vector3f(0, 0, 0),
			new Vector3f(0.1f, 0.1f, 0.1f),
			airplaneModel);
	private final GameObject barn = new GameObject(
			new Vector3f(-70f, 70.5f, 34.5f),
			new Vector3f(1.54f, -0.1f, 0),
			new Vector3f(3f, 3f, 3f),
			barnModel);
    private final GameObject platz = new GameObject(
            new Vector3f(-110.6f, -100.5f, 15f),
            new Vector3f(1.54f, 0f, 0),
            new Vector3f(1.08f, .1f, 1.1f),
            platzModel);
	private final GameObject house = new GameObject(
			new Vector3f(-70f, -64.2f, 34.55f),
			new Vector3f(1.57f, -0.04f, 0),
			new Vector3f(0.1f, 0.1f, 0.1f),
			houseModel);
	private final GameObject skyBox = new GameObject(
			new Vector3f(0, 0, 0),
			new Vector3f(0,0,0),
			new Vector3f(900f, 900f, 900f),
			skyBoxModel);
	private final GameObject dodecaedr = new GameObject(
			new Vector3f(-125, 100f, 30.3f),
			new Vector3f(0,0,0),
			new Vector3f(8f, 8f, 8f),
			dodecaedrModel);
	private final Camera camera = new Camera(new Vector3f(-158f, 0, 545f), new Vector3f(-1.35f, -15.27f, 0));
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
		platzModel.setMaterial(new Material(platzModel.getTexture(), 1f));
		houseModel.setMaterial(new Material(houseModel.getTexture(), 1f));
		skyBoxModel.setMaterial(new Material(skyBoxModel.getTexture(), 1f));
		dodecaedrModel.setMaterial(new Material(dodecaedrModel.getTexture(), 1f));

		airplaneModel.create();
		barnModel.create();
		platzModel.create();
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

	private Vector3f startPosition = new Vector3f(-125, 100f, 30.2f);
    private boolean flag = true;
    private boolean flag1 = true;
    private boolean start = false;
	private void update() {
	    float speed = 4f;
		window.update();
		camera.update();
//		if (Input.isKeyDown(GLFW.GLFW_KEY_LEFT)) airplane.getRotation().x += 0.1f;
//		if (Input.isKeyDown(GLFW.GLFW_KEY_RIGHT)) airplane.getRotation().x -= 0.1f;
//		if (Input.isKeyDown(GLFW.GLFW_KEY_UP)) airplane.getRotation().y += 0.1f;
//		if (Input.isKeyDown(GLFW.GLFW_KEY_DOWN)) airplane.getRotation().y -= 0.1f;
//		if (Input.isKeyDown(GLFW.GLFW_KEY_V)) airplane.getRotation().z -= 0.01f;
//		if (Input.isKeyDown(GLFW.GLFW_KEY_B)) airplane.getRotation().z += 0.01f;

//        System.out.println(camera.getRotation().x + " " + camera.getRotation().y + " " + camera.getRotation().z);
		/**
		 * Движение додекаедра
		 */
        if (dodecaedr.getPosition().y > -337f && flag1) {
            dodecaedr.getPosition().y -= 0.2f * speed;
            dodecaedr.getRotation().z -= 0.01f;
            dodecaedr.getScale().x -= 0.001f;
            dodecaedr.getScale().z -= 0.001f;
        }
        else flag1 = false;
        if (dodecaedr.getPosition().y < 100f && !flag1) {
            dodecaedr.getPosition().y += 0.2f * speed;
            dodecaedr.getRotation().z += 0.01f;
            dodecaedr.getScale().x += 0.001f;
            dodecaedr.getScale().z += 0.001f;
        }
        else flag1 = true;
        if (dodecaedr.getPosition().z < 150f && flag) {
            dodecaedr.getPosition().z += 0.4f;

        } else flag = false;
        if (dodecaedr.getPosition().z >= startPosition.z && !flag) {
            dodecaedr.getPosition().z -= 0.4f;
        } else flag = true;

        /**
         * Движение самолёта
         */
        if (Input.isKeyDown(GLFW.GLFW_KEY_P)) start = true;
        if (start) {
            airplane.getPosition().y += 1f;
            airplane.getPosition().z += 0.05f;
            airplane.getRotation().x += 0.0009f;
            airplane.getRotation().y += 0.0001f;
            airplane.getRotation().z -= 0.0005f;
        }

		float lightAngle = -85;
		float factor = 1 - (float) (Math.abs(lightAngle) - 80) / 10.0f;
		directionalLight.setIntensity(factor);
		directionalLight.getColor().y = Math.max(factor, 0.9f);
		directionalLight.getColor().z = Math.max(factor, 0.5f);

		double angRad = Math.toRadians(lightAngle);
		directionalLight.getDirection().x = (float) Math.sin(angRad);
		directionalLight.getDirection().y = (float) Math.cos(angRad);
	}

	private void render() {
		renderer.renderMesh(airplane, camera, directionalLight, ambientLight, fog);
		renderer.renderMesh(barn, camera, directionalLight, ambientLight, fog);
		renderer.renderMesh(platz, camera, directionalLight, ambientLight, fog);
		renderer.renderMesh(house, camera, directionalLight, ambientLight, fog);
		renderer.renderMesh(dodecaedr, camera, directionalLight, ambientLight, fog);
		renderer.renderSkyBox(skyBox, camera);
		window.swapBuffers();
	}

	private void close() {
		window.destroy();
		airplaneModel.destroy();
		barnModel.destroy();
		platzModel.destroy();
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
