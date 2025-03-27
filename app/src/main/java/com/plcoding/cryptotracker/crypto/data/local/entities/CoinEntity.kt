package com.plcoding.cryptotracker.crypto.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CoinEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val rank: Int,
    val name: String,
    val symbol: String,
    val marketCapUsd: Double,
    val priceUsd: Double,
    val changePercent24Hr: Double
)
