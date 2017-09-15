package com.foretree.xopencv;

/**
 * Created by silen on 06/07/2017.
 * 扣脸 使用opencv的grabcut
 */

import org.opencv.core.Point;

/**
 * //-(id) init;
 * //-(id) initWithImage:(UIImage *)setImage;
 * //-(void) setCalculateImage:(UIImage *)setImage andWindowSize:(CGSize) winSize;
 * //-(void) setDrawPoint:(NSMutableArray*)selectPoint andLineWidth:(int)lineWidth;
 * //-(void) setCreatPoint:(NSMutableArray*)selectPoint andLineWidth:(int)lineWidth;
 * //-(void) setDeletePoint:(NSMutableArray*)selectPoint andLineWidth:(int)lineWidth;
 * //-(UIImage *) getCutResult;
 * //-(NSMutableArray *) getMutableCutResult;
 * //-(void) resetAllMask;
 * //-(void) redoPoint;
 * //-(void) undoPoint;
 */

public class OpenCVJni {
    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("g2uopen_v1");
    }
    public static native long cutout(long l);

    /**
     * 带有待计算图像的初始化程序
     *
     * @param l 要传入的图像
     * @return self指针
     */
    public static native void initWithImage(long l);

    /**
     *  设置待计算的图像，重置内部的所有计算用容器与状态，
     *  此函数被调用意味着1、输入了新图像 2、重置操作
     *
     *  @param l 图像输入
     *  @param s  外部显示view的窗口大小，用于输入坐标点与图像坐标点的转换
     */
    public static native void setCalculateImage(long l, float width, float height);

    /**
     *  将输入点直接转换成Mask点，其中没有图像算法
     *
     *  @param points 输入的Mask点坐标
     *  @param lineWidth   要花的线的线宽
     */
    public static native void setDrawPoint(Point[] points, int lineWidth);
    /**
     *  整体生长算法程序入口
     *
     *  @param points 外部传入的容器，其中包含生长点。
     *  @param lineWidth   线段宽度
     */
    public static native long selectPoint(Point[] points, int lineWidth);
    public static native void setDeletePoint(Point[] points, int lineWidth);
    /**
     *  重置所有生成的mask
     */
    public static native void resetAllMask();
    /**
     *  回退操作
     */
    public static native void redoPoint();
    /**
     *  前进操作
     */
    public static native void undoPoint();
    /**
     *  得到最终的分割结果
     */
    public static native long getCutResult();

}
