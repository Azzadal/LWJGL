package engine.io;

import engine.graphics.Texture;
import engine.graphics.Mesh;
import engine.graphics.Vertex;
//import engine.maths.Vector2f;
//import engine.maths.Vector3f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.assimp.*;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModelLoader {
    public static Mesh loadModel(String filePath, String texturePath) {
        AIScene scene = Assimp.aiImportFile(filePath, Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate);

        if (scene == null) System.err.println("Couldn't load model at " + filePath);

        assert scene != null;
        AIMesh mesh = AIMesh.create(Objects.requireNonNull(scene.mMeshes()).get(0));
        int vertexCount = mesh.mNumVertices();

        AIVector3D.Buffer vertices = mesh.mVertices();
        AIVector3D.Buffer normals = mesh.mNormals();

        Vertex[] vertexList = new Vertex[vertexCount];

        for (int i = 0; i < vertexCount; i++) {
            AIVector3D vertex = vertices.get(i);
            Vector3f meshVertex = new Vector3f(vertex.x(), vertex.y(), vertex.z());

            assert normals != null;
            AIVector3D normal = normals.get(i);
            Vector3f meshNormal = new Vector3f(normal.x(), normal.y(), normal.z());


            Vector2f meshTextureCoord = new Vector2f(0, 0);
            AIVector3D.Buffer textCoords = mesh.mTextureCoords(0);

                AIVector3D texture = Objects.requireNonNull(mesh.mTextureCoords(0)).get(i);
                meshTextureCoord.x = texture.x();
                meshTextureCoord.y = 1 - texture.y();

            vertexList[i] = new Vertex(meshVertex, meshNormal, meshTextureCoord);
        }

        int faceCount = mesh.mNumFaces();
        AIFace.Buffer indices = mesh.mFaces();
        int[] indicesList = new int[faceCount * 3];

        for (int i = 0; i < faceCount; i++) {
            AIFace face = indices.get(i);
            IntBuffer buffer = face.mIndices();
            indicesList[i * 3 + 0] = face.mIndices().get(0);
            indicesList[i * 3 + 1] = face.mIndices().get(1);
            indicesList[i * 3 + 2] = face.mIndices().get(2);
        }
        return new Mesh(vertexList, indicesList, new Texture(texturePath));
    }
}
