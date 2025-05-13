package com.solanasniper.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Модель аккаунта Solana для безопасного хранения ключей
 * @property publicKey Публичный ключ кошелька (Base58-строка)
 * @property keyPair Зашифрованная пара ключей в формате ByteArray
 */
@Parcelize
@JvmInline
value class SolanaAccount(
    val publicKey: String,
    private val keyPair: ByteArray
) : Parcelable {

    fun getKeyPair(): ByteArray = keyPair.copyOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as SolanaAccount
        return publicKey == other.publicKey
    }

    override fun hashCode(): Int = publicKey.hashCode()

    override fun toString(): String = "SolanaAccount(publicKey='${publicKey.take(6)}...')"

    companion object {
        /**
         * Создает пустой аккаунт для инициализации
         */
        fun empty() = SolanaAccount(
            publicKey = "",
            keyPair = ByteArray(0)
    }
}