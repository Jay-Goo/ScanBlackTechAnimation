package animation.jaygoo.com.blackdemo;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import jaygoo.com.animation.AnimatorEndListener;
import jaygoo.com.animation.ScanBlackTechBuilder;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class MainActivity extends AppCompatActivity {

    private int times;
    ScanBlackTechBuilder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (times %2 == 0){
                    gallery();

                }else {
                    builder.revert();
                }
                times++;
            }
        });
    }


    /**
     * 从相册获取
     */
    public void gallery() {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    /**
     * 选择图片返回结果处理
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {//相册
                if (data != null) {
                    // 得到图片的全路径
                    Uri uri = data.getData();
                    if (uri != null) {
                        File file = new File(getRealFilePath(getApplicationContext(),uri));

                        Luban.get(getApplicationContext())
                                .load(file)
                                .putGear(3)
                                .setCompressListener(new OnCompressListener() {
                                    @Override
                                    public void onStart() {
                                    }

                                    @Override
                                    public void onSuccess(final File file) {

                                                builder = new ScanBlackTechBuilder();
                                                builder.build(MainActivity.this,R.id.container,file.getPath())
                                                        .setOnAnimatorEndListener(new AnimatorEndListener() {
                                                            @Override
                                                            public void onAnimatorEnd() {
                                                                builder.release();

                                                            }
                                                        })
                                                        .start();




                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Toast.makeText(getApplication(),e.getMessage(),Toast.LENGTH_LONG).show();
                                        e.printStackTrace();

                                    }
                                }).launch();
                    }

                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Try to return the absolute file path from the given Uri
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePath(final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
}
