package com.rmkrings.helper;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;

import com.rmkrings.main.pius_app.PiusApplication;

class Crypto {
    private static final String RSA_MODE =  "RSA/ECB/PKCS1Padding";
    private static final String AES_MODE = "AES/ECB/PKCS7Padding";
    private static final String KEY_ALIAS = "com.rmkrings.pius_app";
    private static final String AndroidKeyStore = "AndroidKeyStore";

    private KeyStore keyStore;

    Crypto() throws Exception
    {
        keyStore = KeyStore.getInstance(AndroidKeyStore);
        keyStore.load(null);

        // Generate the RSA key pairs
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            // Generate a key pair for encryption
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            end.add(Calendar.YEAR, 30);
            KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(PiusApplication.getAppContext())
                    .setAlias(KEY_ALIAS)
                    .setSubject(new X500Principal("CN=" + KEY_ALIAS))
                    .setSerialNumber(BigInteger.TEN)
                    .setStartDate(start.getTime())
                    .setEndDate(end.getTime())
                    .build();
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", AndroidKeyStore);
            kpg.initialize(spec);
            kpg.generateKeyPair();
        }
    }

    private String getRSAProvider() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) { // below android m
            return "AndroidOpenSSL"; // error in android 6: InvalidKeyException: Need RSA private or public key
        } else { // android m and above
            return "AndroidKeyStoreBCWorkaround"; // error in android 5: NoSuchProviderException: Provider not available: AndroidKeyStoreBCWorkaround
        }
    }

    private byte[] rsaEncrypt(byte[] secret) throws Exception {
        PublicKey publicKey = keyStore.getCertificate(KEY_ALIAS).getPublicKey();

        // Encrypt the text
        Cipher inputCipher = Cipher.getInstance(RSA_MODE, getRSAProvider());
        inputCipher.init(Cipher.ENCRYPT_MODE, publicKey);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, inputCipher);
        cipherOutputStream.write(secret);
        cipherOutputStream.close();

        return outputStream.toByteArray();
    }

    private byte[] rsaDecrypt(byte[] encrypted) throws Exception {
        PrivateKey privateKey = (PrivateKey)keyStore.getKey(KEY_ALIAS, null);

        Cipher output = Cipher.getInstance(RSA_MODE, "AndroidKeyStoreBCWorkaround");
        output.init(Cipher.DECRYPT_MODE, privateKey);

        CipherInputStream cipherInputStream = new CipherInputStream(
                new ByteArrayInputStream(encrypted), output);
        ArrayList<Byte> values = new ArrayList<>();

        int nextByte;
        while ((nextByte = cipherInputStream.read()) != -1) {
            values.add((byte)nextByte);
        }

        byte[] bytes = new byte[values.size()];
        for(int i = 0; i < bytes.length; i++) {
            bytes[i] = values.get(i);
        }

        return bytes;
    }

    private void generateAESKey() throws Exception {
        SharedPreferences sharedPreferences = AppDefaults.getSharedPreferences();

        String enryptedKeyB64 = sharedPreferences.getString("EncryptedKey", null);
        if (enryptedKeyB64 == null) {
            byte[] key = new byte[16];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(key);
            byte[] encryptedKey = rsaEncrypt(key);
            enryptedKeyB64 = Base64.encodeToString(encryptedKey, Base64.DEFAULT);
            SharedPreferences.Editor edit = AppDefaults.getEdit();
            edit.putString("EncryptedKey", enryptedKeyB64);
            edit.commit();
        }
    }

    private Key getSecretKey() throws Exception{
        generateAESKey();

        SharedPreferences sharedPreferences = AppDefaults.getSharedPreferences();
        String enryptedKeyB64 = sharedPreferences.getString("EncryptedKey", null);

        // need to check null, omitted here

        byte[] encryptedKey = Base64.decode(enryptedKeyB64, Base64.DEFAULT);
        byte[] key = rsaDecrypt(encryptedKey);
        return new SecretKeySpec(key, "AES");
    }

    String encrypt(String input) throws Exception {
        @SuppressLint("GetInstance") Cipher c = Cipher.getInstance(AES_MODE);
        c.init(Cipher.ENCRYPT_MODE, getSecretKey());
        byte[] encodedBytes = c.doFinal(input.getBytes());
        return  Base64.encodeToString(encodedBytes, Base64.DEFAULT);
    }


    String decrypt(String input) throws Exception {
        @SuppressLint("GetInstance") Cipher c = Cipher.getInstance(AES_MODE);
        c.init(Cipher.DECRYPT_MODE, getSecretKey());

        byte[] encrypted = Base64.decode(input, Base64.DEFAULT);
        byte[] decodedBytes = c.doFinal(encrypted);

        return new String(decodedBytes);
    }
}
