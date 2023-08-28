package com.apps2you.albaraka.utils.cryptography

import java.security.GeneralSecurityException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.xor
import kotlin.math.ceil
import kotlin.math.pow


/**
 * An implementation of the Password-Based Key Derivation Function as specified
 * in RFC 2898.
 *
 * @author  Will Glozer
 */
object PBKDF {
    /**
     * Implementation of PBKDF2 (RFC2898).
     *
     * @param   alg     HMAC algorithm to use.
     * @param   P       Password.
     * @param   S       Salt.
     * @param   c       Iteration count.
     * @param   dkLen   Intended length, in octets, of the derived key.
     *
     * @return  The derived key.
     *
     * @throws  GeneralSecurityException
     */
    @Throws(GeneralSecurityException::class)
    @JvmStatic
    fun pbkdf2(alg: String, P: ByteArray, S: ByteArray, c: Int, dkLen: Int): ByteArray {
        val mac = Mac.getInstance(alg)
        mac.init(SecretKeySpec(P, alg))
        val DK = ByteArray(dkLen)
        pbkdf2(mac, S, c, DK, dkLen)
        return DK
    }

    /**
     * Implementation of PBKDF2 (RFC2898).
     *
     * @param   mac     Pre-initialized [Mac] instance to use.
     * @param   S       Salt.
     * @param   c       Iteration count.
     * @param   DK      Byte array that derived key will be placed in.
     * @param   dkLen   Intended length, in octets, of the derived key.
     *
     * @throws  GeneralSecurityException
     */
    @Throws(GeneralSecurityException::class)
    @JvmStatic
    fun pbkdf2(mac: Mac, S: ByteArray, c: Int, DK: ByteArray, dkLen: Int) {
        val hLen = mac.macLength
        if (dkLen > (2.0.pow(32.0) - 1) * hLen) {
            throw GeneralSecurityException("Requested key length too long")
        }
        val U = ByteArray(hLen)
        val T = ByteArray(hLen)
        val block1 = ByteArray(S.size + 4)
        val l = ceil(dkLen.toDouble() / hLen).toInt()
        val r = dkLen - (l - 1) * hLen
        System.arraycopy(S, 0, block1, 0, S.size)
        for (i in 1..l) {
            block1[S.size + 0] = (i shr 24 and 0xff).toByte()
            block1[S.size + 1] = (i shr 16 and 0xff).toByte()
            block1[S.size + 2] = (i shr 8 and 0xff).toByte()
            block1[S.size + 3] = (i shr 0 and 0xff).toByte()
            mac.update(block1)
            mac.doFinal(U, 0)
            System.arraycopy(U, 0, T, 0, hLen)
            for (j in 1 until c) {
                mac.update(U)
                mac.doFinal(U, 0)
                for (k in 0 until hLen) {
                    T[k] = T[k] xor U[k]
                }
            }
            System.arraycopy(T, 0, DK, (i - 1) * hLen, if (i == l) r else hLen)
        }
    }
}