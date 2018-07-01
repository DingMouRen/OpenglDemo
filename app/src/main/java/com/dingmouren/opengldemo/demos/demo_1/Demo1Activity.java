package com.dingmouren.opengldemo.demos.demo_1;

import android.Manifest;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.dingmouren.opengldemo.R;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class Demo1Activity extends AppCompatActivity {
    private SquareGLSurfaceView mGLSurfaceView;
    private SquareCameraManager mCamera;
    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private RxPermissions mRxPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo1);
        mRxPermissions = new RxPermissions(this);
        mRxPermissions.request(Manifest.permission.CAMERA)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean value) {
                        Toast.makeText(Demo1Activity.this,""+value,Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        mGLSurfaceView = new SquareGLSurfaceView(this);
        mCamera = new SquareCameraManager(this);

       mCamera.openCamera(mCameraId);
        mGLSurfaceView.initGLSView(mCamera,false);
        setContentView(mGLSurfaceView);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGLSurfaceView != null) {
            mGLSurfaceView.onPause();
            mGLSurfaceView.unInitGLSView();
            mGLSurfaceView = null;
        }

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.releaseCamera();
            mCamera = null;
        }
    }
}
