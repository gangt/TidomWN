package face;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class ScreenUtil {
    public static int mNotificationBarHeight;
    public static int mScreenWidth;
    public static int mScreenHeight;
    public static int mWidth;
    public static int mHeight;
    public static float densityDpi;
    public static float density;

    public static float drawWidth;
    public static float drawHeight;

    private static final int PADDING_L = 30;
    private static final int PADDING_R = 30;
    private static final int PADDING_T = 50;
    private static final int PADDING_B = 40;

    public static float drawPaddingLeft;
    public static float drawPaddingRight;
    public static float drawPaddingTop;
    public static float drawPaddingBottom;

    public static void initialize(Context context) {
        if (drawWidth == 0 || drawHeight == 0 || mWidth == 0 || mHeight == 0 || density == 0) {
            Resources res = context.getResources();
            DisplayMetrics metrics = res.getDisplayMetrics();
            density = metrics.density;
            mNotificationBarHeight = (int) (35 * density);
            mWidth = metrics.widthPixels;
            mHeight = metrics.heightPixels;
            mScreenWidth = metrics.widthPixels;
            mScreenHeight = metrics.heightPixels;

            densityDpi = metrics.densityDpi;
            drawPaddingLeft = density * PADDING_L;
            drawPaddingRight = density * PADDING_R;
            drawPaddingTop = density * PADDING_T;
            drawPaddingBottom = density * PADDING_B;

            drawWidth = mWidth - drawPaddingLeft - drawPaddingRight;
            // 如果非全屏，则减去标题栏的高度
            drawHeight = mHeight - drawPaddingTop - drawPaddingBottom;
        }
    }
}