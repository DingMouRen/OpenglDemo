package com.dingmouren.opengldemo.demos.demo_1;

import android.Manifest;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.dingmouren.opengldemo.R;
import com.dingmouren.opengldemo.common.CameraManagor;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class Demo1Activity extends AppCompatActivity {
    private GLSurfaceView_1 mGLSurfaceView;
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

        mGLSurfaceView = new GLSurfaceView_1(this);
        setContentView(mGLSurfaceView);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }
}
