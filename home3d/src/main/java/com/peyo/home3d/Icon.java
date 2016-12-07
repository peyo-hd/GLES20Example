package com.peyo.home3d;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.FloatBuffer;

public class Icon extends GLES20 {

    private float coords[] = {
            -1, -1, 0,  -1, 1, 0,  1, 1, 0,  1, -1, 0  };

    private float xoffset = 0;
    private float yoffset = 0;

    private final short order[] = {
            0, 1, 2, 0, 2, 3 };

    private final float texmap[] = {
            1, 1,  1, 0,  0, 0,  0, 1};

    private FloatBuffer vertexBuffer;
    private int orderVBO[] =  new int[1];
    private int texmapVBO[] =  new int[1];

    private int mProgram;
    private int aPositionHandle;
    private int uSamplerHandle;
    private int aTexCoordHandle;
    private int uMVPMatrixHandle;

    private int texId;

    public Icon(float xsize, float ysize, float xoff, float yoff, int program)  {
        coords[0] *= xsize/2; coords[3] *= xsize/2; coords[6] *= xsize/2; coords[9] *= xsize/2;
        coords[1] *= ysize/2; coords[4] *= ysize/2; coords[7] *= ysize/2; coords[10] *= ysize/2;
        xoffset = xoff; yoffset = yoff;

        mProgram = program;
        uMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        aPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        aTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoord");
        uSamplerHandle = GLES20.glGetUniformLocation(mProgram, "uSamplerTex");

        setVertex();
        GLToolbox.loadFloatVBO(texmapVBO, texmap);
        GLToolbox.loadElementVBO(orderVBO, order);
    }

    public void bump(float z) {
        coords[2] = -z/2 ; coords[5] = -z/2; coords[8] = -z/2; coords[11] = -z/2;
        setVertex();
    }

    private void setVertex() {
        float vertex[] = coords.clone();
        float rate = (vertex[2] == 0) ? 1 : 1.1f;
        vertex[0] *= rate;
        vertex[3] *= rate;
        vertex[6] *= rate;
        vertex[9] *= rate;
        vertex[1] *= rate;
        vertex[4] *= rate;
        vertex[7] *= rate;
        vertex[10] *= rate;

        vertex[0] -= xoffset;
        vertex[3] -= xoffset;
        vertex[6] -= xoffset;
        vertex[9] -= xoffset;
        vertex[1] += yoffset;
        vertex[4] += yoffset;
        vertex[7] += yoffset;
        vertex[10] += yoffset;
        vertexBuffer = GLToolbox.loadBuffer(vertex);
    }

    public void setTexture(Context context, int res) {
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), res);

        int[] textures = new int[1];
        glGenTextures(1, textures, 0);
        texId = textures[0];
        glBindTexture(GL_TEXTURE_2D, texId);
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bmp, 0);

        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        bmp.recycle();
    }

    public void enableAttrib() {
        glEnableVertexAttribArray(aPositionHandle);
        glEnableVertexAttribArray(aTexCoordHandle);
    }

    public void setMvp(float[] mvpMatrix) {
        glUseProgram(mProgram);
        glUniformMatrix4fv(uMVPMatrixHandle, 1, false, mvpMatrix, 0);
    }

    public void draw() {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texId);
        glUniform1i(uSamplerHandle, 0);

        glVertexAttribPointer(aPositionHandle, 3, GL_FLOAT, false, 3*4, vertexBuffer);
        glBindBuffer(GL_ARRAY_BUFFER, texmapVBO[0]);
        glVertexAttribPointer(aTexCoordHandle, 2, GL_FLOAT, false, 2*4, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, orderVBO[0]);
        glDrawElements(GL_TRIANGLES, order.length, GLES20.GL_UNSIGNED_SHORT, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }
}
