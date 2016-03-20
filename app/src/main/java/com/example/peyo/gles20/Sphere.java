package com.example.peyo.gles20;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Sphere extends GLES20 {
    private SphereModel mModel;

    private int vertexVBO[] = new int[1];
    private int orderVBO[] =  new int[1];

    private int texId;
    private int mSamplerHandle;
    private int mPositionHandle;
    private int mTexCoordHandle;

    public Sphere(int position, int tex) {
        mModel = new SphereModel(12, 0, 0, 0, 1, 1);

        FloatBuffer fbuffer = mModel.getVertices();
        glGenBuffers(1, vertexVBO, 0);
        glBindBuffer(GL_ARRAY_BUFFER, vertexVBO[0]);
        glBufferData(GL_ARRAY_BUFFER, fbuffer.capacity() * 4, fbuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        ShortBuffer sbuffer = mModel.getIndices()[0];
        glGenBuffers(1, orderVBO, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, orderVBO[0]);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, sbuffer.capacity() * 2, sbuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        mPositionHandle = position;
        mTexCoordHandle = tex;
    }

    public void setTexture(int handle, Bitmap bmp) {
        mSamplerHandle = handle;

        int[] textures = new int[1];
        glGenTextures(1, textures, 0);
        texId = textures[0];
        glBindTexture(GL_TEXTURE_2D, texId);
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bmp, 0);

        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    }

    public void draw() {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texId);
        glUniform1i(mSamplerHandle, 0);

        glBindBuffer(GL_ARRAY_BUFFER, vertexVBO[0]);
        glVertexAttribPointer(mPositionHandle, 3, GL_FLOAT, false, mModel.getVeticesStride(), 0);
        glVertexAttribPointer(mTexCoordHandle, 2, GL_FLOAT, false, mModel.getVeticesStride(), 3*4);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, orderVBO[0]);
        glDrawElements(GL_TRIANGLES, mModel.getNumIndices()[0], GLES20.GL_UNSIGNED_SHORT, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }
}
