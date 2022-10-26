package com.example.peyo.gles20

import android.app.Activity
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.Bundle
import android.os.SystemClock
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLES20Activity : Activity() , GLSurfaceView.Renderer {
    private val TAG = "GLES20Activity"

    private var mProgram0: Int = 0
    private var mMVPMatrixHandle0: Int = 0
    private lateinit var mTriangle: Triangle
    private lateinit var mGrid: Grid

    private var mProgram1: Int = 0
    private var mMVPMatrixHandle1: Int = 0
    private lateinit var mSquare: Square

    private var mRatio = 1.0f

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
        mMVPMatrixHandle0 = GLES20.glGetUniformLocation(mProgram0, "uMVPMatrix")
        val aPositionHandle0 = GLES20.glGetAttribLocation(mProgram0, "aPosition")
        val aColorHandle0 = GLES20.glGetAttribLocation(mProgram0, "aColor")
        GLES20.glEnableVertexAttribArray(aPositionHandle0)
        GLES20.glEnableVertexAttribArray(aColorHandle0)

        mGrid = Grid(aPositionHandle0, aColorHandle0, mMVPMatrixHandle0)
        mTriangle = Triangle(aPositionHandle0, aColorHandle0)
        GLToolbox.checkGLError(TAG, "Program and Object for Grid/Triangle")

        mProgram1 = GLToolbox.createProgram(readShader(R.raw.texture_vertex_shader),
                readShader(R.raw.texture_fragment_shader))
        mMVPMatrixHandle1 = GLES20.glGetUniformLocation(mProgram1, "uMVPMatrix")
        val aPositionHandle1 = GLES20.glGetAttribLocation(mProgram1, "aPosition")
        val aTexCoordHandle1 = GLES20.glGetAttribLocation(mProgram1, "aTexCoord")
        val uSamplerHandle1 = GLES20.glGetUniformLocation(mProgram1, "uSamplerTex")
        GLES20.glEnableVertexAttribArray(aPositionHandle1)
        GLES20.glEnableVertexAttribArray(aTexCoordHandle1)

        mSquare = Square(aPositionHandle1, aTexCoordHandle1)
        val bmp = BitmapFactory.decodeResource(resources, R.drawable.ground)
        mSquare.setTexture(uSamplerHandle1, bmp)
        bmp.recycle()
        GLToolbox.checkGLError(TAG, "Program and Object for Square/Sphere")

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glEnable(GLES20.GL_CULL_FACE)
    }

    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val vPMatrix = FloatArray(16)

    private val rotationMatrix = FloatArray(16)
    private fun updateAngle() {
        val time = SystemClock.uptimeMillis() % 4000L
        val angle = 0.090f * time.toInt()
        Matrix.setRotateM(rotationMatrix, 0, angle, 0f, 1f, 0f)
    }

    override fun onDrawFrame(unused: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        Matrix.setLookAtM(viewMatrix, 0, 6f, 6f, 6f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        Matrix.perspectiveM(projectionMatrix, 0, 30f, mRatio, 1f, 20f)
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        GLES20.glUseProgram(mProgram0)
        mGrid.draw(vPMatrix)

        updateAngle()
        Matrix.multiplyMM(vPMatrix, 0, vPMatrix, 0, rotationMatrix, 0)

        GLES20.glUseProgram(mProgram1)
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle1, 1, false, vPMatrix, 0)
        mSquare.draw()

        GLES20.glUseProgram(mProgram0)
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle0, 1, false, vPMatrix, 0)
        mTriangle.draw()
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        mRatio = width.toFloat() / height.toFloat()
    }

    override fun onDestroy() {
        super.onDestroy()
        GLES20.glDeleteProgram(mProgram0)
        GLES20.glDeleteProgram(mProgram1)
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