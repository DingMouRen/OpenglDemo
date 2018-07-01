package com.dingmouren.opengldemo.demos.demo_1;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;

/**
 * Created by GHC on 2017/6/27.
 * <p>
 * CameraManager类
 * 主要实现：
 * openCamera() 打开摄像头
 * startPreview() 开启预览
 * stopPreview()  结束预览
 * setPreviewTexture()  给Camera设置SurfaceTexture
 * releaseCamera() 释放摄像头
 */

public class SquareCameraManager {
    private Activity mActivity;
    private int mCameraId;
    private Camera mCamera;

    public SquareCameraManager(Activity activity) {
        mActivity = activity;
    }

    // 打开Camera，传入的是前后置摄像头参数
    // Camera.CameraInfo.CAMERA_FACING_BACK = 0：后置摄像头
    // Camera.CameraInfo.CAMERA_FACING_FRONT = 1：前置摄像头
    public boolean openCamera(int cameraId) {
        try {
            mCameraId = cameraId;
            // 打开Camera
            mCamera = Camera.open(mCameraId);
            /*获取摄像头的参数*/
            Camera.Parameters parameters = mCamera.getParameters();
            // 设置原生Camera的预览分辨率，例如 1280*720
            parameters.setPreviewSize(Constant.SYSTEM_PREVIEW_WIDTH, Constant.SYSTEM_PREVIEW_HEIGHT);
            // 设置Camera角度，根据当前屏幕的角度设置
            setCameraDisplayOrientation(mActivity, mCameraId, mCamera);
            mCamera.setParameters(parameters);
            Log.i("GHC", "open camera");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, Camera camera) {
        /*创建摄像头信息存储对象，前后摄像头 facing orientation等*/
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        /*获取摄像头信息*/
        Camera.getCameraInfo(cameraId, info);
         /*获取屏幕旋转方向*/
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        /*根据前置与后置摄像头的不同，设置预览方向，否则会发生预览图像倒过来的情况。*/
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {//打开的是前置摄像头
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  //打开的是后置摄像头
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    /*
    * 开启预览，是在GLSurfaceView创建成功后调用
    * */
    public void startPreview() {
        if (mCamera != null) {
            mCamera.startPreview();
        }
    }

    /*
    * 屏幕失去焦点后，停止预览，避免资源浪费
    * */
    public void stopPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    /*
    * 将SurfaceTexture与Camera绑定
    * 这样Camera的输出数据，就可以显示在SurfaceTexture上面
    * 而STexture是通过GLSurfaceView创建的，这样GLSView就可以操控STexture的数据了
    * 通过对STexture上数据的处理，可以实现滤镜功能，当然也可以实现我们需要的方形预览
    *
    * 此部分可以看 @2.2
    * */
    public void setPreviewTexture(SurfaceTexture surfaceTexture) {
        if (mCamera != null) {
            try {
                 /*将摄像头拍摄的数据传送给SurfaceTexture*/
                mCamera.setPreviewTexture(surfaceTexture);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    * 退出应用时，释放Camera
    * */
    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }
}
