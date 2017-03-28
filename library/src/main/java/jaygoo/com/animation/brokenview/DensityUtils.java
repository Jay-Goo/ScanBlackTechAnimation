package jaygoo.com.animation.brokenview;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by JayGoo on 16/9/5.
 */
public class DensityUtils {

    public static int dip2px(float dip, Context ctx) {
        float density = ctx.getResources().getDisplayMetrics().density;
        int px = (int) (dip * density + 0.5f);
        return px;
    }

    public static float px2dip(int px, Context ctx) {
        float density = ctx.getResources().getDisplayMetrics().density;
        float dp = px / density;
        return dp;
    }

    public static DisplayMetrics getMetrics(Activity activity) {
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric;
    }

    /**
     * 屏幕宽度（像素）
     *
     * @param activity
     * @return
     */
    public static int getDeviceWith(Activity activity) {
        return getMetrics(activity).widthPixels;
    }

    /**
     * 屏幕高度（像素）
     *
     * @param activity
     * @return
     */
    public static int getDeviceHeight(Activity activity) {
        return getMetrics(activity).heightPixels;   // 屏幕高度（像素）
    }
}
