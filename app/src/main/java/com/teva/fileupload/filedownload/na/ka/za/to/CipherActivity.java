package com.teva.fileupload.filedownload.na.ka.za.to;

import android.content.Context;
import android.graphics.PixelFormat;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.MediaController;

import com.teva.fileupload.filedownload.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
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
    static final String ENCODED_FILENAME="/ko.shi.hi.ka.ri", MOVIE_FILE="ofisu.m4v", MOVIE_FILE_CHAS="ofisu_chas.m4v";
    @Bind(R.id.video_surface) SurfaceView mVideo;
    @Bind(R.id.encode_type) TextView mEncodeType;
    static File sSdCard = Environment.getExternalStorageDirectory();
    static String sSdCardPath = sSdCard.getAbsolutePath();
    SurfaceHolder mSurfaceHolder;
    MediaPlayer mMediaPlayer;
    MediaController mMediaController;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cipher);
        ButterKnife.bind(this);

        getWindow().setFormat(PixelFormat.TRANSPARENT);

        mSurfaceHolder = mVideo.getHolder();
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
        final long startTime = System.currentTimeMillis();
        new DecodeAsync(startTime) {
            @Override
            protected void onPostExecute(Uri uri) {
                long endTime = System.currentTimeMillis();
                long time = endTime - startTime;
                String m = "でコードおわた\n経過時間："+time+"ms";
                Log.d(LOG_TAG, "でコードおわた");
                Log.d(LOG_TAG, "経過時間："+time+"ms");
                Toast.makeText(CipherActivity.this, m, Toast.LENGTH_SHORT).show();
                try {
                    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            Log.d(LOG_TAG, "プリペアおわた");
                            mp.start();
                        }
                    });
                    mMediaPlayer.setDataSource(CipherActivity.this, uri);
                    mMediaPlayer.setDisplay(mSurfaceHolder);
                    mMediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    /** エンコード処理を非同期でする */
    public static class EncodeAsync extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String filename = sSdCardPath + "/SampleFolder6/" + MOVIE_FILE;
            File src = new File(filename);
            Key key = new SecretKeySpec("0123456789ABCDEF".getBytes(), "AES");
            CipherOutputStream cos = null;
            FileOutputStream fos = null;
            try {
                FileInputStream fis = new FileInputStream(src);
                Cipher cipher = Cipher.getInstance(ENCODE);
                cipher.init(Cipher.ENCRYPT_MODE, key);
                fos = new FileOutputStream(sSdCardPath + ENCODED_FILENAME);
                cos = new CipherOutputStream(fos, cipher);
                fos.write(cipher.getIV());
                byte[] a = new byte[8000];
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

    /** でコードする処理非同期の奴　内部領域に保存してそいつのUriを返すバジョーン */
    static final String DECODED_FILE_NAME="deco_file_ofisu.mp4";
    public class DecodeAsync extends AsyncTask<String, Void, Uri> {
        long mStart;
        public DecodeAsync(long start) {
            mStart = start;
        }
        @Override
        protected Uri doInBackground(String... params) {
            Key key = new SecretKeySpec("0123456789ABCDEF".getBytes(), "AES");
            String outFilePath = getFilesDir().getPath() + MOVIE_FILE;
            BufferedInputStream fis = null;
            CipherInputStream cis = null;
            BufferedOutputStream fos = null;
            File file = null;
            try {
                fis = new BufferedInputStream(new FileInputStream(sSdCardPath + ENCODED_FILENAME));
                byte[] iv = new byte[16];
                fis.read(iv);
                Cipher cipher = Cipher.getInstance(ENCODE);
                IvParameterSpec ivspec = new IvParameterSpec(iv);
                cipher.init(Cipher.DECRYPT_MODE, key, ivspec);
                cis = new CipherInputStream(fis, cipher);
                long time = System.currentTimeMillis() - mStart;
                Log.d(LOG_TAG, "CipherInputStreamに値が入りました。時間："+time);

                byte[] readByte = new byte[512];
                int i = cis.read(readByte);

                fos = new BufferedOutputStream(openFileOutput(DECODED_FILE_NAME, Context.MODE_PRIVATE));

                while(i != -1) {
                    fos.write(readByte, 0, i);
                    i = cis.read(readByte);
                }

                fos.flush();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fis != null) fis.close();
                    if (cis != null) cis.close();
                    if (fos != null) fos.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            return Uri.parse(getFilesDir() + "/" +DECODED_FILE_NAME);
        }
    }

    // サーフェスビューの処理
    SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

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

    @OnClick(R.id.copyPestButton)
    public void onClickCopype(Button b) {
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

    @OnClick(R.id.generateHeadPlusFile)
    public void onClickGenerateUnko(Button b) {
        new GenerateAsync(System.currentTimeMillis()) {
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        }.execute();
    }

    public static final String CHAS_FILE="chasFile";
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

    @OnClick(R.id.removeHeadAndPlayMoviewButton)
    public void onClickRemoveHeadPlayButton(Button b) {
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
