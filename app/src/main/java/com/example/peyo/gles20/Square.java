package com.example.peyo.gles20;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class Square extends GLES20 {

    static float coords[] = {
            -1, -1, -1,  -1, -1, 1,  1, -1, 1,  1, -1, -1  };

    private final short order[] = {
            0, 1, 2, 0, 2, 3 };

    static float texmap[] = {
            0, 1,  1, 1,  1, 0,  0, 0};

    private int vertexVBO[] = new int[1];
    private int orderVBO[] =  new int[1];
    private int texmapVBO[] =  new int[1];

    private int texId;
    private int mSamplerHandle;
    private int mPositionHandle;
    private int mTexCoordHandle;

    public Square(int position, int tex) {
        GLToolbox.loadFloatVBO(vertexVBO, coords);
        GLToolbox.loadFloatVBO(texmapVBO, texmap);
        GLToolbox.loadElementVBO(orderVBO, order);

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
        glVertexAttribPointer(mPositionHandle, 3, GL_FLOAT, false, 3*4, 0);
        glBindBuffer(GL_ARRAY_BUFFER, texmapVBO[0]);
        glVertexAttribPointer(mTexCoordHandle, 2, GL_FLOAT, false, 2*4, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, orderVBO[0]);
        glDrawElements(GL_TRIANGLES, order.length, GLES20.GL_UNSIGNED_SHORT, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }
}
