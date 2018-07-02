package com.dingmouren.opengldemo.demos.demo_1;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.WindowManager;

import com.dingmouren.opengldemo.common.CameraManagor;

/**
 * Created by GHC on 2017/6/12.
 */

public class GLSurfaceView_1 extends GLSurfaceView {

    private static final String TAG = "SquareGLSurfaceView";
    /*渲染器*/
    private Renderer_1 mRenderer;
    /*相机管理*/
    private CameraManagor mCameraManagor;

    public GLSurfaceView_1(Context context) {
        this(context,null);
    }

    public GLSurfaceView_1(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,false);
    }




    public void init(Context context, boolean isPreviewStarted) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mCameraManagor = new CameraManagor(windowManager);
        /*设置使用的OpenGL ES的版本，一般都是设置为2*/
        setEGLContextClientVersion(2);
        /*创建渲染器对象*/
        mRenderer = new Renderer_1();
         /*传入Camera对象，主要是为了调用预览等函数*/
        mRenderer.init(this, mCameraManagor, isPreviewStarted);
        setRenderer(mRenderer);
    }

    //  --------------------------  声明生命周期相关  --------------------------

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        /*注销Render线程，避免资源浪费*/
        if (mRenderer != null) {
            mRenderer.deinit();
            mRenderer = null;
        }
        /*释放相机*/
        if (mCameraManagor != null){
            mCameraManagor.stopPreview();
            mCameraManagor.releaseCamera();
            mCameraManagor = null;
        }
    }
}
