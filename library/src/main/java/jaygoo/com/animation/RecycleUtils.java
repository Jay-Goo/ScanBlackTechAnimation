package jaygoo.com.animation;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * ================================================
 * 作    者：JayGoo
 * 版    本：1.1.0
 * 创建日期：2017/3/29
 * 描    述:
 * ================================================
 */
public class RecycleUtils {

    public static void recycleBitmap(Bitmap bitmap){
        if (bitmap != null && !bitmap.isRecycled()){
            bitmap.recycle();
            bitmap = null;
            System.gc();
        }
    }

    public static void recycleImageView(ImageView imageView){
        if (imageView != null && imageView.getDrawable() != null){
            BitmapDrawable bitmapDrawable = ((BitmapDrawable) imageView.getDrawable());
            if (bitmapDrawable != null && bitmapDrawable instanceof BitmapDrawable){
                RecycleUtils.recycleBitmap(bitmapDrawable.getBitmap());
                imageView.setImageDrawable(null);
            }
        }
    }




}
