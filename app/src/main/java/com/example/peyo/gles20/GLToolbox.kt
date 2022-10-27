package com.example.peyo.gles20

import android.opengl.GLES20
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer


class GLToolbox : GLES20() {
    companion object {

        fun checkGLError(tag: String, label: String) {
            val error: Int = glGetError()
            if (error != GL_NO_ERROR) {
                Log.e(tag, "$label: glError $error")
                throw RuntimeException("$label: glError $error")
            }
        }

        private fun loadShader(shaderType: Int, source: String): Int {
            val shader = glCreateShader(shaderType)
            if (shader != 0) {
                glShaderSource(shader, source)
                glCompileShader(shader)
                val compiled = IntArray(1)
                glGetShaderiv(shader, GL_COMPILE_STATUS, compiled, 0)
                if (compiled[0] == 0) {
                    val info = glGetShaderInfoLog(shader)
                    glDeleteShader(shader)
                    throw RuntimeException("Could not compile shader $shaderType:$info")
                }
            }
            return shader
        }

        fun createProgram(vertexSource: String, fragmentSource: String): Int {
            val vertexShader = loadShader(GL_VERTEX_SHADER, vertexSource)
            if (vertexShader == 0) {
                return 0
            }
            val pixelShader = loadShader(GL_FRAGMENT_SHADER, fragmentSource)
            if (pixelShader == 0) {
                return 0
            }

            val program = glCreateProgram()
            if (program != 0) {
                glAttachShader(program, vertexShader)
                glAttachShader(program, pixelShader)
                glLinkProgram(program)
                val linkStatus = IntArray(1)
                glGetProgramiv(program, GL_LINK_STATUS, linkStatus,
                        0)
                if (linkStatus[0] != GL_TRUE) {
                    val info = glGetProgramInfoLog(program)
                    glDeleteProgram(program)
                    throw RuntimeException("Could not link program: $info")
                }
            }
            return program
        }

        fun loadBuffer(farray: FloatArray): FloatBuffer {
            val bbuffer = ByteBuffer.allocateDirect(farray.size * 4)
            bbuffer.order(ByteOrder.nativeOrder())

            val fbuffer = bbuffer.asFloatBuffer()
            fbuffer.put(farray)
            fbuffer.position(0)
            return fbuffer
        }

        fun loadBuffer(sarray: ShortArray): ShortBuffer {
            val bbuffer = ByteBuffer.allocateDirect(sarray.size * 2)
            bbuffer.order(ByteOrder.nativeOrder())

            val sbuffer = bbuffer.asShortBuffer()
            sbuffer.put(sarray)
            sbuffer.position(0)
            return sbuffer
        }

        fun loadFloatVBO(vbo: IntArray, farray: FloatArray) {
            val buffer = loadBuffer(farray)
            glGenBuffers(1, vbo, 0)
            glBindBuffer(GL_ARRAY_BUFFER, vbo[0])
            glBufferData(GL_ARRAY_BUFFER, buffer.capacity() * 4, buffer, GL_STATIC_DRAW)
            glBindBuffer(GL_ARRAY_BUFFER, 0)
        }

        fun loadFloatVBO(vbo: IntArray, fbuffer: FloatBuffer) {
            glGenBuffers(1, vbo, 0)
            glBindBuffer(GL_ARRAY_BUFFER, vbo[0])
            glBufferData(GL_ARRAY_BUFFER, fbuffer.capacity() * 4, fbuffer, GL_STATIC_DRAW)
            glBindBuffer(GL_ARRAY_BUFFER, 0)
        }

        fun loadElementVBO(vbo: IntArray, sarray: ShortArray) {
            val buffer = loadBuffer(sarray)
            glGenBuffers(1, vbo, 0)
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo[0])
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer.capacity() * 2, buffer, GL_STATIC_DRAW)
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
        }

        fun loadElementVBO(vbo: IntArray, sbuffer: ShortBuffer) {
            glGenBuffers(1, vbo, 0)
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo[0])
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, sbuffer.capacity() * 2, sbuffer, GL_STATIC_DRAW)
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
        }
    }
}
