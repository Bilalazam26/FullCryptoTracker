package com.plcoding.cryptotracker.di

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.room.Room
import com.plcoding.cryptotracker.core.data.networking.HttpClientFactory
import com.plcoding.cryptotracker.crypto.data.CoinRepository
import com.plcoding.cryptotracker.crypto.data.local.CoinDatabase
import com.plcoding.cryptotracker.crypto.data.local.CoinsDao
import com.plcoding.cryptotracker.crypto.data.paging.CoinsRemoteMediator
import com.plcoding.cryptotracker.crypto.data.remote.RemoteCoinDataSource
import com.plcoding.cryptotracker.crypto.domain.CoinDataSource
import com.plcoding.cryptotracker.crypto.presentaion.coin_list.CoinListViewModel
import io.ktor.client.engine.cio.CIO
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

@OptIn(ExperimentalPagingApi::class)
val appModule = module {
    single { HttpClientFactory.create(CIO.create()) }//CIO -> Coroutine-based IO
    singleOf(::RemoteCoinDataSource).bind<CoinDataSource>()
    single {
        Room.databaseBuilder(
            get<Context>(),
            CoinDatabase::class.java,
            "coins.db"
        ).build()
    }
    single { get<CoinDatabase>().coinsDao() }
    single { get<CoinDatabase>().coinRemoteKeysDao() }
    single { CoinRepository(get(), get()) }
    single {
        Pager(
            config = PagingConfig(
                pageSize = 20, // Adjust based on your API
                prefetchDistance = 1,
                enablePlaceholders = false
            ),
            remoteMediator = CoinsRemoteMediator(
                remoteCoinDataSource = get(),
                coinsDatabase = get()
            ),
            pagingSourceFactory = { get<CoinsDao>().pagingSource() }
        )
    }
    viewModelOf(::CoinListViewModel)
}

