package com.example.peyo.gles20

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

const val COORDS_PER_VERTEX = 3
var triangleCoords = floatArrayOf(
        0.0f, 0.622008459f, 0.0f,
        -0.5f, -0.311004243f, 0.0f,
        0.5f, -0.311004243f, 0.0f
)



class Triangle(val positionHandle: Int, val colorHanlde: Int) : GLES20() {

    private val vertexBuffer: FloatBuffer =
            ByteBuffer.allocateDirect(triangleCoords.size *4).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(triangleCoords)
                    position(0)
                }

            }
    private val color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f)

    private val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    fun draw() {
        glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GL_FLOAT, false,
                vertexStride, vertexBuffer)
        glUniform4fv(colorHanlde, 1, color, 0)
        glDrawArrays(GL_TRIANGLES, 0, vertexCount)
    }
}