/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// copied from android.openglperf.cts
package com.example.peyo.gles20

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/*
 * Class for generating a sphere model for given input params
 * The generated class will have vertices and indices
 * Vertices data is composed of vertex coordinates in x, y, z followed by
 *  texture coordinates s, t for each vertex
 * Indices store vertex indices for the whole sphere.
 * Formula for generating sphere is originally coming from source code of
 * OpenGL ES2.0 Programming guide
 * which is available from http://code.google.com/p/opengles-book-samples/,
 * but some changes were made to make texture look right.
 */
class SphereModel(nSlices: Int, x: Float, y: Float, z: Float, r: Float, numIndexBuffers: Int) {
    val vertices: FloatBuffer
    val indices: Array<ShortBuffer?>
    val numIndices: IntArray
    val totalIndices: Int

    /*
     * @param nSlices how many slice in horizontal direction.
     *                The same slice for vertical direction is applied.
     *                nSlices should be > 1 and should be <= 180
     * @param x,y,z the origin of the sphere
     * @param r the radius of the sphere
     */
    init {
        val iMax = nSlices + 1
        val nVertices = iMax * iMax
        if (nVertices > Short.MAX_VALUE) {
            // this cannot be handled in one vertices / indices pair
            throw RuntimeException("nSlices $nSlices too big for vertex")
        }
        totalIndices = nSlices * nSlices * 6
        val angleStepI = Math.PI.toFloat() / nSlices
        val angleStepJ = 2.0f * Math.PI.toFloat() / nSlices

        // 3 vertex coords + 2 texture coords
        vertices = ByteBuffer.allocateDirect(nVertices * 5 * FLOAT_SIZE)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        this.indices = arrayOfNulls(numIndexBuffers)
        numIndices = IntArray(numIndexBuffers)
        // first evenly distribute to n-1 buffers, then put remaining ones to the last one.
        val noIndicesPerBuffer = totalIndices / numIndexBuffers / 6 * 6
        for (i in 0 until numIndexBuffers - 1) {
            numIndices[i] = noIndicesPerBuffer
        }
        numIndices[numIndexBuffers - 1] = totalIndices - noIndicesPerBuffer *
                (numIndexBuffers - 1)
        for (i in 0 until numIndexBuffers) {
            indices[i] = ByteBuffer.allocateDirect(numIndices[i] * SHORT_SIZE)
                .order(ByteOrder.nativeOrder()).asShortBuffer()
        }
        // calling put for each float took too much CPU time, so put by line instead
        val vLineBuffer = FloatArray(iMax * 5)
        for (i in 0 until iMax) {
            for (j in 0 until iMax) {
                val vertexBase = j * 5
                val sini = Math.sin((angleStepI * i).toDouble()).toFloat()
                val sinj = Math.sin((angleStepJ * j).toDouble()).toFloat()
                val cosi = Math.cos((angleStepI * i).toDouble()).toFloat()
                val cosj = Math.cos((angleStepJ * j).toDouble()).toFloat()
                // vertex x,y,z
                vLineBuffer[vertexBase + 0] = x + r * sini * sinj
                vLineBuffer[vertexBase + 1] = y + r * cosi
                vLineBuffer[vertexBase + 2] = z + r * sini * cosj
                // texture s,t
                vLineBuffer[vertexBase + 3] = j.toFloat() / nSlices.toFloat()
                vLineBuffer[vertexBase + 4] = i.toFloat() / nSlices.toFloat()
            }
            vertices.put(vLineBuffer, 0, vLineBuffer.size)
        }
        val indexBuffer = ShortArray(max(numIndices))
        var index = 0
        var bufferNum = 0
        for (i in 0 until nSlices) {
            for (j in 0 until nSlices) {
                val i1 = i + 1
                val j1 = j + 1
                if (index >= numIndices[bufferNum]) {
                    // buffer ready for moving to target
                    indices[bufferNum]!!.put(indexBuffer, 0, numIndices[bufferNum])
                    // move to the next one
                    index = 0
                    bufferNum++
                }
                indexBuffer[index++] = (i * iMax + j).toShort()
                indexBuffer[index++] = (i1 * iMax + j).toShort()
                indexBuffer[index++] = (i1 * iMax + j1).toShort()
                indexBuffer[index++] = (i * iMax + j).toShort()
                indexBuffer[index++] = (i1 * iMax + j1).toShort()
                indexBuffer[index++] = (i * iMax + j1).toShort()
            }
        }
        indices[bufferNum]!!.put(indexBuffer, 0, numIndices[bufferNum])
        vertices.position(0)
        for (i in 0 until numIndexBuffers) {
            indices[i]!!.position(0)
        }
    }

    val veticesStride: Int
        get() = 5 * FLOAT_SIZE

    private fun max(array: IntArray): Int {
        var max = array[0]
        for (i in 1 until array.size) {
            if (array[i] > max) max = array[i]
        }
        return max
    }

    companion object {
        const val FLOAT_SIZE = 4
        const val SHORT_SIZE = 2
    }
}