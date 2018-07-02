package com.dingmouren.opengldemo.demos.demo_1;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.dingmouren.opengldemo.common.CameraManagor;

import java.nio.FloatBuffer;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;
import static javax.microedition.khronos.opengles.GL11.GL_FLOAT;

/**
 * 渲染器
 */

public class Renderer_1 implements GLSurfaceView.Renderer {

    private static final String TAG = "Renderer_1";
    private GLSurfaceView_1 mGLSurfaceView;
    private CameraManagor mCameraManagor;
    private int mOESTextureId = -1;//创建的新的纹理
    private SurfaceTexture mSurfaceTexture;
    private float[] transformMatrix = new float[16];
    private boolean bIsPreviewStarted;
    private RenderEngine_1 mRenderEngine1;
    private FloatBuffer mDataBuffer;
    private int mShaderProgram = -1;
    private int aPositionLocation = -1;
    private int aTextureCoordLocation = -1;
    private int uTextureMatrixLocation = -1;
    private int uTextureSamplerLocation = -1;
    private int mWidth, mHeight;
    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

    public void init(GLSurfaceView_1 glSurfaceView, CameraManagor cameraManagor, boolean isPreviewStarted) {
        mGLSurfaceView = glSurfaceView;
        mCameraManagor = cameraManagor;
        bIsPreviewStarted = isPreviewStarted;

    }

    /*
    * 一般在onSurfaceCreated 做一些初始化的动作
    * Render的初始化主要包括：
    * 创建shader---> 加载shader代码---> 编译shader  ====》最终可以生成vertexShader和fragmentShader
    * 创建porgram---> 附着顶点着色器和片段着色器到program--->链接program---> 通知OpenGL ES使用此program  ===》最终将shader添加到program中
    *
    * */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mCameraManagor.openCamera(mCameraId);
         /*获取一个纹理句柄，注释点仍然可以预览*/
        mOESTextureId = createOESTextureObject();
        Log.d(TAG, "mOESTextureId:" + mOESTextureId);
         /*初始化渲染器*/
        mRenderEngine1 = new RenderEngine_1();
         /*获取缓冲区*/
        mDataBuffer = mRenderEngine1.getBuffer();
         /*获取OpenGL ES程序对象*/
        mShaderProgram = mRenderEngine1.getShaderProgram();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.e(TAG, "Surface尺寸: " + width + " ; height: " + height);
        this.mWidth = width;
        this.mHeight = height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        /*启用剪裁测试，注释掉仍然可以预览*/
        GLES20.glEnable(GL10.GL_SCISSOR_TEST);
         /*设置显示区域，坐标系以左下角作为坐标原点，参数1和参数2为x y坐标，后两个参数是显示区域的宽高,在完整的摄像头视图上裁剪显示部分区域*/
        GLES20.glScissor(0, 0, mWidth, mHeight);

        if (mSurfaceTexture != null) {
             /*SurfaceTexture对象所关联的OpenGLES中纹理对象的内容将被更新为Image Stream中最新的图片。*/
            mSurfaceTexture.updateTexImage();
            /*调用getTransformMatrix()来转换纹理坐标,每次texture image被更新时，getTransformMatrix ()也应该被调用。transformMatrix里面的值是在这里被赋值的*/
            mSurfaceTexture.getTransformMatrix(transformMatrix);
        }
        /*未开启预览时调用*/
        if (!bIsPreviewStarted) {
            bIsPreviewStarted = initSurfaceTexture();
            return;
        }
         /*设置窗口颜色，注释掉仍然可以预览*/
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
        /*获取顶点着色器的位置的句柄，glsl中只用顶点着色器才能使用attribute，用来表示顶点数据*/
        aPositionLocation = glGetAttribLocation(mShaderProgram, RenderEngine_1.POSITION_ATTRIBUTE);
        aTextureCoordLocation = glGetAttribLocation(mShaderProgram, RenderEngine_1.TEXTURE_COORD_ATTRIBUTE);
        /*获取vertex和fragment共享使用的属性句柄*/
        uTextureMatrixLocation = glGetUniformLocation(mShaderProgram, RenderEngine_1.TEXTURE_MATRIX_UNIFORM);
        uTextureSamplerLocation = glGetUniformLocation(mShaderProgram, RenderEngine_1.TEXTURE_SAMPLER_UNIFORM);
         /*激活GL_TEXTURE0，该纹理单元默认激活，，注释掉仍然可以预览*/
        glActiveTexture(GLES20.GL_TEXTURE0);
        /*将自己创建的纹理绑定在扩展纹理上,注释掉仍然可以预览*/
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mOESTextureId);
        /*设置纹理采样器，这个对应纹理第一层,注释掉仍然可以预览*/
        glUniform1i(uTextureSamplerLocation, 0);
        /*向shader中传递矩阵，参数分别为：下标位置，矩阵数量，是否进行转置，矩阵*/
        glUniformMatrix4fv(uTextureMatrixLocation, 1, false, transformMatrix, 0);

        if (mDataBuffer != null) {
             /*设置缓冲区从0开始读取*/
            mDataBuffer.position(0);
            /*启动句柄*/
            glEnableVertexAttribArray(aPositionLocation);
            /*设置图形数据*/
            glVertexAttribPointer(aPositionLocation, 2, GL_FLOAT, false, 16, mDataBuffer);

             /*设置缓冲区从2开始读取*/
            mDataBuffer.position(2);
            /*启动句柄*/
            glEnableVertexAttribArray(aTextureCoordLocation);
            /*设置图形数据*/
            glVertexAttribPointer(aTextureCoordLocation, 2, GL_FLOAT, false, 16, mDataBuffer);
        }
         /*绘制三角形*/
        glDrawArrays(GL_TRIANGLES, 0, 6);
        /*解绑FrameBuffer，注释掉仍然可以预览*/
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public boolean initSurfaceTexture() {
        if (mCameraManagor == null || mGLSurfaceView == null) {
            Log.i(TAG, "mCamera or mGLSurfaceView is null!");
            return false;
        }
        try {
            Log.e(TAG,"initSurfaceTexture--mOESTextureId:"+mOESTextureId);
            mSurfaceTexture = new SurfaceTexture(mOESTextureId);
            /* 当数据帧有效时，即onFrameAvailable被调用时，可调用GLSurfaceView.requestRender()，来显示要求进行渲染，即触发Renderer的onDrawFrame()，但是注释掉仍然是可以预览的*/
            mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                @Override
                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                    mGLSurfaceView.requestRender();
                }
            });
            /*将SurfaceTexture与Camera绑定*/
            mCameraManagor.setPreviewTexture(mSurfaceTexture);
            /*摄像头开始预览*/
            mCameraManagor.startPreview();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,"initSurfaceTexture失败："+e.getMessage());
            return false;
        }

    }

    public void deinit() {
        if (mRenderEngine1 != null) {
            mRenderEngine1 = null;
        }
        mDataBuffer = null;
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        mCameraManagor = null;
        mOESTextureId = -1;
        bIsPreviewStarted = false;
    }

    /*
    * 返回一个纹理句柄，拿到这个纹理句柄后，就可以对它进行操作
    * */
    public static int createOESTextureObject() {
        int[] tex = new int[1];
         /*创建纹理 ID。,返回一个纹理的句柄*/
        GLES20.glGenTextures(1, tex, 0);
        /*将纹理 ID 和纹理目标绑定,解除绑定的话将最后一个参数设置为0就可以了*/
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex[0]);
        // 纹理对象使用GL_TEXTURE_EXTERNAL_OES作为纹理目标，其是OpenGL ES扩展GL_OES_EGL_image_external定义的。
        // https://blog.csdn.net/zsc09_leaf/article/details/17529769 有详细解释
        /*设置缩小的情况下过滤方式*/
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        /*设置放大的情况下过滤方式*/
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return tex[0];
    }

}
