package com.example.peyo.gles20;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLES20Activity extends Activity implements GLSurfaceView.Renderer {

    private static final String TAG = "GLES20Activity";

    private int mProgram0;
    private int mMVPMatrixHandle0;
    Triangle mTriangle;
    Grid mGrid;

    private int mProgram1;
    private int mMVPMatrixHandle1;
    Square mSquare;
    Sphere mSphere;

    private int mProgram2;
    private int mMVPMatrixHandle2;
    ObjLoader mObjLoader;
    Column mColumn;

    private float mAngle = 0;
    private float mRatio = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GLSurfaceView view = new GLSurfaceView(this);
        view.setEGLContextClientVersion(2);
        view.setRenderer(this);
        setContentView(view);
        mObjLoader = new ObjLoader(this);
        mObjLoader.load("column.obj");
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mProgram0 = GLToolbox.createProgram(readShader(R.raw.color_vertex), readShader(R.raw.color_fragment));
        mMVPMatrixHandle0 = GLES20.glGetUniformLocation(mProgram0, "uMVPMatrix");
        int aPositionHandle = GLES20.glGetAttribLocation(mProgram0, "aPosition");
        int aColorHandle = GLES20.glGetAttribLocation(mProgram0, "aColor");
        GLES20.glEnableVertexAttribArray(aPositionHandle);
        GLES20.glEnableVertexAttribArray(aColorHandle);

        mGrid = new Grid(aPositionHandle, aColorHandle, mMVPMatrixHandle0);
        mTriangle = new Triangle(aPositionHandle, aColorHandle);
        GLToolbox.checkGLError(TAG, "Program and Object for Grid/Triangle");

        mProgram1 = GLToolbox.createProgram(readShader(R.raw.texture_vertex), readShader(R.raw.texture_fragment));
        mMVPMatrixHandle1 = GLES20.glGetUniformLocation(mProgram1, "uMVPMatrix");
        aPositionHandle = GLES20.glGetAttribLocation(mProgram1, "aPosition");
        int aTexCoordHandle = GLES20.glGetAttribLocation(mProgram1, "aTexCoord");
        int uSamplerHandle = GLES20.glGetUniformLocation(mProgram1, "uSamplerTex");
        GLES20.glEnableVertexAttribArray(aPositionHandle);
        GLES20.glEnableVertexAttribArray(aTexCoordHandle);

        mSquare = new Square(aPositionHandle, aTexCoordHandle);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ground);
        mSquare.setTexture(uSamplerHandle, bmp);
        bmp.recycle();

        mSphere = new Sphere(aPositionHandle, aTexCoordHandle);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.globe);
        mSphere.setTexture(uSamplerHandle, bmp);
        bmp.recycle();
        GLToolbox.checkGLError(TAG, "Program and Object for Square/Sphere");

        mProgram2 = GLToolbox.createProgram(readShader(R.raw.position_vertex), readShader(R.raw.solid_fragment));
        mMVPMatrixHandle2 = GLES20.glGetUniformLocation(mProgram2, "uMVPMatrix");
        aPositionHandle = GLES20.glGetAttribLocation(mProgram2, "aPosition");
        int uColorHandle = GLES20.glGetUniformLocation(mProgram2, "uColor");
        GLES20.glEnableVertexAttribArray(aPositionHandle);

        mColumn = new Column(mObjLoader.vertices, aPositionHandle, uColorHandle);
        GLToolbox.checkGLError(TAG, "Program and Object for Column");

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        mRatio = (float) width / height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        float[] viewMatrix = new float[16];
        Matrix.setLookAtM(viewMatrix, 0,  6, 6, 6,  0, (float) (0.5 * Math.sin(mAngle / 40)), 0,  0, 1, 0);
        float[] projectionMatrix = new float[16];
        Matrix.perspectiveM(projectionMatrix, 0, 30, mRatio, 1, 20);
        float[] mvpMatrix = new float[16];
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        GLES20.glUseProgram(mProgram0);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle0, 1, false, mvpMatrix, 0);
        mGrid.draw(mvpMatrix);

        float[] mvpMatrix1 = new float[16];
        Matrix.translateM(mvpMatrix1, 0, mvpMatrix, 0, -4, 0, -4);
        GLES20.glUseProgram(mProgram1);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle1, 1, false, mvpMatrix1, 0);
        mSquare.draw();
        GLES20.glUseProgram(mProgram0);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle0, 1, false, mvpMatrix1, 0);
        mTriangle.draw();

        float[] mvpMatrix2 = new float[16];
        Matrix.rotateM(mvpMatrix2, 0, mvpMatrix, 0, ++mAngle,  0, 1, 0);
        GLES20.glUseProgram(mProgram1);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle1, 1, false, mvpMatrix2, 0);
        mSphere.draw();
        GLES20.glUseProgram(mProgram2);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle2, 1, false, mvpMatrix2, 0);
        mColumn.draw();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GLES20.glDeleteProgram(mProgram0);
        GLES20.glDeleteProgram(mProgram1);
        GLES20.glDeleteProgram(mProgram2);
    }

    private String readShader(int resId) {
        InputStream inputStream = getResources().openRawResource(resId);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
