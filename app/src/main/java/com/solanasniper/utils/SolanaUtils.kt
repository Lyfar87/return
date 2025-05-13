package com.solanasniper.utils

import android.util.Base64
import com.solanasniper.domain.model.SolanaAccount
import java.security.KeyPairGenerator
import java.security.SecureRandom

class SolanaUtils {

    fun generateNewAccount(): SolanaAccount {
        val keyGen = KeyPairGenerator.getInstance("Ed25519")
        val random = SecureRandom.getInstanceStrong()
        keyGen.initialize(256, random)

        val keyPair = keyGen.generateKeyPair()
        val publicKey = Base64.encodeToString(keyPair.public.encoded, Base64.NO_WRAP)

        return SolanaAccount(
            publicKey = publicKey,
            keyPair = keyPair.encoded
        )
    }

    fun getBalance(publicKey: String): Long {
        // Заглушка: реальная реализация через RPC
        return (Math.random() * 1_000_000_000).toLong()
    }

    fun signTransaction(keyPair: ByteArray, txData: ByteArray): ByteArray {
        // Заглушка для подписи транзакции
        return txData // В реальности использовать ключи для подписи
    }

    fun validateAddress(address: String): Boolean {
        return address.matches("^[1-9A-HJ-NP-Za-km-z]{32,44}$".toRegex())
    }
}