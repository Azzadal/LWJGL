package main.java.engine.utils;

import org.lwjglx.BufferUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Objects;

public class FileUtils {
	public static String loadAsString(String path) {
		StringBuilder result = new StringBuilder();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(
				FileUtils.class.getResourceAsStream(path))))) {
			String line = "";
			while ((line = reader.readLine()) != null) {
				result.append(line).append("\n");
			}
		} catch (IOException e) {
			System.err.println("Couldn't find the file at " + path);
		}

		return result.toString();
	}

	public static ByteBuffer resourceToByteBuffer(final String resource) throws IOException
	{
		File file = new File(resource);

		FileInputStream fileInputStream = new FileInputStream(file);
		FileChannel fileChannel = fileInputStream.getChannel();

		ByteBuffer buffer = BufferUtils.createByteBuffer((int) fileChannel.size() + 1);

		while (fileChannel.read(buffer) != -1) {
			;
		}

		fileInputStream.close();
		fileChannel.close();
		buffer.flip();

		return buffer;
	}
}
