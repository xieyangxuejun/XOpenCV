package com.foretree.xopencv;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


public class GradCutActivity extends ActionBarActivity {
    static final int REQUEST_OPEN_IMAGE = 1;
    private static final String TAG = "GradCutActivity";
    String mCurrentPhotoPath;
    Bitmap mBitmap;
    ImageView mImageView;
    int touchCount = 0;
    Point tl;
    Point br;
    boolean targetChose = false;
    ProgressDialog dlg;
    private Uri imgUri;

    /**
     * 根据路径获得突破并压缩返回bitmap用于显示
     *
     * @param filePath
     * @return
     */
    public static Bitmap getSmallBitmap(String filePath, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;  //只返回图片的大小信息
        BitmapFactory.decodeFile(filePath, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public void backgroundForegroundSegm(Bitmap b) {

        b = b.copy(Bitmap.Config.ARGB_8888, true);

        //GrabCut part
        Mat img = new Mat();
        Utils.bitmapToMat(b, img);

        int r = img.rows();
        int c = img.cols();
        Point p1 = new Point(c / 100, r / 100);
        Point p2 = new Point(c - c / 100, r - r / 100);
        Rect rect = new Rect(p1, p2);

        Mat mask = new Mat();
        Mat fgdModel = new Mat();
        Mat bgdModel = new Mat();

        Mat imgC3 = new Mat();
        Imgproc.cvtColor(img, imgC3, Imgproc.COLOR_RGBA2RGB);

        Imgproc.grabCut(imgC3, mask, rect, bgdModel, fgdModel, 2, Imgproc.
                GC_INIT_WITH_RECT);


        Core.convertScaleAbs(mask, mask, 100, 0);
        Imgproc.cvtColor(mask, mask, Imgproc.COLOR_GRAY2RGBA);

        //convert to Bitmap
        Utils.matToBitmap(mask, b);
        mImageView.setVisibility(View.VISIBLE);
        mImageView.setImageBitmap(b);


        //release MAT part
        img.release();
        imgC3.release();
        mask.release();
        fgdModel.release();
        bgdModel.release();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.sample_iv);
        dlg = new ProgressDialog(this);
        tl = new Point();
        br = new Point();
        mImageView.setImageResource(R.drawable.test);
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }


    }

    /*开始*/
    public void cutout(View view) {
        mImageView.setImageBitmap(null);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        backgroundForegroundSegm(bitmap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void setPic() {
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();


        mBitmap = getSmallBitmap(mCurrentPhotoPath, targetW, targetH);
        mImageView.setImageBitmap(mBitmap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_OPEN_IMAGE:
                if (resultCode == RESULT_OK) {
                    imgUri = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(imgUri, filePathColumn,
                            null, null, null);
                    cursor.moveToFirst();

                    int colIndex = cursor.getColumnIndex(filePathColumn[0]);
                    mCurrentPhotoPath = cursor.getString(colIndex);
                    cursor.close();
                    setPic();
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_open_img:
                Intent getPictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getPictureIntent.setType("image/*");
                Intent pickPictureIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Intent chooserIntent = Intent.createChooser(getPictureIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{
                        pickPictureIntent
                });
                startActivityForResult(chooserIntent, REQUEST_OPEN_IMAGE);
                return true;
            case R.id.action_choose_target:
                if (mCurrentPhotoPath != null)
                    targetChose = false;
                mImageView.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            if (touchCount == 0) {
                                tl.x = event.getX();
                                tl.y = event.getY();
                                touchCount++;
                            } else if (touchCount == 1) {
                                br.x = event.getX();
                                br.y = event.getY();

                                Paint rectPaint = new Paint();
                                rectPaint.setARGB(255, 255, 0, 0);
                                rectPaint.setStyle(Paint.Style.STROKE);
                                rectPaint.setStrokeWidth(3);
                                Bitmap tmpBm = Bitmap.createBitmap(mBitmap.getWidth(),
                                        mBitmap.getHeight(), Bitmap.Config.RGB_565);
                                Canvas tmpCanvas = new Canvas(tmpBm);

                                tmpCanvas.drawBitmap(mBitmap, 0, 0, null);
                                tmpCanvas.drawRect(new RectF((float) tl.x, (float) tl.y, (float) br.x, (float) br.y),
                                        rectPaint);
                                mImageView.setImageDrawable(new BitmapDrawable(getResources(), tmpBm));

                                targetChose = true;
                                touchCount = 0;
                                mImageView.setOnTouchListener(null);
                            }
                        }

                        return true;
                    }
                });

                return true;
            case R.id.action_cut_image:
                if (mCurrentPhotoPath != null && targetChose) {
                    new ProcessImageTask().execute();
                    targetChose = false;
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ProcessImageTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dlg.setMessage("Processing Image...");
            dlg.setCancelable(false);
            dlg.setIndeterminate(true);
            dlg.show();
        }

        @Override
        protected Integer doInBackground(Integer... params) {

            Mat img = Imgcodecs.imread(mCurrentPhotoPath);
            Mat background = new Mat(img.size(), CvType.CV_8U,
                    new Scalar(255, 255, 255));
            Mat firstMask = new Mat();
            Mat bgModel = new Mat();
            Mat fgModel = new Mat();
            Mat mask;
            Mat source = new Mat(1, 1, CvType.CV_8U, new Scalar(Imgproc.GC_PR_FGD));
            Mat dst = new Mat();
            Rect rect = new Rect(tl, br);
            firstMask.create(img.size(), CvType.CV_8UC1);
            firstMask.setTo(new Scalar(0));
            firstMask.submat(rect).setTo(new Scalar(Imgproc.GC_PR_FGD));

            Imgproc.grabCut(img, firstMask, rect, bgModel, fgModel, 3, 1);
            Core.compare(firstMask, source, firstMask, Core.CMP_EQ);

            Mat foreground = new Mat(img.size(), CvType.CV_8UC1,
                    new Scalar(0, 0, 0));
            img.copyTo(foreground, firstMask);

            Scalar color = new Scalar(255, 0, 0, 255);
            Imgproc.rectangle(img, tl, br, color);

            Mat tmp = new Mat();
            Imgproc.resize(background, tmp, img.size());
            background = tmp;
            mask = new Mat(foreground.size(), CvType.CV_8UC1,
                    new Scalar(255, 255, 255));

            Imgproc.cvtColor(foreground, mask, Imgproc.COLOR_BGR2GRAY);
            Imgproc.threshold(mask, mask, 254, 255, Imgproc.THRESH_BINARY_INV);
            System.out.println();
            Mat vals = new Mat(1, 1, CvType.CV_8UC3, new Scalar(0.0));
            background.copyTo(dst);

            background.setTo(vals, mask);

            Core.add(background, foreground, dst, mask);

            firstMask.release();
            source.release();
            bgModel.release();
            fgModel.release();
            vals.release();

            Imgcodecs.imwrite(mCurrentPhotoPath + ".png", dst);

            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            Bitmap jpg = BitmapFactory
                    .decodeFile(mCurrentPhotoPath + ".png");

            mImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            mImageView.setAdjustViewBounds(true);
            mImageView.setPadding(2, 2, 2, 2);
            mImageView.setImageBitmap(jpg);
            mImageView.invalidate();

            dlg.dismiss();
        }
    }


//    public static Bitmap grabCutIter(Bitmap bitmap){
//        opencv_core.IplImage inputImage = opencv_core.IplImage.create(bitmap.getWidth(), bitmap.getHeight(), IPL_DEPTH_8U, 4);
//        bitmap.copyPixelsToBuffer(inputImage.getByteBuffer());
//        opencv_core.Mat inputMat = new opencv_core.Mat(inputImage);
//        opencv_core.IplImage result = opencv_core.IplImage.create(bitmap.getWidth(), bitmap.getHeight(), IPL_DEPTH_8U, 1);
//       Mat matResult = new Mat(result);
//        Rect boundingRectangle = new Rect(10, 10, 20, 20);
////        IplImage bgModel = IplImage.create(bitmap.getWidth(), bitmap.getHeight(), IPL_DEPTH_8U, 1);
//        Mat bgModelMat = new Mat();
////        IplImage fgModel = IplImage.create(bitmap.getWidth(), bitmap.getHeight(), IPL_DEPTH_8U, 1);
//        Mat fgModelMat = new Mat();
//        Imgproc.grabCut(inputMat, matResult, boundingRectangle, bgModelMat, fgModelMat, 1, Imgproc.GC_INIT_WITH_RECT);
//        Bitmap outputBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
//        outputBitmap.copyPixelsFromBuffer(matResult.getByteBuffer());
//        cvRelease(inputImage);
////        cvRelease(bgModel);
////        cvRelease(fgModel);
//        return outputBitmap;
//    }
}
