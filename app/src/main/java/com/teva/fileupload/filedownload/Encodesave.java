package com.teva.fileupload.filedownload;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

public class Encodesave {

    public boolean save(Context ctx,String filename, String in_filename) {
		FileCrypt fc = new FileCrypt();

		//save
		try {
			DataOutputStream saveData = fc.getSaveStream();
			/*
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream (new File(in_filename));
				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				// Put data in your baos

				baos.writeTo(fos);
			} catch(IOException ioe) {,
				// Handle exception here
				ioe.printStackTrace();
			} finally {
				if(fos != null) {
					fos.close();
				}
			}
			DataOutputStream saveData = new DataOutputStream(fos);
			*/
//			byte[] data = readBinaryFile(ctx,in_filename);

			saveData.writeInt(12345);
			saveData.writeBoolean(true);
			saveData.writeFloat(98765.12f);
			saveData.writeUTF("アッーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーー！");

//			saveData.writeUTF(new String(data));

			fc.save(ctx, filename);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * ファイルからバイナリデータを読み込む
	 * @param context
	 * @param fileName
	 * @return バイトデータ ファイルがない場合はnull
	 */
	public static byte[] readBinaryFile(Context context, String fileName) {
		// ファイルの存在チェック
		if (!(new File( fileName).exists())) {
			return null;
		}

		int size;
		byte[] data = new byte[1024];
		InputStream in = null;
		ByteArrayOutputStream out = null;

		try {
			//in = context.openFileInput(fileName);
			//in = new FileIutputStream(fileName);
			InputStream is = new FileInputStream(fileName);
			byte[] bytes = new byte[is.available()];
			is.read(bytes);
			is.close();

			/*
			out = new ByteArrayOutputStream();
			while ((size = in.read(data)) != -1) {
				out.write(data, 0, size);
			}
			return out.toByteArray();*/
			return bytes;
		} catch (Exception e) {
			// エラーの場合もnullを返すのでここでは何もしない
		} finally {
			try {
				if (in != null) in.close();
				if (out != null) out.close();
			} catch (Exception e) {
			}
		}

		return null;
	}

	public String load(Context ctx,String filename){
    	//load
		String loaded ="";
		try {
			FileCrypt fc = new FileCrypt();

			DataInputStream loadData = fc.load(ctx, filename);


			loaded  = loaded + loadData.readInt() + ":";
			loaded  = loaded + loadData.readBoolean() + ":";
			loaded  = loaded + loadData.readFloat() + ":";
			loaded  = loaded + loadData.readUTF() + ":";

	        // Toast.makeText(this, loaded, Toast.LENGTH_LONG).show();

		} catch (Exception e) {
			e.printStackTrace();
		}


		return loaded;

    }
}