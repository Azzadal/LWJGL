package main.java.engine.objects;

import main.java.engine.graphics.Mesh;

import org.joml.Matrix4f;
import org.joml.Vector3f;


public class GameObject {
    private Vector3f position, rotation, scale;
    private Mesh mesh;

    public GameObject(Vector3f position, Vector3f rotation, Vector3f scale, Mesh mesh) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.mesh = mesh;
    }

    public Matrix4f getModelMatrix()
    {
        return new Matrix4f()
                .translate(position)
                .rotate(rotation.x, new Vector3f(1, 0, 0))
                .rotate(rotation.y, new Vector3f(0, 1, 0))
                .rotate(rotation.z, new Vector3f(0, 0, 1))
                .scale(scale);
    }

    public void update() {
        position.z = position.z - 0.05f;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Vector3f getScale() {
        return scale;
    }

    public Mesh getMesh() {
        return mesh;
    }
}
