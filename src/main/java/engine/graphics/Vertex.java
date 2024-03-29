package main.java.engine.graphics;


//import main.java.engine.maths.Vector2f;
//import main.java.engine.maths.Vector3f;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Vertex {
	private Vector3f position, normal;
	private Vector2f textureCoord;

	public Vertex(Vector3f position, Vector3f normal, Vector2f textureCoord) {
		this.position = position;
		this.normal = normal;
		this.textureCoord = textureCoord;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getNormal() {
		return normal;
	}

	public Vector2f getTextureCoord() {
		return textureCoord;
	}
}