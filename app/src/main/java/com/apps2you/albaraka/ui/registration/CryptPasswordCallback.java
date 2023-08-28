package com.apps2you.albaraka.ui.registration;

public interface CryptPasswordCallback {

    void encryptSuccess();

    void decryptSuccess(String password);

    void keyInvalidated();
}
