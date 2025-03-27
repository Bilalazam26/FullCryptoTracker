package com.plcoding.cryptotracker.crypto.data.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.plcoding.cryptotracker.core.domain.util.Result
import com.plcoding.cryptotracker.core.domain.util.onError
import com.plcoding.cryptotracker.core.domain.util.onSuccess
import com.plcoding.cryptotracker.crypto.data.local.CoinDatabase
import com.plcoding.cryptotracker.crypto.data.local.entities.CoinEntity
import com.plcoding.cryptotracker.crypto.data.local.entities.CoinRemoteKeysEntity
import com.plcoding.cryptotracker.crypto.data.remote.RemoteCoinDataSource
import com.plcoding.cryptotracker.crypto.mappers.toCoinEntity
@OptIn(ExperimentalPagingApi::class)
class CoinsRemoteMediator(
    private val remoteCoinDataSource: RemoteCoinDataSource,
    private val coinsDatabase: CoinDatabase
): RemoteMediator<Int, CoinEntity>() {
    private val coinsDao = coinsDatabase.coinsDao()
    private val remoteKeysDao = coinsDatabase.coinRemoteKeysDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CoinEntity>
    ): MediatorResult {
        Log.d("CoinsRemoteMediator", "load() called with LoadType: $loadType")
        return try {
            val page = when(loadType) {
                LoadType.REFRESH -> {

                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextPageKey?.minus(1) ?: 1

                }
                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    remoteKeys?.prevPageKey
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = remoteKeys != null
                        )
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    remoteKeys?.nextPageKey
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = remoteKeys != null
                        )
                }
            }
            Log.d("CoinsRemoteMediator", "Fetching page: $page")
            val result = remoteCoinDataSource.getCoins(page = page, pageSize = state.config.pageSize)
            result.onSuccess { coins ->
                coinsDatabase.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        remoteKeysDao.clear()
                        coinsDao.clear()
                    }
                    val endOfPagination = coins.isEmpty()  // Stop if API returns no more data
                    val keys = coins.map {
                        CoinRemoteKeysEntity(
                            id = it.id,
                            prevPageKey = if (page == 1) null else page - 1,
                            nextPageKey = if (endOfPagination) null else page + 1
                        )
                    }
                    remoteKeysDao.insertAllRemoteKeys(keys)
                    coinsDao.upsertAllCoins(coins.map { it.toCoinEntity() })
                }
            }.onError {
                Log.e("CoinsRemoteMediator", "Error fetching coins: $it")
                return MediatorResult.Error(Exception(it.toString()))
            }
            MediatorResult.Success(endOfPaginationReached = result is Result.Success && result.data.isEmpty())
        } catch (e: Exception) {
            Log.e("CoinsRemoteMediator", "load() failed", e)
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, CoinEntity>
    ): CoinRemoteKeysEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                coinsDatabase.coinRemoteKeysDao().getRemoteKeys(id = id)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, CoinEntity>
    ): CoinRemoteKeysEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { coin ->
                coinsDatabase.coinRemoteKeysDao().getRemoteKeys(id = coin.id)
            }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, CoinEntity>
    ): CoinRemoteKeysEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { coin ->
                coinsDatabase.coinRemoteKeysDao().getRemoteKeys(id = coin.id)
            }
    }
}