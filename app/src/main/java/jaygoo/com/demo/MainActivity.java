package jaygoo.com.demo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


import jaygoo.com.animation.ScanBlackTechBuilder;
import jaygoo.com.animation.AnimatorEndListener;

public class MainActivity extends AppCompatActivity {

    int[] btps = {R.drawable.aaa,R.drawable.bbb,R.drawable.newsweek_1933};
    int times = 1;
    ScanBlackTechBuilder animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.fragment_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (times %2 == 1) {
                    final Bitmap srcBitmap = BitmapFactory.decodeResource(getResources(), btps[times % 3]);
                    animation = new ScanBlackTechBuilder();
                    animation.build(MainActivity.this, R.id.fragment_container, srcBitmap)
                            .setOnAnimatorEndListener(new AnimatorEndListener() {
                                @Override
                                public void onAnimatorEnd() {
                                    findViewById(R.id.fragment_container).setVisibility(View.GONE);

                                }
                            })
                            .start();
                }else {
                    animation.revert();
                }
                times ++;
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
