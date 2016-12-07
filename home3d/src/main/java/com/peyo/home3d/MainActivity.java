package com.peyo.home3d;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Surface;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends Activity implements GLSurfaceView.Renderer {
    private static final String TAG = "Home3dActivity";

    private int mVideoTextureProgram;
    Screen mScreen;

    private int mIconTextureProgram;
    private Icon[] mIcons;
    private int mSelected = 0;

    private MediaPlayer mPlayer;
    private boolean mPlayerStarted = false;
    private int textureId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GLSurfaceView view = new GLSurfaceView(this);
        view.setEGLContextClientVersion(2);
        view.setRenderer(this);
        setContentView(view);
        getActionBar().hide();
        mSound = new SoundPool.Builder().setMaxStreams(2).build();
        focusSound = mSound.load(this, R.raw.focus, 1);
        clickSound = mSound.load(this, R.raw.click, 1);
    }

    private SoundPool mSound;
    private int focusSound;
    private int clickSound;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mIcons != null) mIcons[mSelected].bump(0);
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_UP:
                if (mSelected < 4) mSelected++;
                mSound.play(focusSound, 1, 1, 1, 0, 1);
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (mSelected > 0) mSelected--;
                mSound.play(focusSound, 1, 1, 1, 0, 1);
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                mSound.play(clickSound, 1, 1, 1, 0, 1);
                break;
        }
        if (mIcons != null) mIcons[mSelected].bump(1);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mIconTextureProgram = GLToolbox.createProgram(this, R.raw.texture_vertex_shader, R.raw.texture_fragment_shader);
        mIcons = new Icon[] { new Icon(6, 3.4f, -8, -5, mIconTextureProgram),
                new Icon(6, 3.4f, 0, -5, mIconTextureProgram),
                new Icon(6, 3.4f, 8, -5, mIconTextureProgram),
                new Icon(3, 3, 8, 0.5f, mIconTextureProgram),
                new Icon(3, 3, 8, 5.5f, mIconTextureProgram) } ;
        mIcons[0].setTexture(this, R.drawable.icon0);
        mIcons[1].setTexture(this, R.drawable.icon1);
        mIcons[2].setTexture(this, R.drawable.icon2);
        mIcons[3].setTexture(this, R.drawable.icon3);
        mIcons[4].setTexture(this, R.drawable.icon4);
        GLToolbox.checkGLError(TAG, "Icons");

        mVideoTextureProgram = GLToolbox.createProgram(this, R.raw.texture_vertex_shader, R.raw.video_fragment_shader);
        mScreen = new Screen(-3, 3, mVideoTextureProgram);
        GLToolbox.checkGLError(TAG, "Screen for Video");
        makeVideoTexture();
    }

    private SurfaceTexture mSurfaceTexture;
    private Surface mSurface;
    private final int GL_TEXTURE_EXTERNAL_OES = 0x8D65;
    private void makeVideoTexture() {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        textureId = textures[0];
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, textureId);
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLToolbox.checkGLError(TAG, "Bind Texture");
        mSurfaceTexture = new SurfaceTexture(textureId);
        mSurface = new Surface(mSurfaceTexture);
        startMediaPlayer();
    }

    private void startMediaPlayer() {
        if (mSurface != null && !mPlayerStarted) {
            mPlayer = MediaPlayer.create(this, R.raw.video);
            mPlayer.setLooping(true);
            mPlayer.setSurface(mSurface);
            mPlayer.start();
            mPlayerStarted = true;
        }
    }

    protected void releaseMediaPlayer() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
            mPlayerStarted = false;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        startMediaPlayer();
    }

    float mRatio;

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        mRatio = (float) width / height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mIcons[0].enableAttrib();

        mScreen.enableAttrib();
        mSurfaceTexture.updateTexImage();
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, textureId);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        float[] viewMatrix = new float[16];
        Matrix.setLookAtM(viewMatrix, 0,  0, 0, -15,  0, 0, 0,  0, 1, 0);
        float[] projectionMatrix = new float[16];
        Matrix.perspectiveM(projectionMatrix, 0, 60, mRatio, 1, 20);
        float[] mvpMatrix = new float[16];
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        mScreen.setMvp(mvpMatrix);
        mScreen.draw();

        mIcons[0].setMvp(mvpMatrix);
        mIcons[0].draw();
        mIcons[1].draw();
        mIcons[2].draw();
        mIcons[3].draw();
        mIcons[4].draw();
    }


    @Override
    protected void onDestroy() {
        releaseMediaPlayer();
        super.onDestroy();
        GLES20.glDeleteProgram(mVideoTextureProgram);
    }

    @Override
    protected void onPause() {
        releaseMediaPlayer();
        super.onPause();
    }
}
