package com.teva.fileupload.filedownload.na.ka.za.to;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.teva.fileupload.filedownload.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by nkzt on 16/02/03.
 */
public class CipherActivity extends AppCompatActivity {
    static final String LOG_TAG=CipherActivity.class.getSimpleName();
    static final String ENCODE="AES/CBC/PKCS5Padding";
    @Bind(R.id.video_view) VideoView mVideo;
    @Bind(R.id.encode_type) TextView mEncodeType;
    static File sdCard = Environment.getExternalStorageDirectory();
    static String path = sdCard.getAbsolutePath();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cipher);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEncodeType.setText(ENCODE);
    }

    /** エンコードボタン */
    @OnClick(R.id.encode_button)
    public void onClickEncode(Button b) {
        final long startTime = System.currentTimeMillis();
        new EncodeAsync() {
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                long endTime = System.currentTimeMillis();
                long time = endTime - startTime;
                String m = "エンコードおわた\n経過時間："+time+"ms";
                Log.d(LOG_TAG, "エンコードおわた");
                Log.d(LOG_TAG, "経過時間："+time+"ms");
                Toast.makeText(CipherActivity.this, m, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    /** でコードボタン */
    @OnClick(R.id.decode_button)
    public void onClickDecode(Button b) {

    }

    /** エンコード処理を非同期でする */
    public static class EncodeAsync extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String filename = path + "/SampleFolder6/test7.mp4";
            File src = new File(filename);
            Key key = new SecretKeySpec("0123456789ABCDEF".getBytes(), "AES");
            CipherOutputStream cos = null;
            FileOutputStream fos = null;
            try {
                FileInputStream fis = new FileInputStream(src);
                Cipher cipher = Cipher.getInstance(ENCODE);
                cipher.init(Cipher.ENCRYPT_MODE, key);
                fos = new FileOutputStream(path + "/hoge.hoge");
                cos = new CipherOutputStream(fos, cipher);
                fos.write(cipher.getIV());
                byte[] a = new byte[8];
                int i = fis.read(a);
                while (i != -1) {
                    cos.write(a, 0, i);
                    i = fis.read(a);
                }
                cos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (cos != null) cos.close();
                    if (fos != null) fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    }

    public static class DecodeAsync extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            Key key = new SecretKeySpec("0123456789ABCDEF".getBytes(), "AES");
            try {
                FileInputStream fis = new FileInputStream(path + "/hoge.hoge");
                byte[] iv = new byte[16];
                fis.read(iv);
                Cipher cipher = Cipher.getInstance("AES/PCBC/PKCS5Padding");
                IvParameterSpec ivspec = new IvParameterSpec(iv);
                cipher.init(Cipher.DECRYPT_MODE, key, ivspec);
                CipherInputStream cis = new CipherInputStream(fis, cipher);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
