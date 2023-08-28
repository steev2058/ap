package com.apps2you.albaraka

object Keys {

    init {
        System.loadLibrary("native-lib")
    }

    external fun encryptionKey(): String
}