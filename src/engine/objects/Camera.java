package engine.objects;

import engine.graphics.PointLight;
import engine.io.Input;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class Camera {
    private Vector3f position, rotation;
    private float mouseSensitivity = 0.007f;
    private float distance = 2.0f;
    private float horizontalAngle = 0;
    private float verticalAngle = 0;
    private double oldMouseX = 0, oldMouseY = 0, newMouseX, newMouseY;

    public Camera(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public void update() {
        newMouseX = Input.getMouseX();
        newMouseY = Input.getMouseY();

        float moveSpeed = 2f;
        float x = (float) Math.sin(Math.toRadians(rotation.y)) * moveSpeed;
        float z = (float) Math.cos(Math.toRadians(rotation.y)) * moveSpeed;

        if (Input.isKeyDown(GLFW.GLFW_KEY_A)) position = position.add(-z, 0, x);
        if (Input.isKeyDown(GLFW.GLFW_KEY_D)) position = position.add(z, 0, -x);
        if (Input.isKeyDown(GLFW.GLFW_KEY_W)) position = position.add(-x, 0, -z);
        if (Input.isKeyDown(GLFW.GLFW_KEY_S)) position = position.add(x, 0, z);
        if (Input.isKeyDown(GLFW.GLFW_KEY_SPACE)) position = position.add(0, moveSpeed, 0);
        if (Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) position = position.add(0, -moveSpeed, 0);

        float dx = (float) (newMouseX - oldMouseX);
        float dy = (float) (newMouseY - oldMouseY);

        rotation = rotation.add( new Vector3f(-dy * mouseSensitivity, -dx * mouseSensitivity, 0));

        oldMouseX = newMouseX;
        oldMouseY = newMouseY;
    }

    public void update(GameObject object) {
        newMouseX = Input.getMouseX();
        newMouseY = Input.getMouseY();

        float dx = (float) (newMouseX - oldMouseX);
        float dy = (float) (newMouseY - oldMouseY);

        if (Input.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
            verticalAngle -= dy * mouseSensitivity;
            horizontalAngle += dx * mouseSensitivity;
        }
        if (Input.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
            if (distance > 0) {
                distance += dy * mouseSensitivity / 4;
            } else {
                distance = 0.1f;
            }
        }

        float horizontalDistance = (float) (distance * Math.cos(Math.toRadians(verticalAngle)));
        float verticalDistance = (float) (distance * Math.sin(Math.toRadians(verticalAngle)));

        float xOffset = (float) (horizontalDistance * Math.sin(Math.toRadians(-horizontalAngle)));
        float zOffset = (float) (horizontalDistance * Math.cos(Math.toRadians(-horizontalAngle)));

        position.set(object.getPosition().x + xOffset, object.getPosition().y - verticalDistance, object.getPosition().z + zOffset);

        rotation.set(verticalAngle, -horizontalAngle, 0);

        oldMouseX = newMouseX;
        oldMouseY = newMouseY;
    }

    public Matrix4f view(Vector3f position, Vector3f rotation){
        Matrix4f result;

        Vector3f negative = new Vector3f(-position.x, -position.y, -position.z);
        Matrix4f translationMatrix = new Matrix4f().translate(negative);
        Matrix4f rotXMatrix = new Matrix4f().rotate(rotation.x, new Vector3f(1, 0, 0));
        Matrix4f rotYMatrix = new Matrix4f().rotate(rotation.y, new Vector3f(0, 1, 0));
        Matrix4f rotZMatrix = new Matrix4f().rotate(rotation.z, new Vector3f(0, 0, 1));

        Matrix4f rotationMatrix = rotYMatrix.mul(rotZMatrix.mul(rotXMatrix));

        result = translationMatrix.mul(rotationMatrix);

        return result;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }
}
