package com.teva.fileupload.filedownload.na.ka.za.to;

import android.databinding.DataBindingUtil;
import android.graphics.PixelFormat;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.MediaController;

import com.teva.fileupload.filedownload.R;
import com.teva.fileupload.filedownload.databinding.ActivityCipherBinding;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by nkzt on 16/02/03.
 */
public class CipherActivity extends AppCompatActivity implements CipherActivityClickHandle {
    static final byte bb = 5;
    static final String LOG_TAG=CipherActivity.class.getSimpleName();
    static final String ENCODE="AES/CBC/PKCS5Padding";
    static final String ENCODED_FILENAME = "/ko.shi.hi.ka.ri",
            MOVIE_FILE = "ofisu.m4v", MOVIE_FILE_CHAS = "ofisu_chas.m4v",
            DECODED_FILE_NAME = "deco_file_ofisu.mp4", CHAS_FILE = "chasFile";
    static File sSdCard = Environment.getExternalStorageDirectory();
    static String sSdCardPath = sSdCard.getAbsolutePath();
    SurfaceHolder mSurfaceHolder;
    MediaPlayer mMediaPlayer;
    MediaController mMediaController;
    private ActivityCipherBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_cipher);
        mBinding = (ActivityCipherBinding) DataBindingUtil.setContentView(this, R.layout.activity_cipher);
        mBinding.setClickHandler(this);

        getWindow().setFormat(PixelFormat.TRANSPARENT);

        mSurfaceHolder = mBinding.videoSurface.getHolder();
        mSurfaceHolder.addCallback(mCallback);

        mMediaPlayer = new MediaPlayer();
        mMediaController = new MediaController(this);
        mMediaController.setMediaPlayer(mControll);

        String str = getFilesDir().toString() + "/" +DECODED_FILE_NAME;
        File f = new File(str);

        f.delete();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.encodeType.setText(ENCODE);
    }

    @Override
    public void onClickCopype(View v) {
        // コピペボタンクリック
        new CopyPeAsync(System.currentTimeMillis()) {
            @Override
            protected void onPostExecute(String outFilePath) {
                try {
                    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            Log.d(LOG_TAG, "プリペアおわた");
                            mp.start();
                        }
                    });
                    Uri uri = Uri.parse(outFilePath);
                    mMediaPlayer.setDataSource(CipherActivity.this, uri);
                    mMediaPlayer.setDisplay(mSurfaceHolder);
                    mMediaPlayer.prepare();
                } catch (Exception e) {

                }
            }
        }.execute();
    }

    @Override
    public void onClickGenerateUnko(View v) {
        // 動画ファイルの先頭に文字を付与するボタン
        new GenerateAsync(System.currentTimeMillis()) {
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        }.execute();
    }

    @Override
    public void onClickRemoveHeadPlayButton(View v) {
        // 動画ファイルの先頭に付与された文字を取り除いて再生するボタン
        new RemoveHead(System.currentTimeMillis()) {
            @Override
            protected void onPostExecute(String s) {
                try {
                    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            Log.d(LOG_TAG, "プリペアおわた");
                            mp.start();
                        }
                    });
                    Uri uri = Uri.parse(s);
                    mMediaPlayer.setDataSource(CipherActivity.this, uri);
                    mMediaPlayer.setDisplay(mSurfaceHolder);
                    mMediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    // サーフェスビューの処理
    SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {}
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (mMediaPlayer != null) {
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }
    };

    // メディアコントロールの諸々の処理
    MediaController.MediaPlayerControl mControll = new MediaController.MediaPlayerControl() {
        @Override
        public void start() {}
        @Override
        public void pause() {}
        @Override
        public int getDuration() {return 0;}
        @Override
        public int getCurrentPosition() {return 0;}
        @Override
        public void seekTo(int pos) {}
        @Override
        public boolean isPlaying() {return false;}
        @Override
        public int getBufferPercentage() {return 0;}
        @Override
        public boolean canPause() {return false;}
        @Override
        public boolean canSeekBackward() {return false;}
        @Override
        public boolean canSeekForward() {return false;}
        @Override
        public int getAudioSessionId() {return 0;}
    };

    class CopyPeAsync extends AsyncTask<String, Void, String> {
        long mStartTime;
        public CopyPeAsync(long startTime) {
            mStartTime = startTime;
        }
        @Override
        protected String doInBackground(String... params) {
            String inFilePath = sSdCardPath + "/SampleFolder6/"+MOVIE_FILE;
            String outFilePath = getFilesDir().getPath() + "/" + MOVIE_FILE;
            File src = new File(inFilePath);
            BufferedOutputStream bos = null;
            BufferedInputStream bis = null;
            try {
                bis = new BufferedInputStream(new FileInputStream(src));
                bos = new BufferedOutputStream(new FileOutputStream(outFilePath));
                byte[] a = new byte[80000];
                int i = bis.read(a);
                while (i != -1) {
                    bos.write(a, 0, i);
                    i = bis.read(a);
                }
                bos.flush();
                Log.d(LOG_TAG, "コピペ終わり経過時間" + (System.currentTimeMillis() - mStartTime));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bos != null) bos.close();
                    if (bis != null) bis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return outFilePath;
        }
    }

    class GenerateAsync extends AsyncTask<String, Void, String> {
        long mStartTime;
        public GenerateAsync(long startTime) {
            mStartTime = startTime;
        }
        @Override
        protected String doInBackground(String... params) {
            String inFilePath = sSdCardPath + "/SampleFolder6/"+MOVIE_FILE;
            String outFilePath = sSdCardPath + "/SampleFolder6/"+MOVIE_FILE_CHAS;
            String chasFilePath = sSdCardPath + "/SampleFolder6/"+CHAS_FILE;
            File src = new File(inFilePath);
            BufferedOutputStream bos = null;
            BufferedInputStream bis = null;

            File chasFile = new File(chasFilePath);
            BufferedInputStream chabis = null;
            try {
                bis = new BufferedInputStream(new FileInputStream(src));
                bos = new BufferedOutputStream(new FileOutputStream(outFilePath));

                chabis = new BufferedInputStream(new FileInputStream(chasFile));
                byte[] cha = new byte[8];
                int chaint = chabis.read(cha);
                while(chaint != -1) {
                    bos.write(cha, 0, chaint);
                    chaint = chabis.read(cha);
                }

                byte[] a = new byte[80000];
                int i = bis.read(a);
                while (i != -1) {
                    bos.write(a, 0, i);
                    i = bis.read(a);
                }
                bos.flush();
                Log.d(LOG_TAG, "ファイル生成おわた経過時間" + (System.currentTimeMillis() - mStartTime));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bos != null) bos.close();
                    if (bis != null) bis.close();
                    if (chabis != null) chabis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return outFilePath;
        }
    }

    class RemoveHead extends AsyncTask<String, Void, String> {
        long mStartTime;
        public RemoveHead(long startTime) {
            mStartTime = startTime;
        }
        @Override
        protected String doInBackground(String... params) {
            String inFilePath = sSdCardPath + "/SampleFolder6/"+MOVIE_FILE_CHAS;
            String outFilePath = getFilesDir().getPath() + "/" + MOVIE_FILE_CHAS;
            String chasFilePath = sSdCardPath + "/SampleFolder6/"+CHAS_FILE;
            File src = new File(inFilePath);
            BufferedOutputStream bos = null;
            BufferedInputStream bis = null;
            BufferedInputStream chabis = null;
            File chasFile = new File(chasFilePath);
            try {
                bis = new BufferedInputStream(new FileInputStream(src));
                bos = new BufferedOutputStream(new FileOutputStream(outFilePath));
                chabis = new BufferedInputStream(new FileInputStream(chasFile));
                byte[] a = new byte[80000];
                bis.skip(getInputStreamLength(chabis));
                int i = bis.read(a);
                while (i != -1) {
                    bos.write(a, 0, i);
                    i = bis.read(a);
                }
                bos.flush();
                Log.d(LOG_TAG, "先頭のちゃす文字剥がし　作成経過時間" + (System.currentTimeMillis() - mStartTime));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bos != null) bos.close();
                    if (bis != null) bis.close();
                    if (chabis != null) chabis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return outFilePath;
        }
    }

    int getInputStreamLength(BufferedInputStream bis) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] b = new byte[8];
        int size = 0;
        int streamSize = 0;
        try {
            while ((size = bis.read(b, 0, b.length)) != -1) {
                baos.write(b, 0, size);
            }
            streamSize = baos.size();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) baos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return streamSize;
    }
}
