package com.dionly.flashlight;

import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    private CameraManager manager;
    private Camera camera = null;

    boolean lightOn=false;
    ImageView imageView;
    ImageView imageViewCover;
    Drawable drawable;

    RelativeLayout bgLightOn;

    int curPos=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);


        manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);


        LightSwitch lightLightSwitch = findViewById(R.id.light_switch);
        bgLightOn = findViewById(R.id.light_on_bg);

//        RecyclerView recyclerView = findViewById(R.id.rate_scroller);
//
//        int data[] = new int[18*9];
//        for (int i = 0; i < 18*9; i++) {
//            data[i] = i;
//        }
//
//        RateControllerAdapter adapter = new RateControllerAdapter(this, data);
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        layoutManager.setReverseLayout(true);
//
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setAdapter(adapter);
//
//
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//            }
//        });
//



        lightLightSwitch.setOnSlideListener(new LightSwitch.onSlideListener() {
            @Override
            public void setToggleState(boolean open) {
                processUI(open);
                new LightTask().execute(open);
            }
        });

    }

    private void processUI(boolean open) {
        if (open) {
            bgLightOn.setVisibility(View.VISIBLE);
        }else{
            bgLightOn.setVisibility(View.INVISIBLE);
        }
    }

    private class LightTask extends AsyncTask<Boolean, Void, Void> {
        @Override
        protected Void doInBackground(Boolean... booleans) {
            process(booleans[0]);
            return null;
        }

        private void process(boolean lightOn) {
            if (lightOn) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        manager.setTorchMode("0", true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    final PackageManager pm = getPackageManager();
                    final FeatureInfo[] features = pm.getSystemAvailableFeatures();
                    for (final FeatureInfo f : features) {
                        if (PackageManager.FEATURE_CAMERA_FLASH.equals(f.name)) { // 判断设备是否支持闪光灯
                            if (null == camera) {
                                camera = Camera.open();
                            }
                            final Camera.Parameters parameters = camera.getParameters();
                            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                            camera.setParameters(parameters);
                            camera.startPreview();
                        }
                    }
                }
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        manager.setTorchMode("0", false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (camera != null) {
                        camera.stopPreview();
                        camera.release();
                        camera = null;
                    }
                }
            }
        }
    }







}
