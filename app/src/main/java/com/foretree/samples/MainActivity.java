package com.foretree.samples;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.foretree.xopencv.R;

import org.opencv.core.Core;
import org.opencv.core.Mat;

public class MainActivity extends AppCompatActivity {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private ImageView tv;
    private Mat bmpMat;
    private Bitmap mBuildedBmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        tv = (ImageView) findViewById(R.id.sample_iv);
    }
}
