package com.peyo.home3d;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class Screen extends GLES20 {

    private float coords[] = {
            -8, -4.5f, 0,  -8, 4.5f, 0,  8, 4.5f, 0,  8, -4.5f, 0  };

    private final short order[] = {
            0, 1, 2, 0, 2, 3 };

    private final float texmap[] = {
            1, 1,  1, 0,  0, 0,  0, 1};

    private int vertexVBO[] = new int[1];
    private int orderVBO[] =  new int[1];
    private int texmapVBO[] =  new int[1];

    private int mProgram;
    private int aPositionHandle;
    private int aTexCoordHandle;
    private int uMVPMatrixHandle;

    public Screen(float xoffset, float yoffset, int program)  {
        coords[0] -= xoffset; coords[3] -= xoffset; coords[6] -= xoffset; coords[9] -= xoffset;
        coords[1] += yoffset; coords[4] += yoffset; coords[7] += yoffset; coords[10] += yoffset;

        mProgram = program;
        uMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        aPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        aTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoord");

        GLToolbox.loadFloatVBO(vertexVBO, coords);
        GLToolbox.loadFloatVBO(texmapVBO, texmap);
        GLToolbox.loadElementVBO(orderVBO, order);
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
        glBindBuffer(GL_ARRAY_BUFFER, vertexVBO[0]);
        glVertexAttribPointer(aPositionHandle, 3, GL_FLOAT, false, 3*4, 0);
        glBindBuffer(GL_ARRAY_BUFFER, texmapVBO[0]);
        glVertexAttribPointer(aTexCoordHandle, 2, GL_FLOAT, false, 2*4, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, orderVBO[0]);
        glDrawElements(GL_TRIANGLES, order.length, GLES20.GL_UNSIGNED_SHORT, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }
}
