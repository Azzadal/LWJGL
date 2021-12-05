package main.java.engine.graphics;

import org.lwjgl.system.MemoryUtil;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL46C.*;

public class Mesh {
	private final Vertex[] vertices;
	private final int[] indices;
	private final main.java.engine.graphics.Texture texture;
	private int vao, pbo, ibo, cbo, tbo, vnbo;
	private Material material;

	public Mesh(Vertex[] vertices, int[] indices, Texture texture) {
		this.vertices = vertices;
		this.indices = indices;
		this.texture = texture;
	}

	public void create() {
		texture.create();

		vao = glCreateVertexArrays();
		glBindVertexArray(vao);

		FloatBuffer positionBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
		float[] positionData = new float[vertices.length * 3];
		for (int i = 0; i < vertices.length; i++) {
			positionData[i * 3] = vertices[i].getPosition().x();
			positionData[i * 3 + 1] = vertices[i].getPosition().y();
			positionData[i * 3 + 2] = vertices[i].getPosition().z();
		}
		positionBuffer.put(positionData).flip();

		pbo = storeData(positionBuffer, 0, 3);

		FloatBuffer vnormalBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
		float[] vnormalData = new float[vertices.length * 3];
		for (int i = 0; i < vertices.length; i++) {
			vnormalData[i * 3] = vertices[i].getNormal().x();
			vnormalData[i * 3 + 1] = vertices[i].getNormal().y();
			vnormalData[i * 3 + 2] = vertices[i].getNormal().z();
		}
		vnormalBuffer.put(vnormalData).flip();

		vnbo = storeData(vnormalBuffer, 3, 3);

		FloatBuffer textureBuffer = MemoryUtil.memAllocFloat(vertices.length * 2);
		float[] textureData = new float[vertices.length * 2];
		for (int i = 0; i < vertices.length; i++) {
			textureData[i * 2] = vertices[i].getTextureCoord().x;
			textureData[i * 2 + 1] = vertices[i].getTextureCoord().y;
		}
		textureBuffer.put(textureData).flip();

		tbo = storeData(textureBuffer, 2, 2);

		IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indices.length);
		indicesBuffer.put(indices).flip();

		ibo = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	private int storeData(FloatBuffer buffer, int index, int size) {
		int bufferID = glCreateBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, bufferID);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		glVertexAttribPointer(index, size, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		return bufferID;
	}

	public void destroy() {
		glDeleteBuffers(pbo);
		glDeleteBuffers(cbo);
		glDeleteBuffers(ibo);
		glDeleteBuffers(tbo);
		glDeleteBuffers(vnbo);

		glDeleteVertexArrays(vao);

		texture.destroy();
	}

	public Vertex[] getVertices() {
		return vertices;
	}

	public int[] getIndices() {
		return indices;
	}

	public Material getMaterial() {
		return material;
	}

	public int getVAO() {
		return vao;
	}

	public int getVnbo() {
		return vnbo;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public int getPBO() {
		return pbo;
	}

	public int getCBO() {
		return cbo;
	}

	public int getTBO() {
		return tbo;
	}

	public int getIBO() {
		return ibo;
	}

	public Texture getTexture() {
		return texture;
	}
}
