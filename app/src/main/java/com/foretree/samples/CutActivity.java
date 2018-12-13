package com.foretree.samples;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.foretree.xopencv.OpenCVJni;
import com.foretree.xopencv.R;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

public class CutActivity extends AppCompatActivity {
    public final static int LINE_STEP = 5;
    public final static int DEF_LINE_WIDTH = 10;
    private static final String TAG = "xy-->" + "CutActivity";
    private final static int REQUEST_IMAGE = 201;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private ImageView imageView;
    private Mat bmpMat;
    private Bitmap mBuildedBmp;
    private List<Point> listPos = new ArrayList<>();
    private Point[] points = {};
    private boolean isSelect = false;
    private int screenWidth;
    private int screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cut);

        // Example of a call to a native method
        imageView = (ImageView) findViewById(R.id.sample_iv);
        screenWidth = DensityUtil.getMetricsWidth(this);
        screenHeight = DensityUtil.getMetricsWidth(this);
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
//                        listPos.clear();
                        listPos.add(new Point(event.getX(), event.getY()));
                        Log.d(TAG, "onTouch: " + event.getX() + ",y=" + event.getY());
                        break;
                    case MotionEvent.ACTION_MOVE:
                        listPos.add(new Point(event.getX(), event.getY()));
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isSelect)
                            cutOut();
                        break;
                }
                return true;
            }
        });
        //Java层代码
        mBuildedBmp = BitmapFactory.decodeResource(getResources(), R.drawable.test2);
        imageView.setImageBitmap(mBuildedBmp);
        bmpMat = new Mat();
        Utils.bitmapToMat(mBuildedBmp, bmpMat);
        OpenCVJni.initWithImage(bmpMat.getNativeObjAddr());
        OpenCVJni.setCalculateImage(bmpMat.getNativeObjAddr(), screenWidth, screenHeight);
        this.autoCut();
    }

    private void autoCut() {
        listPos.add(new Point(641, 55));
        listPos.add(new Point(609, 541));
        listPos.add(new Point(578, 390));
        listPos.add(new Point(780, 349));
        cutOut();

    }

    private void cutOut() {
//        OpenCVJni.setDrawPoint(points, 10);
        new AsyncTask<Void,Void,Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                long resultAddress = OpenCVJni.selectPoint(points, 10);
                if (resultAddress < 0) {
                    return null;
                }
                Mat resultLaplacianMat = new Mat(resultAddress);
                Bitmap newBitmap = Bitmap.createBitmap(mBuildedBmp.getWidth(), mBuildedBmp.getHeight(), Bitmap.Config.RGB_565);
                Utils.matToBitmap(resultLaplacianMat, newBitmap);
                return newBitmap;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                int size = listPos.size();
                while (true) {
                    if (size < 9000) break;
                    listPos.remove(0);
                }
                points = new Point[size];
                for (int i = 0; i < size; i++) {
                    points[i] = listPos.get(i);
                }
            }

            @Override
            protected void onPostExecute(Bitmap newBitmap) {
                super.onPostExecute(newBitmap);
                imageView.setImageBitmap(newBitmap);
            }
        }.execute();
    }

    public void cutout(View view) {
        long resultAddress = OpenCVJni.getCutResult();

        if (resultAddress < 0) {
            return;
        }
        Mat resultLaplacianMat = new Mat(resultAddress);
        Utils.matToBitmap(resultLaplacianMat, mBuildedBmp);
        imageView.setImageBitmap(mBuildedBmp);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE) {
            Uri uri = data.getData();
            mBuildedBmp = PicUtils.getSmallBitmap(getRealPathFromURI(uri), 400, 400);
            imageView.setImageBitmap(mBuildedBmp);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public void choice(View v) {
        isSelect = true;

    }

    public void add(View v) {

    }

    public void del(View v) {

    }

    public void move(View v) {
    }

    public void back(View v) {
    }

    public void go(View v) {
    }

    public void rePos(View v) {
    }

    public void reDraw(View v) {
        OpenCVJni.resetAllMask();
        imageView.setImageBitmap(mBuildedBmp);
    }

    public void openAlbum(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE);
    }
}
