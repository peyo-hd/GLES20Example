package com.example.peyo.gles20

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.FloatBuffer


class Grid(val positionHandle: Int, val colorHandle: Int, val matrixHandle: Int) : GLES20() {
    val coords = floatArrayOf (-4f,0f,0f,  4f,0f,0f,  0f,0f,-4f,  0f,0f,4f )
    val h = 0.5f;
    val colors = floatArrayOf (h,h,h,1f,  h,h,h,1f,  h,h,h,1f,  h,h,h,1f)
    private val vertexCount: Int = coords.size / 3
    private val vertexBuffer: FloatBuffer = GLToolbox.loadBuffer(coords)
    private val colorBuffer: FloatBuffer = GLToolbox.loadBuffer(colors)

    fun draw(vpMatrix: FloatArray) {
        glVertexAttribPointer(positionHandle, 3, GL_FLOAT, false,
                3 * 4, vertexBuffer)
        glVertexAttribPointer(colorHandle, 4, GL_FLOAT, false,
                4 * 4, colorBuffer)
        glLineWidth(3f)

        for (i in -4 .. 4) {
            translate(vpMatrix, 0f, 0f, i.toFloat())
            glDrawArrays(GL_LINES, 0, 2)
            translate(vpMatrix, i.toFloat(),0f, 0f)
            glDrawArrays(GL_LINES, 2, 2)
        }
    }

    private fun translate(vpMatrix: FloatArray, x: Float, y: Float, z: Float) {
        val transMatrix = FloatArray(16)
        val vpMatrix2 = FloatArray(16)
        Matrix.setIdentityM(transMatrix, 0)
        Matrix.translateM(transMatrix, 0, x, y, z)
        Matrix.multiplyMM(vpMatrix2, 0, vpMatrix, 0, transMatrix, 0)
        glUniformMatrix4fv(matrixHandle, 1, false, vpMatrix2, 0)
    }
}
