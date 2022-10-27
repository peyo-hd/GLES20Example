package com.example.peyo.gles20

import android.opengl.GLES20

class Column(coords: FloatArray, private val mPositionHandle: Int, private val mColorHandle: Int) :
    GLES20() {
    private val vertexVBO = IntArray(1)
    private val mNumverts: Int

    init {
        mNumverts = coords.size / 3
        GLToolbox.loadFloatVBO(vertexVBO, coords)
    }

    fun draw() {
        glBindBuffer(GL_ARRAY_BUFFER, vertexVBO[0])
        glVertexAttribPointer(mPositionHandle, 3, GL_FLOAT, false, 3 * 4, 0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glUniform4fv(mColorHandle, 1, color, 0)
        glDrawArrays(GL_TRIANGLES, 0, mNumverts)
    }

    companion object {
        var color = floatArrayOf(
            0.5f, 0f, 0.5f, 1f
        )
    }
}