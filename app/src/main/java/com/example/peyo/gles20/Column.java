package com.example.peyo.gles20;

import android.opengl.GLES20;

public class Column extends GLES20 {

    static float color[] = {
            0.5f, 0, 0.5f, 1 };

    private int vertexVBO[] = new int[1];

    private int mNumverts;
    private int mPositionHandle;
    private int mColorHandle;

    public Column(float coords[], int position, int color) {
        mPositionHandle = position;
        mColorHandle = color;
        mNumverts = coords.length/3;
        GLToolbox.loadFloatVBO(vertexVBO, coords);
    }

    public void draw() {
        glBindBuffer(GL_ARRAY_BUFFER, vertexVBO[0]);
        glVertexAttribPointer(mPositionHandle, 3, GL_FLOAT, false, 3*4, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glUniform4fv(mColorHandle, 1, color, 0);

        glDrawArrays(GL_TRIANGLES, 0, mNumverts);
    }
}
