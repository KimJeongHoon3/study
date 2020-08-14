package com.biz.bgmsgw.util.cipher.aes;

public class AESKey {
    private byte[] key;
    private byte[] ivKey=new byte[16];

    public AESKey(byte[] symKey,int keySize){
        if(keySize==16){
            key=new byte[16];
            System.arraycopy(symKey, 0, this.key, 0, 16);
        }else{
            key=new byte[32];
            System.arraycopy(symKey, 0, this.key, 0, 32);
        }

        if (symKey.length > 16) {
            System.arraycopy(symKey, 16, this.ivKey, 0, 16);
        }
    }

    public byte[] getKey() {
        return key;
    }

    public byte[] getIvKey() {
        return ivKey;
    }
}
