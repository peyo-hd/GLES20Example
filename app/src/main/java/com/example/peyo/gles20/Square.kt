package com.example.peyo.gles20

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils

class Square(position: Int, tex: Int) : GLES20() {
    private var coords = floatArrayOf(-1f, -1f, -1f, -1f, -1f, 1f, 1f, -1f, 1f, 1f, -1f, -1f)
    private val order = shortArrayOf(0, 1, 2, 0, 2, 3)
    private var texmap = floatArrayOf(0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f)

    private val vertexVBO = IntArray(1)
    private val orderVBO = IntArray(1)
    private val texmapVBO = IntArray(1)

    private var texId = 0
    private var mSamplerHandle = 0
    private val mPositionHandle: Int
    private val mTexCoordHandle: Int

    init {
        GLToolbox.loadFloatVBO(vertexVBO, coords)
        GLToolbox.loadFloatVBO(texmapVBO, texmap)
        GLToolbox.loadElementVBO(orderVBO, order)
        mPositionHandle = position
        mTexCoordHandle = tex
    }

    fun setTexture(handle: Int, bmp: Bitmap?) {
        mSamplerHandle = handle
        val textures = IntArray(1)
        glGenTextures(1, textures, 0)
        texId = textures[0]
        glBindTexture(GL_TEXTURE_2D, texId)
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bmp, 0)
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR.toFloat())
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR.toFloat())
    }

    fun draw() {
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, texId)
        glUniform1i(mSamplerHandle, 0)
        glBindBuffer(GL_ARRAY_BUFFER, vertexVBO[0])
        glVertexAttribPointer(mPositionHandle, 3, GL_FLOAT, false, 3 * 4, 0)
        glBindBuffer(GL_ARRAY_BUFFER, texmapVBO[0])
        glVertexAttribPointer(mTexCoordHandle, 2, GL_FLOAT, false, 2 * 4, 0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, orderVBO[0])
        glDrawElements(GL_TRIANGLES, order.size, GL_UNSIGNED_SHORT, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }
}