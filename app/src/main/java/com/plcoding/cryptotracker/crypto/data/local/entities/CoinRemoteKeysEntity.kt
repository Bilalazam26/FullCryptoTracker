package com.plcoding.cryptotracker.crypto.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CoinRemoteKeysEntity (
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val prevPageKey: Int?,
    val nextPageKey: Int?
)
