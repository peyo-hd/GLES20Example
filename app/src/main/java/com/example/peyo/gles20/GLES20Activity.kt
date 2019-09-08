package com.example.peyo.gles20

import android.app.Activity
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.Bundle
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLES20Activity : Activity() , GLSurfaceView.Renderer {

    private var mProgram0: Int = 0
    private lateinit var mTriangle: Triangle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = GLSurfaceView(this)
        view.setEGLContextClientVersion(2)
        view.setRenderer(this)
        setContentView(view)
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)

        mProgram0 = GLToolbox.createProgram(readShader(R.raw.color_vertex_shader),
                readShader(R.raw.color_fragment_shader))
        val positionHandle = GLES20.glGetAttribLocation(mProgram0, "aPosition")
        val colorHandle = GLES20.glGetUniformLocation(mProgram0, "vColor")
        mTriangle = Triangle(positionHandle, colorHandle)
        GLES20.glEnableVertexAttribArray(positionHandle)
    }

    override fun onDrawFrame(unused: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glUseProgram(mProgram0)
        mTriangle.draw()
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDestroy() {
        super.onDestroy()
        GLES20.glDeleteProgram(mProgram0)
    }

    private fun readShader(resId: Int): String {
        val inputStream = resources.openRawResource(resId)
        try {
            val reader = BufferedReader(InputStreamReader(inputStream))
            val sb = StringBuilder()
            var line: String?
            do {
                line = reader.readLine();
                if (line != null)
                    sb.append(line).append("\n")
            } while (line != null)
            reader.close()
            return sb.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }
}