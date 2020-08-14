package com.biz.bgmsgw.util.cipher.rsa;

import java.security.Key;

public class RSAKey {
    Key privateKey;
    Key publicKey;

    public RSAKey() {
    }

    public RSAKey(Key privateKey, Key publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public Key getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(Key privateKey) {
        this.privateKey = privateKey;
    }

    public Key getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(Key publicKey) {
        this.publicKey = publicKey;
    }
}
