package com.plcoding.cryptotracker.crypto.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.plcoding.cryptotracker.crypto.data.local.entities.CoinEntity
import com.plcoding.cryptotracker.crypto.data.local.entities.CoinRemoteKeysEntity

@Database(
    version = 1,
    entities = [CoinEntity::class, CoinRemoteKeysEntity::class]
)
abstract class CoinDatabase: RoomDatabase() {
    abstract fun coinsDao(): CoinsDao
    abstract fun coinRemoteKeysDao(): CoinRemoteKeysDao
}