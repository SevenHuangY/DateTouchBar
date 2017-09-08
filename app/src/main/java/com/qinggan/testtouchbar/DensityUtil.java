package com.qinggan.testtouchbar;

import android.content.Context;

/**
 * Description:
 * Author: Seven
 * Mail: huangyawen.happy@gmail.com
 * Date: 17-8-30.
 */

public class DensityUtil
{
    public static int dip2px(Context context, float dpValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
