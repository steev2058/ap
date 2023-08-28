package com.apps2you.albaraka.utils.cryptography

data class CiphertextWrapper(val ciphertext: ByteArray, val initializationVector: ByteArray)