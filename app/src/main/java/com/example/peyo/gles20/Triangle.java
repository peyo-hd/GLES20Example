package com.example.peyo.gles20;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

public class Triangle extends GLES20 {

    static float coords[] = {
            0, 1, -1,  -1, -1, 1,  1, -1, 1  };

    static float colors[] = {
            0, 1, 0, 1,  0, 0, 1, 1,  1, 0, 0, 1 };

    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;

    private int mPositionHandle;
    private int mColorHandle;

    public Triangle(int position, int color) {
        vertexBuffer = GLToolbox.loadBuffer(coords);
        colorBuffer = GLToolbox.loadBuffer(colors);

        mPositionHandle = position;
        mColorHandle = color;
    }

    public void draw() {
        glVertexAttribPointer(mPositionHandle, 3, GL_FLOAT, false,
                3 * 4, vertexBuffer);
        glVertexAttribPointer(mColorHandle, 4, GL_FLOAT, false,
                4 * 4, colorBuffer);
        glDrawArrays(GL_TRIANGLES, 0, 3);
    }
}
