package com.lakeel.altla.sample.tango.planedetection;

import org.rajawali3d.Object3D;
import org.rajawali3d.math.vector.Vector3;

/**
 * Rajawali の Plane で定義された UV 座標ではテクスチャが左右反転するため、Rajawali の Plane を参考に改変クラスを作成。
 */
public final class Plane extends Object3D {

    private float mWidth;

    private float mHeight;

    private int mSegmentsW;

    private int mSegmentsH;

    private int mNumTextureTiles;

    private boolean mCreateTextureCoords;

    private boolean mCreateVertexColorBuffer;

    private Vector3.Axis mUpAxis;

    public Plane(float width, float height, int segmentsW, int segmentsH) {
        this(width, height, segmentsW, segmentsH, Vector3.Axis.Z, true, false, 1);
    }

    public Plane(float width, float height, int segmentsW, int segmentsH, Vector3.Axis upAxis,
                 boolean createTextureCoordinates,
                 boolean createVertexColorBuffer, int numTextureTiles) {
        this(width, height, segmentsW, segmentsH, upAxis, createTextureCoordinates, createVertexColorBuffer,
             numTextureTiles, true);
    }

    public Plane(float width, float height, int segmentsW, int segmentsH, Vector3.Axis upAxis,
                 boolean createTextureCoordinates,
                 boolean createVertexColorBuffer, int numTextureTiles, boolean createVBOs) {
        super();
        mWidth = width;
        mHeight = height;
        mSegmentsW = segmentsW;
        mSegmentsH = segmentsH;
        mUpAxis = upAxis;
        mCreateTextureCoords = createTextureCoordinates;
        mCreateVertexColorBuffer = createVertexColorBuffer;
        mNumTextureTiles = numTextureTiles;
        init(createVBOs);
    }

    private void init(boolean createVBOs) {
        int i, j;
        int numVertices = (mSegmentsW + 1) * (mSegmentsH + 1);
        float[] vertices = new float[numVertices * 3];
        float[] textureCoords = null;
        if (mCreateTextureCoords) {
            textureCoords = new float[numVertices * 2];
        }
        float[] normals = new float[numVertices * 3];
        float[] colors = null;
        if (mCreateVertexColorBuffer) {
            colors = new float[numVertices * 4];
        }
        int[] indices = new int[mSegmentsW * mSegmentsH * 6];
        int vertexCount = 0;
        int texCoordCount = 0;

        for (i = 0; i <= mSegmentsW; i++) {
            for (j = 0; j <= mSegmentsH; j++) {
                float v1 = ((float) i / (float) mSegmentsW - 0.5f) * mWidth;
                float v2 = ((float) j / (float) mSegmentsH - 0.5f) * mHeight;
                if (mUpAxis == Vector3.Axis.X) {
                    vertices[vertexCount] = 0;
                    vertices[vertexCount + 1] = v1;
                    vertices[vertexCount + 2] = v2;
                } else if (mUpAxis == Vector3.Axis.Y) {
                    vertices[vertexCount] = v1;
                    vertices[vertexCount + 1] = 0;
                    vertices[vertexCount + 2] = v2;
                } else if (mUpAxis == Vector3.Axis.Z) {
                    vertices[vertexCount] = v1;
                    vertices[vertexCount + 1] = v2;
                    vertices[vertexCount + 2] = 0;
                }

                if (mCreateTextureCoords) {
                    float u = (float) i / (float) mSegmentsW;
                    textureCoords[texCoordCount++] = u * mNumTextureTiles;
                    float v = (float) j / (float) mSegmentsH;
                    textureCoords[texCoordCount++] = (1.0f - v) * mNumTextureTiles;
                }

                normals[vertexCount] = mUpAxis == Vector3.Axis.X ? 1 : 0;
                normals[vertexCount + 1] = mUpAxis == Vector3.Axis.Y ? 1 : 0;
                normals[vertexCount + 2] = mUpAxis == Vector3.Axis.Z ? 1 : 0;

                vertexCount += 3;
            }
        }

        int colspan = mSegmentsH + 1;
        int indexCount = 0;

        for (int col = 0; col < mSegmentsW; col++) {
            for (int row = 0; row < mSegmentsH; row++) {
                int ul = col * colspan + row;
                int ll = ul + 1;
                int ur = (col + 1) * colspan + row;
                int lr = ur + 1;

                if (mUpAxis == Vector3.Axis.X || mUpAxis == Vector3.Axis.Z) {
                    indices[indexCount++] = ur;
                    indices[indexCount++] = lr;
                    indices[indexCount++] = ul;

                    indices[indexCount++] = lr;
                    indices[indexCount++] = ll;
                    indices[indexCount++] = ul;
                } else {
                    indices[indexCount++] = ur;
                    indices[indexCount++] = ul;
                    indices[indexCount++] = lr;

                    indices[indexCount++] = lr;
                    indices[indexCount++] = ul;
                    indices[indexCount++] = ll;
                }
            }
        }

        if (mCreateVertexColorBuffer) {
            int numColors = numVertices * 4;
            for (j = 0; j < numColors; j += 4) {
                colors[j] = 1.0f;
                colors[j + 1] = 1.0f;
                colors[j + 2] = 1.0f;
                colors[j + 3] = 1.0f;
            }
        }

        setData(vertices, normals, textureCoords, colors, indices, createVBOs);
    }
}
