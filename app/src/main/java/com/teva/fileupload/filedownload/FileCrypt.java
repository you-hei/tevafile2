package com.teva.fileupload.filedownload;

import android.app.Activity;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.security.GeneralSecurityException;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import android.content.Context;
import android.provider.Settings.Secure;

public class FileCrypt  {
    private static final String KEYGEN_ALGORITHM = "PBEWITHSHAAND256BITAES-CBC-BC";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
  //適当に変えること
    private static final String RANDOM_KEY = "obutuwashoodokuda--";
  //適当に変えること
    private static final byte[] IV =
        { 36, 64, 61, -90, 22, 91, -37, 82, 107, -44, 0, -49, 60, 85, -2, 64 };
  //適当に変えること
    private static final byte[] SALT = new byte[] {
        -26, 75, 40, -118, -123, -47, 84, -54, 41, 98, -85, -35, 87, -127, -46, -123, -21, 42, -74, 99
    };

    private Cipher mEncryptor;
    private Cipher mDecryptor;
    private ByteArrayOutputStream bo;
    private DataOutputStream saveData;

	public FileCrypt(){
		super();

		//端末ごとに異なる暗号化をするならば、ここに端末由来の固有のキーを設定する。例：androidID
		//String deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		//エミュレータの場合androidIDは取得できない
		String deviceId = "emulator";

		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance(KEYGEN_ALGORITHM);
			KeySpec keySpec =
			new PBEKeySpec((RANDOM_KEY + deviceId).toCharArray(), SALT, 1024, 256);
			SecretKey tmp = factory.generateSecret(keySpec);
			SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
			mEncryptor = Cipher.getInstance(CIPHER_ALGORITHM);
			mEncryptor.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(IV));
			mDecryptor = Cipher.getInstance(CIPHER_ALGORITHM);
			mDecryptor.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(IV));
		} catch (GeneralSecurityException e) {
			throw new RuntimeException("Invalid environment", e);
		}

    }

	public DataOutputStream getSaveStream() {
		bo = new ByteArrayOutputStream();
		saveData = new DataOutputStream(bo);
		return saveData;
	}

	public void save(Context ctx, String fileName) throws Exception {

		try {
			bo = new ByteArrayOutputStream();

			byte[] crypted = mEncryptor.doFinal(bo.toByteArray());

	        DataOutputStream ds = new DataOutputStream(
	            new BufferedOutputStream(ctx.openFileOutput(fileName, Context.MODE_PRIVATE)));
//					new BufferedOutputStream(new FileOutputStream(fileName)));

			try {
	        	ds.write(crypted, 0, crypted.length);
				ds.flush();
	        } finally { ds.close(); }

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}

		saveData = null;
		bo = null;
	}

	/** ロード
	 * @throws Exception **/
	protected final DataInputStream load(Context ctx, String fileName) throws Exception {
		DataInputStream result = null;

		try {

	        DataInputStream ds = new DataInputStream(
	            new BufferedInputStream(ctx.openFileInput(fileName)));

	        try {
	        	ByteArrayOutputStream tmpbo = new ByteArrayOutputStream();
	    		DataOutputStream tmp = new DataOutputStream(tmpbo);

	    		byte[] buf = new byte[1024];

	    		int readlen;

	    		while (true) {
	    			readlen = ds.read(buf);
	    			if (readlen == -1) { break; }
	    			tmp.write(buf, 0, readlen);
	    		}

	    		byte[] decrypted = mDecryptor.doFinal(tmpbo.toByteArray());

	    		ByteArrayInputStream bi = new ByteArrayInputStream(decrypted);
	    		result = new DataInputStream(bi);

	        } finally { ds.close(); }

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}

		return result;
    }

}