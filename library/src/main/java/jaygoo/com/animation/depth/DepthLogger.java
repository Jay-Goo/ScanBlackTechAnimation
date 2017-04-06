package jaygoo.com.animation.depth;

import android.util.Log;

/**
 * Created by florentchampigny on 06/03/2017.
 */

class DepthLogger {
    public static boolean enabled = true ;

    public static void log(String string){
        if(enabled) {
            Log.d("DepthLogger", string);
        }
    }
}
