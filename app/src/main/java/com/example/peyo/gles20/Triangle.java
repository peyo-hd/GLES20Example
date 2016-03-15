package com.example.peyo.gles20;

import android.opengl.GLES20;

public class Triangle extends GLES20 {

    static float coords[] = {
            0, 1, -1,  -1, -1, 1,  1, -1, 1  };

    static float colors[] = {
            0, 1, 0, 1,  0, 0, 1, 1,  1, 0, 0, 1 };

    private int vertexVBO[] = new int[1];
    private int colorVBO[] = new int[1];

    private int mPositionHandle;
    private int mColorHandle;

    public Triangle(int position, int color) {
        GLToolbox.loadFloatVBO(vertexVBO, coords);
        GLToolbox.loadFloatVBO(colorVBO, colors);

        mPositionHandle = position;
        mColorHandle = color;
    }

    public void draw() {
        glBindBuffer(GL_ARRAY_BUFFER, vertexVBO[0]);
        glVertexAttribPointer(mPositionHandle, 3, GL_FLOAT, false, 3*4, 0);
        glBindBuffer(GL_ARRAY_BUFFER, colorVBO[0]);
        glVertexAttribPointer(mColorHandle, 4, GL_FLOAT, false, 4*4, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glDrawArrays(GL_TRIANGLES, 0, 3);
    }
}
