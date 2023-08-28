package com.apps2you.albaraka.utils.cryptography

import android.content.Context
import javax.crypto.Cipher

interface CryptographyManager {

    fun getInitializedCipherForEncryption(keyName: String): Cipher

    fun getInitializedCipherForDecryption(keyName: String, initializationVector: ByteArray): Cipher?

    fun encryptData(plaintext: String, cipher: Cipher): CiphertextWrapper

    fun decryptData(ciphertext: ByteArray, cipher: Cipher): String

    fun persistCiphertextWrapperToSharedPrefs(
            ciphertextWrapper: CiphertextWrapper,
            context: Context,
            filename: String,
            mode: Int,
            prefKey: String
    )

    fun clearCiphertextWrapperFromSharedPrefs(
            context: Context,
            filename: String,
            mode: Int,
            prefKey: String
    )

    fun getCiphertextWrapperFromSharedPrefs(
            context: Context,
            filename: String,
            mode: Int,
            prefKey: String
    ): CiphertextWrapper?

}