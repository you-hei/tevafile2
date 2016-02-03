package com.teva.fileupload.filedownload;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;

import android.os.Environment;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {
    private final String DOWNLOAD_FILE_URL = "unko";
    private ProgressDialog progressDialog;
    private ProgressHandler progressHandler;
    private AsyncFileDownload asyncfiledownload;
    private File outputFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        progressHandler = new ProgressHandler();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void download_click(View view){
        initFileLoader();
        showDialog(0);
        progressHandler.progressDialog = progressDialog;
        progressHandler.asyncfiledownload = asyncfiledownload;

        if (progressDialog != null && asyncfiledownload != null){
            progressDialog.setProgress(0);
            progressHandler.sendEmptyMessage(0);
        }else{
            Toast ts = Toast.makeText(this, "NULLエラー", Toast.LENGTH_LONG);
            ts.show();
        }
    }

    public void play_click(View view){


        VideoView videoView = (VideoView) findViewById(R.id.video);
        File sdCard = Environment.getExternalStorageDirectory();
        videoView.setVideoPath(sdCard.getAbsolutePath() + "/SampleFolder6/test7.mp4");
        videoView.start();

        /*
        Encodesave es =  new Encodesave();
        boolean ret = es.save((Context)this,"savedata","ああああああああああああ");
        String value = es.load((Context)this,"savedata");
        Toast.makeText(this, value, Toast.LENGTH_LONG).show();
        */

    }

    public void encode_click(View view){
        Encodesave es =  new Encodesave();
        File sdCard = Environment.getExternalStorageDirectory();
        String filename = sdCard.getAbsolutePath() + "/SampleFolder6/test7.mp4";
        String savefile_path = sdCard.getAbsolutePath() + "/SampleFolder6/savedata";
        boolean ret = es.save((Context)this,"savedata","savedata");
//        String value = es.load((Context)this,"savedata");

    }

    public void decode_click(View view){
        Encodesave es =  new Encodesave();
        File sdCard = Environment.getExternalStorageDirectory();
        String savefile_path = sdCard.getAbsolutePath() + "/SampleFolder6/savedata";
        String value = es.load((Context) this, "savedata");
        Toast.makeText(this, value, Toast.LENGTH_LONG).show();
    }

    private void initFileLoader()
    {
        File sdCard = Environment.getExternalStorageDirectory();


        File directory = new File(sdCard.getAbsolutePath() + "/SampleFolder6");

        //File directory = getDir(sdCard.getAbsolutePath() + "/SampleFolder6",MODE_PRIVATE);
        if(directory.exists() == false){
            if (directory.mkdir() == true){
            }else{
                Toast ts = Toast.makeText(this, "ディレクトリ作成に失敗", Toast.LENGTH_LONG);
                ts.show();
            }
        }
        //File outputFile = new File(directory, "test.jpg");
        File outputFile = new File(directory, "test6");
        String outputFilePath = outputFile.getPath();
        /*
        outputFile.setReadable(false, true);
        outputFile.setWritable(false, true);
        outputFile.setExecutable(false, true);
        */

        asyncfiledownload = new AsyncFileDownload(this,DOWNLOAD_FILE_URL, outputFile);
        asyncfiledownload.execute();
    /*
    //内部メモリの領域を用いる場合
    File dataDir = this.getFilesDir();
    File directory = new File(dataDir.getAbsolutePath()+ "/SampleFolder");
    if(directory.exists() == false){
      if (directory.mkdir() == true){
      }else{
        Toast ts = Toast.makeText(this, "ディレクトリ作成に失敗", Toast.LENGTH_LONG);
        ts.show();
      }
    }
    File outputFile = new File(directory, "test.jpg");
    asyncfiledownload = new AsyncFileDownload(this,DOWNLOAD_FILE_URL, outputFile);
    asyncfiledownload.execute();
    */
    }

    @Override
    protected void onPause(){
        super.onPause();
        cancelLoad();
    }

    @Override
    protected void onStop(){
        super.onStop();
        cancelLoad();
    }

    private void cancelLoad()
    {
        if(asyncfiledownload != null){
            asyncfiledownload.cancel(true);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id){
        switch(id){
            case 0:
                progressDialog = new ProgressDialog(this);
                //progressDialog.setIcon(R.drawable.ic_launcher);
                progressDialog.setTitle("Downloading files..");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Hide",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cancelLoad();
                            }
                        });
        }
        return progressDialog;
    }
}