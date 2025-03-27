package com.plcoding.cryptotracker.crypto.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.plcoding.cryptotracker.crypto.data.local.entities.CoinRemoteKeysEntity

@Dao
interface CoinRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRemoteKeys(remoteKeys: List<CoinRemoteKeysEntity>)

    @Query("SELECT * FROM coinremotekeysentity WHERE id=:id")
    suspend fun getRemoteKeys(id: String): CoinRemoteKeysEntity

    @Query("DELETE FROM coinremotekeysentity")
    suspend fun clear()
}