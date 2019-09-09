package com.example.peyo.gles20

import android.opengl.GLES20
import java.nio.FloatBuffer

val triangleCoords = floatArrayOf (
        0f, 2f, 0f,
        -1f, -1f, 0f,
        1f, -1f, 0f)

val triangleColors = floatArrayOf (
        0f, 1f, 0f, 1f,
        0f, 0f, 1f, 1f,
        1f, 0f, 0f, 1f )


class Triangle(val positionHandle: Int, val colorHandle: Int) : GLES20() {

    private val vertexCount: Int = triangleCoords.size / 3
    private val vertexBuffer: FloatBuffer = GLToolbox.loadBuffer(triangleCoords)
    private val colorBuffer: FloatBuffer = GLToolbox.loadBuffer(triangleColors)

    fun draw() {
        glVertexAttribPointer(positionHandle, 3, GL_FLOAT, false,
                3 * 4, vertexBuffer)
        glVertexAttribPointer(colorHandle, 4, GL_FLOAT, false,
                4 * 4, colorBuffer)
        glDrawArrays(GL_TRIANGLES, 0, vertexCount)
    }
}