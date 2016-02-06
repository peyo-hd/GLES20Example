package com.example.peyo.gles20;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Square extends GLES20 {

    static float coords[] = {
            -1, -1, -1,  -1, -1, 1,  1, -1, 1,  1, -1, -1  };

    private final short order[] = {
            0, 1, 2, 0, 2, 3 };

    static float texmap[] = {
            0, 1,  1, 1,  1, 0,  0, 0};

    private FloatBuffer vertexBuffer;
    private ShortBuffer orderBuffer;
    private FloatBuffer texmapBuffer;
    private int texId;
    private int mSamplerHandle;
    private int mPositionHandle;
    private int mTexCoordHandle;

    public Square(int position, int tex) {
        vertexBuffer = GLToolbox.loadBuffer(coords);
        orderBuffer = GLToolbox.loadBuffer(order);
        texmapBuffer = GLToolbox.loadBuffer(texmap);

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

        glVertexAttribPointer(mPositionHandle, 3, GL_FLOAT, false,
                3 * 4, vertexBuffer);
        glVertexAttribPointer(mTexCoordHandle, 2, GL_FLOAT, false,
                2 * 4, texmapBuffer);

        glDrawElements(GL_TRIANGLES, order.length, GLES20.GL_UNSIGNED_SHORT, orderBuffer);
    }
}
