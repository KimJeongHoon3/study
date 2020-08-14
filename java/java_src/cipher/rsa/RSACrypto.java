package com.biz.bgmsgw.util.cipher.rsa;

import javax.crypto.*;
import java.security.*;
import java.security.spec.*;

public class RSACrypto {
    //키 생성
    private static final String Algorithm = "RSA";

    private RSACrypto(){

    }

    public static RSAKey generateKey() throws Exception{
        KeyPairGenerator keyPairGenerator=KeyPairGenerator.getInstance(Algorithm);
        keyPairGenerator.initialize(2048);
        KeyPair keyPair=keyPairGenerator.genKeyPair();
        Key privateKey=keyPair.getPrivate();
        Key publickKey=keyPair.getPublic();

        KeyFactory fact = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec clsPublicKeySpec = fact.getKeySpec( publickKey, RSAPublicKeySpec.class);
        RSAPrivateKeySpec clsPrivateKeySpec = fact.getKeySpec( privateKey, RSAPrivateKeySpec.class);
        System.out.println( "public key modulus(" + clsPublicKeySpec.getModulus( ) + ") exponent(" + clsPublicKeySpec.getPublicExponent( ) + ")" );
        System.out.println( "private key modulus(" + clsPrivateKeySpec.getModulus( ) + ") exponent(" + clsPrivateKeySpec.getPrivateExponent( ) + ")" );

        return new RSAKey(keyPair.getPrivate(),keyPair.getPublic());
    }


    public static byte[] privateDecrypt(byte[] encPrivateKey,byte[] data) throws Exception{
        KeyFactory fac = KeyFactory.getInstance(Algorithm);
        PKCS8EncodedKeySpec PKCS8Spec = new PKCS8EncodedKeySpec(encPrivateKey);
        Key privateKey = fac.generatePrivate(PKCS8Spec);

        return doCipher(Cipher.DECRYPT_MODE,privateKey,data);
    }

    public static byte[] privateEncrypt(byte[] encPrivateKey,byte[] data) throws Exception{

        KeyFactory fac = KeyFactory.getInstance(Algorithm);
        PKCS8EncodedKeySpec PKCS8Spec = new PKCS8EncodedKeySpec(encPrivateKey);
        Key privateKey = fac.generatePrivate(PKCS8Spec);

        return doCipher(Cipher.ENCRYPT_MODE,privateKey,data);
    }

    public static byte[] publicDecrypt(byte[] encPublicKey,byte[] data) throws Exception{

        KeyFactory fac = KeyFactory.getInstance(Algorithm);
        X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(encPublicKey); // key 가 바로 byte 배열
        Key publicKey = fac.generatePublic(x509Spec);

        return doCipher(Cipher.DECRYPT_MODE,publicKey,data);
    }

    public static byte[] publicEncrypt(byte[] encPublicKey,byte[] data) throws Exception{
        KeyFactory fac = KeyFactory.getInstance(Algorithm);
        X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(encPublicKey); // key 가 바로 byte 배열
        Key publicKey = fac.generatePublic(x509Spec);

        return doCipher(Cipher.ENCRYPT_MODE,publicKey,data);
    }

    private static byte[] doCipher(int mode, Key key, byte[] data) throws Exception{
        Cipher cipher=Cipher.getInstance(Algorithm);
        cipher.init(mode,key);
        return cipher.doFinal(data);
    }

    public static Key getPrivateKey(byte[] encPrivateKey) throws Exception{
        KeyFactory fac = KeyFactory.getInstance(Algorithm);
        PKCS8EncodedKeySpec PKCS8Spec = new PKCS8EncodedKeySpec(encPrivateKey);
        return fac.generatePrivate(PKCS8Spec);
    }

    public static Key getPublicKey(byte[] encPublicKey) throws Exception{
        KeyFactory fac = KeyFactory.getInstance(Algorithm);
        X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(encPublicKey);
        return fac.generatePublic(x509Spec);
    }

}
