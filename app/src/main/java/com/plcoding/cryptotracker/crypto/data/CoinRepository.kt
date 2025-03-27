package com.plcoding.cryptotracker.crypto.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.plcoding.cryptotracker.core.domain.util.NetworkError
import com.plcoding.cryptotracker.core.domain.util.Result
import com.plcoding.cryptotracker.crypto.data.local.CoinDatabase
import com.plcoding.cryptotracker.crypto.data.local.entities.CoinEntity
import com.plcoding.cryptotracker.crypto.data.paging.CoinsRemoteMediator
import com.plcoding.cryptotracker.crypto.data.remote.RemoteCoinDataSource
import com.plcoding.cryptotracker.crypto.domain.CoinPrice
import java.time.ZonedDateTime

class CoinRepository(
    private val remoteCoinDataSource: RemoteCoinDataSource,
    private val pager: Pager<Int, CoinEntity>
) {


    fun getCoinsPager(): Pager<Int, CoinEntity> {
        return pager
    }

    suspend fun getCoinHistory(
        coinId: String,
        start: ZonedDateTime,
        end: ZonedDateTime
    ): Result<List<CoinPrice>, NetworkError> {
        return remoteCoinDataSource.getHistory(coinId, start, end)
    }
}