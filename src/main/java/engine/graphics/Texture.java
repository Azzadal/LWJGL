package main.java.engine.graphics;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static main.java.engine.utils.FileUtils.*;

import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL46C.*;
import org.lwjgl.opengl.GL13;

import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

public class Texture {
	private String path;
	private int format, internalFormat;
	private int width, height;
	private int textureID;
	private int channels;
	private ByteBuffer data;
	
	public Texture(String path) {
		this.path = path;
	}
	
	public void create() {
		IntBuffer w = BufferUtils.createIntBuffer(1);
		IntBuffer h = BufferUtils.createIntBuffer(1);
		IntBuffer chan = BufferUtils.createIntBuffer(1);
		try {
			data = stbi_load_from_memory(resourceToByteBuffer(path), w, h, chan, 0);
			this.width = w.get(0);
			this.height = h.get(0);
			this.channels = chan.get(0);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (this.channels == 4){
			this.internalFormat = GL_RGBA8;
			this.format = GL_RGBA;
		} else if (this.channels == 3){
			this.internalFormat = GL_RGB8;
			this.format = GL_RGB;
		}

        this.textureID = glCreateTextures(GL_TEXTURE_2D);

        bind();

		glTextureStorage2D(this.textureID, 1, internalFormat, width, height);
		glTextureParameteri(this.textureID, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTextureParameteri(this.textureID, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTextureParameteri(this.textureID, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
		glTextureParameteri(this.textureID, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);

		glTextureSubImage2D(this.textureID, 0, 0, 0, width, height, format,  GL_UNSIGNED_BYTE, data);

        unbind();

		if (data != null){
			stbi_image_free(data);
		}
	}

	public void bind(){
		glBindTexture(GL_TEXTURE_2D, this.textureID);
	}

	public void unbind(){
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public void destroy() {
		GL13.glDeleteTextures(this.textureID);
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public int getTextureID() {
		return textureID;
	}

	public int getChannels() {
		return channels;
	}

	public ByteBuffer getData() {
		return data;
	}
}