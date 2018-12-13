//
// Created by silen on 06/07/2017.
//

#include "com_foretree_xopencv_OpenCVJni.h"
#include <opencv2/opencv.hpp>
#include "CutoutImagePacking.h"
#include <android/log.h>

#define LOGI(FORMAT, ...) __android_log_print(ANDROID_LOG_INFO,"xy-->",FORMAT,##__VA_ARGS__);
#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"xy-->",FORMAT,##__VA_ARGS__);


using namespace cv;
using namespace std;

class CutOutWrapper {
public:
    CutOutWrapper() {};

    ~CutOutWrapper() {};
private:
};

CutoutImagePacking *cutoutImagePacking;
float xScale = 1;
float yScale = 1;

JNIEXPORT jlong JNICALL Java_com_foretree_xopencv_OpenCVJni_cutout
        (JNIEnv *env, jclass jcls, jlong jl) {


    Mat img = Mat(*(Mat *) jl);
    Mat img0(img.size(), CV_8UC3);
    cvtColor(img, img0, CV_BGRA2BGR);

    Mat result;
    Mat bgModel;
    Mat fgModel;
    Rect selection = Rect(img.cols / 10, img.rows / 10, img.cols * 9 / 10, img.rows * 9 / 10);

    grabCut(img0, result, selection, bgModel, fgModel, 1, GC_INIT_WITH_RECT);
    compare(result, GC_PR_FGD, result, CMP_EQ);
    Mat foreground(img.size(), CV_8UC3, Scalar::all(255));
    img0.copyTo(foreground, result);

    Mat *hist = new Mat(foreground);
    return (jlong) hist;
}

JNIEXPORT void JNICALL Java_com_foretree_xopencv_OpenCVJni_initWithImage
        (JNIEnv *env, jclass jcl, jlong jl) {
    Mat img = Mat(*(Mat *) jl);
    cutoutImagePacking = new CutoutImagePacking;
    int cols = img.cols;
    int rows = img.rows;
    cvMat(rows, cols, CV_8UC4);
    Mat sendImageRGBA = img;
    Mat sendImageBGR;
    cvtColor(sendImageRGBA, sendImageBGR, CV_RGBA2BGR);
    LOGI("sendImageBGR.cols = %d",sendImageBGR.cols);
    LOGI("sendImageBGR.rows = %d",sendImageBGR.rows);
    cutoutImagePacking->setColorImage(sendImageBGR, 20);
    LOGI("%s", "成功");
};

JNIEXPORT void JNICALL Java_com_foretree_xopencv_OpenCVJni_setCalculateImage
        (JNIEnv *env, jclass jcl, jlong jl, jfloat width, jfloat height) {
    Mat img = Mat(*(Mat *) jl);
    int cols = img.cols;
    int rows = img.rows;
    cvMat(rows, cols, CV_8UC4);
    Mat sendImageRGBA = img;
    Mat sendImageBGR;
    cvtColor(sendImageRGBA, sendImageBGR, CV_RGBA2BGR);
    LOGI("sendImageBGR.cols = %d",sendImageBGR.cols);
    LOGI("sendImageBGR.rows = %d",sendImageBGR.rows);
    cutoutImagePacking->setColorImage(sendImageBGR, 20);
    xScale = sendImageBGR.cols/width;
    yScale = sendImageBGR.rows/height;
    LOGI("xScale = %f \n yScale = %f",xScale, xScale);
};

JNIEXPORT void JNICALL Java_com_foretree_xopencv_OpenCVJni_setDrawPoint
        (JNIEnv *env, jclass jcl, jobjectArray array, int lineWidth) {
    LOGI("lineWidth = %d",lineWidth);
    jsize size = env->GetArrayLength(array);
    LOGI("size = %d",size);
    vector<cv::Point> sendPoint;
    for(int i =0;i< size;i++){
        jobject obj = env->GetObjectArrayElement(array,i);
        jclass clazz = env->GetObjectClass(obj);
        jfieldID fid_x = env->GetFieldID(clazz,"x","D");
        jdouble x = env->GetDoubleField(obj, fid_x);
        jfieldID fid_y = env->GetFieldID(clazz,"y","D");
        jdouble y = env->GetDoubleField(obj, fid_y);
        Point cvp;
        cvp.x = (int)(x*xScale);
        cvp.y = (int)(y*yScale);
        sendPoint.push_back(cvp);
    }
    Mat getMat;
    cutoutImagePacking->drawMask(sendPoint, lineWidth, getMat);
    cvtColor(getMat, getMat, CV_BGR2RGB);
};

JNIEXPORT jlong JNICALL Java_com_foretree_xopencv_OpenCVJni_selectPoint
        (JNIEnv *env, jclass jcl, jobjectArray array, int lineWidth) {
    LOGI("lineWidth = %d",lineWidth);
    jsize size = env->GetArrayLength(array);
    LOGI("size = %d",size);
    vector<cv::Point> sendPoint;
    for(int i =0;i< size;i++){
        jobject obj = env->GetObjectArrayElement(array,i);
        jclass clazz = env->GetObjectClass(obj);
        jfieldID fid_x = env->GetFieldID(clazz,"x","D");
        jdouble x = env->GetDoubleField(obj, fid_x);
        jfieldID fid_y = env->GetFieldID(clazz,"y","D");
        jdouble y = env->GetDoubleField(obj, fid_y);
        Point cvp;
        cvp.x = (int)(x*xScale);
        cvp.y = (int)(y*yScale);
        sendPoint.push_back(cvp);
    }
    Mat getMat;
    cutoutImagePacking->creatMask(sendPoint, lineWidth, getMat);
    cvtColor(getMat, getMat, CV_BGR2RGB);
    LOGI("getMat.cols = %d \n getMat.rows = %d",getMat.cols,getMat.rows);
    // todo 需要将getMat转换成bitmap
    Mat *h = new Mat(getMat);
    return (jlong) h;
};

JNIEXPORT void JNICALL Java_com_foretree_xopencv_OpenCVJni_setDeletePoint
        (JNIEnv *env, jclass jcl, jobjectArray array, int lineWidth) {
    LOGI("lineWidth = %d",lineWidth);
    jsize size = env->GetArrayLength(array);
    LOGI("size = %d",size);
    vector<cv::Point> sendPoint;
    for(int i =0;i< size;i++){
        jobject obj = env->GetObjectArrayElement(array,i);
        jclass clazz = env->GetObjectClass(obj);
        jfieldID fid_x = env->GetFieldID(clazz,"x","D");
        jdouble x = env->GetDoubleField(obj, fid_x);
        jfieldID fid_y = env->GetFieldID(clazz,"y","D");
        jdouble y = env->GetDoubleField(obj, fid_y);
        Point cvp;
        cvp.x = (int)(x*xScale);
        cvp.y = (int)(y*yScale);
        sendPoint.push_back(cvp);
    }
    Mat getMat;
    cutoutImagePacking->deleteMask(sendPoint, lineWidth, getMat);
    cvtColor(getMat, getMat, CV_BGR2RGB);
    LOGI("getMat.cols = %d \n getMat.rows = %d",getMat.cols,getMat.rows);
    // todo 需要将getMat转换成bitmap
};

JNIEXPORT void JNICALL Java_com_foretree_xopencv_OpenCVJni_resetAllMask
        (JNIEnv *, jclass) {
    Mat getMat;
    cutoutImagePacking->resetMask(getMat);
    cvtColor(getMat, getMat, CV_BGR2RGB);
    //todo Mat to Bitmap next Opration
};
JNIEXPORT void JNICALL Java_com_foretree_xopencv_OpenCVJni_redoPoint
        (JNIEnv *, jclass) {
    Mat getMat;
    cutoutImagePacking->redo(getMat);
    cvtColor(getMat, getMat, CV_BGR2RGB);
    //todo Mat to Bitmap next Opration

};
JNIEXPORT void JNICALL Java_com_foretree_xopencv_OpenCVJni_undoPoint
        (JNIEnv *, jclass) {
    Mat getMat;
    cutoutImagePacking->undo(getMat);
    cvtColor(getMat, getMat, CV_BGR2RGB);
    //todo Mat to Bitmap next Opration

};
JNIEXPORT jlong JNICALL Java_com_foretree_xopencv_OpenCVJni_getCutResult
        (JNIEnv *, jclass) {
    Mat cutResultMat = cutoutImagePacking->getFinalColorMergeImg();
    LOGI(" cutResultMat.channels() = %d ",cutResultMat.channels());
    cvtColor(cutResultMat, cutResultMat, CV_BGRA2RGB);
    Mat *h = new Mat(cutResultMat);
    return (jlong) h;
};

