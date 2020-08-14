package com.biz.bgmsgw.util.cipher.aes;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESCrypto {
    private static final String CipherTransformation = "AES/CBC/PKCS5Padding";
    private static final String Algorithm = "AES";

    private static byte[] doCipher(AESKey key,int mode,byte[] data){
        try {
            Cipher cipher = Cipher.getInstance(CipherTransformation);
            SecretKeySpec secretKeySpec=new SecretKeySpec(key.getKey(),Algorithm);
            IvParameterSpec ivParameterSpec=new IvParameterSpec(key.getIvKey());
            cipher.init(mode,secretKeySpec,ivParameterSpec);
            return cipher.doFinal(data);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] decrypt(AESKey key,byte[] data){
        return doCipher(key,Cipher.DECRYPT_MODE,data);
    }

    public static byte[] encrypt(AESKey key, byte[] data){
        return doCipher(key,Cipher.ENCRYPT_MODE,data);
    }
}
