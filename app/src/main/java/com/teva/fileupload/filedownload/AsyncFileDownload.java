package com.teva.fileupload.filedownload;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import android.util.Log;

import android.app.Activity;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;

public class AsyncFileDownload extends AsyncTask<String, Void, Boolean>{
    private final String TAG = "AsyncFileDownload";
    private final int TIMEOUT_READ = 5000;
    private final int TIMEOUT_CONNECT = 30000;

    public Activity owner;
    private final int BUFFER_SIZE = 1024;

    private String urlString;
    private File outputFile;
    private FileOutputStream fileOutputStream;
    private InputStream inputStream;
    private BufferedInputStream bufferedInputStream;

    private int totalByte = 0;
    private int currentByte = 0;

    private byte[] buffer = new byte[BUFFER_SIZE];

    private URL url;
    private URLConnection urlConnection;

    public AsyncFileDownload(Activity activity, String url, File oFile) {
        owner = activity;
        urlString = url;
        outputFile = oFile;
    }

    @Override
    protected Boolean doInBackground(String... url) {
        try{
            connect();
        }catch(IOException e){
            Log.d(TAG, "ConnectError:" + e.toString());
            cancel(true);
        }

        if(isCancelled()){
            return false;
        }
        if (bufferedInputStream !=  null){
            try{
                int len;
                while((len = bufferedInputStream.read(buffer)) != -1){
                    fileOutputStream.write(buffer, 0, len);
                    currentByte += len;
                    //publishProgress();
                    if(isCancelled()){
                        break;
                    }
                }
                // exec("/system/bin/chmod", "700", outputFile.getPath());
                //String[] commands = {"chmod " + "600 " +outputFile.getPath()};
                //String[] commands = {"chmod","600" , outputFile.getPath()};
                //String[] commands = {"chmod","600" , outputFile.getPath()};
                //adbComandExe( commands);
            }catch(IOException e){
                Log.d(TAG, e.toString());
                return false;
            }
        }else{
            Log.d(TAG, "bufferedInputStream == null");
        }

        try{
            close();
        }catch(IOException e){
            Log.d(TAG, "CloseError:" + e.toString());
        }
        return true;
    }

    @Override
    protected void onPreExecute(){
    }

    @Override
    protected void onPostExecute(Boolean result){
    }

    @Override
    protected void onProgressUpdate(Void... progress){
    }

    private void connect() throws IOException
    {
        url = new URL(urlString);
        urlConnection = url.openConnection();
        urlConnection.setReadTimeout(TIMEOUT_READ);
        urlConnection.setConnectTimeout(TIMEOUT_CONNECT);
        inputStream = urlConnection.getInputStream();
        bufferedInputStream = new BufferedInputStream(inputStream, BUFFER_SIZE);
        fileOutputStream = new FileOutputStream(outputFile);
        //fileOutputStream = openFileOutput(outputFile.getPath(), MODE_PRIVATE);

        totalByte = urlConnection.getContentLength();
        currentByte = 0;
    }

    private void close() throws IOException
    {
        fileOutputStream.flush();
        fileOutputStream.close();
        bufferedInputStream.close();
    }

    public int getLoadedBytePercent()
    {
        if(totalByte <= 0){
            return 0;
        }
        return (int)Math.floor(100 * currentByte/totalByte);
    }
    private String adbComandExe(String[] command) {
        File file = new File(outputFile.getPath());
        if(file.exists()){
            System.out.print("exist");
        }
        StringBuffer sBuffer = new StringBuffer();
        ProcessBuilder processBuilder = new ProcessBuilder(command[0]);

        try {
            Process proc = processBuilder.start();
            InputStream iStream = proc.getInputStream();
            InputStreamReader isReader = new InputStreamReader(iStream);
            BufferedReader bufferedReader = new BufferedReader(isReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                sBuffer.append(line + "\n");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return sBuffer.toString();
    }
}

