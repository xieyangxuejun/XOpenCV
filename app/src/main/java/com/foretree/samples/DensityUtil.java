package com.foretree.samples;

import android.content.Context;
import android.content.res.Resources;

/**
 * Copyright 2014 Xiaoluo's Studio
 *
 * @author xiaoluo
 * @version create time: 2014年10月22日 - 上午10:24:21
 */
public class DensityUtil {

    /* 根据手机的分辨率从 dp 的单位 转成为 px(像素) */
    public static int dip2px(float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(float pxValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getDensity() {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (scale + 0.5f);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int sp2px(Context context, int spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) ((spValue - 0.5f) * fontScale);
    }

    /* 获取手机的通知栏的高度 */
    public static int getStatusBarHeight() {
        return Resources.getSystem().getDimensionPixelSize(
                Resources.getSystem().getIdentifier("status_bar_height",
                        "dimen", "android"));
    }

    /* 获取手机屏幕的宽度 */
    public static int getMetricsWidth(Context context) {
        if (context == null) {
            return 0;
        }
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /* 获取手机屏幕的高度 */
    public static int getMetricsHeight(Context context) {
        if (context == null) {
            return 0;
        }
        return context.getResources().getDisplayMetrics().heightPixels;
    }
}