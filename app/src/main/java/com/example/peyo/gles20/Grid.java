package com.example.peyo.gles20;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.FloatBuffer;

public class Grid extends GLES20 {

    static float coords[] = {
            -4,0,0, 4,0,0,  0,0,-4, 0,0,4};

    static float h = 0.5f;
    static float colors[] = {
            h,h,h,1, h,h,h,1, h,h,h,1, h,h,h,1};

    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;

    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private float[] mMVPMatrix;

    public Grid(int position, int color, int mvp) {
        vertexBuffer = GLToolbox.loadBuffer(coords);
        colorBuffer = GLToolbox.loadBuffer(colors);

        mPositionHandle = position;
        mColorHandle = color;
        mMVPMatrixHandle = mvp;
    }

    public void draw(float[] mvpMatrix) {
        mMVPMatrix = mvpMatrix;

        glVertexAttribPointer(mPositionHandle, 3, GL_FLOAT, false,
                3 * 4, vertexBuffer);
        glVertexAttribPointer(mColorHandle, 4, GL_FLOAT, false,
                4 * 4, colorBuffer);
        glLineWidth(3);

        for (float f=-4; f<=4; f++) {
            translate(0, 0, f);
            glDrawArrays(GL_LINES, 0, 2);

            translate(f, 0, 0);
            glDrawArrays(GL_LINES, 2, 2);
        }
    }

    private void translate(float x, float y, float z) {
        float[] mvpMatrix = new float[16];
        Matrix.translateM(mvpMatrix, 0, mMVPMatrix, 0, x, y, z);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
    }
}
