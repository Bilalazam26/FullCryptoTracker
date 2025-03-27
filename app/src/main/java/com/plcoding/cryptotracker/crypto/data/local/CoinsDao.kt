package com.plcoding.cryptotracker.crypto.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.plcoding.cryptotracker.crypto.data.local.entities.CoinEntity

@Dao
interface CoinsDao {
    @Query("SELECT * FROM coinentity")
    fun pagingSource(): PagingSource<Int, CoinEntity>

    @Upsert
    suspend fun upsertAllCoins(coins: List<CoinEntity>)

    @Query("DELETE FROM coinentity")
    suspend fun clear()
}