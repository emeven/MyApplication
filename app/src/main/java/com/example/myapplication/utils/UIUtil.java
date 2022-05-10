package com.example.myapplication.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.DisplayMetrics;

/**
 * @author Perceiver
 * @date 2019/2/14 下午2:54
 */
public class UIUtil {

    private static final DisplayMetrics sMetrics = Resources.getSystem().getDisplayMetrics();

//    /**
//     * 获取屏幕宽度
//     * Use getScreenWidth() instead
//     *
//     * @param context
//     * @return
//     */
//    @Deprecated
//    public static int getDisplayWidthPixels(Context context) {
//        return getScreenWidth();
//    }

    public static int getScreenWidth() {
        return sMetrics != null ? sMetrics.widthPixels : 0;
    }
//
//    /**
//     * 获取屏幕高度
//     * Use getScreenHeight() instead
//     *
//     * @param context
//     * @return
//     */
//    @Deprecated
//    public static int getDisplayheightPixels(Context context) {
//        return getScreenHeight();
//    }
//
//    public static int getScreenHeight() {
//        return sMetrics != null ? sMetrics.heightPixels : 0;
//    }
//
//    public static int px2dp(float pxValue) {
//        final float scale = sMetrics != null ? sMetrics.density : 1;
//        return (int) (pxValue / scale + 0.5f);
//    }
//
//    /**
//     * 将dip或dp值转换为px值
//     *
//     * @param dipValue
//     * @return
//     */
//    @Deprecated
//    public static int dip2px(Context context, float dipValue) {
//        return dp2px(dipValue);
//    }
//
//    /**
//     *
//     * @param dipValue
//     * @return
//     *
//     * {#com.xingin.utils.ext.dp}
//     */
//    @Deprecated
//    public static int dp2px(float dipValue) {
//        final float scale = sMetrics != null ? sMetrics.density : 1;
//        return (int) (dipValue * scale + 0.5f);
//    }
//
//    /**
//     *
//     * @param spValue
//     * @return
//     *
//     * {#com.xingin.utils.ext.sp}
//     */
//    @Deprecated
//    public static int sp2px(float spValue) {
//        final float fontScale = sMetrics != null ? sMetrics.scaledDensity : 1;
//        return (int) (spValue * fontScale + 0.5f);
//    }
//
//    public static int getDimensionPixelSize(Context context, int resId) {
//        return context.getResources().getDimensionPixelSize(resId);
//    }
//
//    /**
//     * 获取TextView 的width
//     */
//    public static Float getTextWidth(String str, Typeface typeface, Float textSize) {
//        if (TextUtils.isEmpty(str)) return 0f;
//        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        paint.setTextSize(textSize);
//        paint.setTypeface(typeface);
//        return paint.measureText(str);
//    }
}
