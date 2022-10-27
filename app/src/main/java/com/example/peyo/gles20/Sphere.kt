package com.example.peyo.gles20

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils

class Sphere(position: Int, tex: Int) : GLES20() {
    private val vertexVBO = IntArray(1)
    private val orderVBO = IntArray(1)

    private var texId = 0
    private var mSamplerHandle = 0
    private val mPositionHandle: Int
    private val mTexCoordHandle: Int

    private val mModel: SphereModel = SphereModel(12, 0f, 0f, 0f, 1f, 1)

    init {
        GLToolbox.loadFloatVBO(vertexVBO, mModel.vertices)
        GLToolbox.loadElementVBO(orderVBO, mModel.indices[0]!!)
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
        glVertexAttribPointer(mPositionHandle, 3, GL_FLOAT, false, mModel.veticesStride, 0)
        glVertexAttribPointer(mTexCoordHandle, 2, GL_FLOAT, false, mModel.veticesStride, 3 * 4)
        glBindBuffer(GL_ARRAY_BUFFER, 0)

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, orderVBO[0])
        glDrawElements(GL_TRIANGLES, mModel.numIndices[0], GL_UNSIGNED_SHORT, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }
}