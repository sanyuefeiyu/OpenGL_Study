package com.example.opengles2;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderer implements GLSurfaceView.Renderer {

    private final float[] vertexData = {
            0f, 0f, 0f,
            1f, -1f, 0f,
            1f, 1f, 0f
    };
    private final float[] projectionMatrix = new float[16];
    private Context context;
    private String vertexShader;
    private String fragmentShader;
    private int programId;
    private int aPositionHandle;
    private FloatBuffer vertexBuffer;
    private int uMatrixHandle;

    public GLRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        vertexShader = ShaderUtils.readRawTextFile(context, R.raw.vertex_shader);
        fragmentShader = ShaderUtils.readRawTextFile(context, R.raw.fragment_shader);
        programId = ShaderUtils.createProgram(vertexShader, fragmentShader);
        aPositionHandle = GLES20.glGetAttribLocation(programId, "aPosition");

        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        float ratio = width > height ?
                (float) width / height :
                (float) height / width;
        if (width > height) {
            Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, -1f, 1f);
        } else {
            Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -ratio, ratio, -1f, 1f);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(programId);
        GLES20.glUniformMatrix4fv(uMatrixHandle,1,false,projectionMatrix,0);
        GLES20.glEnableVertexAttribArray(aPositionHandle);
        GLES20.glVertexAttribPointer(aPositionHandle, 3, GLES20.GL_FLOAT, false,
                12, vertexBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }
}
