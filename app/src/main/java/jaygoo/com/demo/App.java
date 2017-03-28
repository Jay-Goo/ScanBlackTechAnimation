package jaygoo.com.demo;

import android.app.Application;

/**
 * ================================================
 * 作    者：JayGoo
 * 版    本：1.1.0
 * 创建日期：2017/3/27
 * 描    述:
 * ================================================
 */
public class App extends Application {

    @Override public void onCreate() {
        super.onCreate();
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);
//        // Normal app init code...
//        CrashReport.initCrashReport(getApplicationContext(), "f876e1d1ca", true);

    }
}
