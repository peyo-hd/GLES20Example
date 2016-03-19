package com.example.peyo.gles20;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLES20Activity extends Activity implements GLSurfaceView.Renderer {

    private static final String TAG = "Ex01Activity";

    private final String vertexShaderCode0 =
            "uniform mat4 uMVPMatrix0;" +
                    "attribute vec4 aPosition0;" +
                    "attribute vec4 aColor0;" +
                    "varying vec4 vColor;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix0 * aPosition0;" +
                    "  vColor = aColor0;" +
                    "}";

    private final String fragmentShaderCode0 =
            "precision mediump float;" +
                    "varying vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private final String vertexShaderCode1 =
            "uniform mat4 uMVPMatrix1;" +
                    "attribute vec4 aPosition1;" +
                    "attribute vec2 aTexCoord;" +
                    "varying vec2 vTexCoord;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix1 * aPosition1;" +
                    "  vTexCoord = aTexCoord;" +
                    "}";

    private final String fragmentShaderCode1 =
            "precision mediump float;" +
                    "varying vec2 vTexCoord;" +
                    "uniform sampler2D samplerTex;" +
                    "void main() {" +
                    "  gl_FragColor = texture2D(samplerTex, vTexCoord);" +
                    "}";

    private final String vertexShaderCode2 =
            "uniform mat4 uMVPMatrix2;" +
                    "attribute vec4 aPosition2;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix2 * aPosition2;" +
                    "}";

    private final String fragmentShaderCode2 =
            "precision mediump float;" +
                    "uniform vec4 uColor2;" +
                    "void main() {" +
                    "  gl_FragColor = uColor2;" +
                    "}";

    private int mProgram0;
    private int mProgram1;
    private int mProgram2;
    Triangle mTriangle;
    Square mSquare;
    Grid mGrid;
    Column mColumn;

    private float mAngle = 0;
    private float mRatio = 1.0f;

    private ObjLoader mObjLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GLSurfaceView view = new GLSurfaceView(this);
        view.setEGLContextClientVersion(2);
        view.setRenderer(this);
        setContentView(view);
        mObjLoader = new ObjLoader(this);
    }

    private int mPositionHandle0;
    private int mColorHandle0;
    private int mPositionHandle1;
    private int mTexCoordHandle;
    private int mPositionHandle2;
    private int mColorHandle2;

    private int mMVPMatrixHandle0;
    private int mMVPMatrixHandle1;
    private int mSamplerHandle;
    private int mMVPMatrixHandle2;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        mProgram0 = GLToolbox.createProgram(vertexShaderCode0, fragmentShaderCode0);
        mProgram1 = GLToolbox.createProgram(vertexShaderCode1, fragmentShaderCode1);
        mProgram2 = GLToolbox.createProgram(vertexShaderCode2, fragmentShaderCode2);

        mPositionHandle0 = GLES20.glGetAttribLocation(mProgram0, "aPosition0");
        mColorHandle0 = GLES20.glGetAttribLocation(mProgram0, "aColor0");
        mPositionHandle1 = GLES20.glGetAttribLocation(mProgram1, "aPosition1");
        mTexCoordHandle = GLES20.glGetAttribLocation(mProgram1, "aTexCoord");
        mPositionHandle2 = GLES20.glGetAttribLocation(mProgram2, "aPosition2");

        mMVPMatrixHandle0 = GLES20.glGetUniformLocation(mProgram0, "uMVPMatrix0");
        mSamplerHandle = GLES20.glGetUniformLocation(mProgram1, "samplerTex");
        mMVPMatrixHandle1 = GLES20.glGetUniformLocation(mProgram1, "uMVPMatrix1");
        mMVPMatrixHandle2 = GLES20.glGetUniformLocation(mProgram2, "uMVPMatrix2");
        mColorHandle2 = GLES20.glGetUniformLocation(mProgram2, "uColor2");

        mGrid = new Grid(mPositionHandle0, mColorHandle0, mMVPMatrixHandle0);
        mTriangle = new Triangle(mPositionHandle0, mColorHandle0);
        mSquare = new Square(mPositionHandle1, mTexCoordHandle);

        mObjLoader.load("column.obj");
        mColumn = new Column(mObjLoader.vertices, mPositionHandle2, mColorHandle2);

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ground);
        mSquare.setTexture(mSamplerHandle, bmp);
        bmp.recycle();

        GLES20.glEnableVertexAttribArray(mPositionHandle0);
        GLES20.glEnableVertexAttribArray(mColorHandle0);
        GLES20.glEnableVertexAttribArray(mPositionHandle1);
        GLES20.glEnableVertexAttribArray(mTexCoordHandle);
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

        GLES20.glUseProgram(mProgram1);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle1, 1, false, mvpMatrix, 0);
        mSquare.draw();

        GLES20.glUseProgram(mProgram0);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle0, 1, false, mvpMatrix, 0);
        mTriangle.draw();

        float[] rotationMatrix = new float[16];
        Matrix.setRotateM(rotationMatrix, 0, ++mAngle,  0, 1, 0);
        Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, rotationMatrix, 0);

        GLES20.glUseProgram(mProgram2);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle2, 1, false, mvpMatrix, 0);
        mColumn.draw();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GLES20.glDeleteProgram(mProgram0);
        GLES20.glDeleteProgram(mProgram1);
    }
}
