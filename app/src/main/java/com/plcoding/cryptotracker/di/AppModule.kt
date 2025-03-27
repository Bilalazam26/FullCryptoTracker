package com.plcoding.cryptotracker.di

import android.content.Context
import androidx.paging.cachedIn
import androidx.room.Room
import com.plcoding.cryptotracker.core.data.networking.HttpClientFactory
import com.plcoding.cryptotracker.crypto.data.CoinRepository
import com.plcoding.cryptotracker.crypto.data.local.CoinDatabase
import com.plcoding.cryptotracker.crypto.data.remote.RemoteCoinDataSource
import com.plcoding.cryptotracker.crypto.domain.CoinDataSource
import com.plcoding.cryptotracker.crypto.presentaion.coin_list.CoinListViewModel
import io.ktor.client.engine.cio.CIO
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

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
        get<CoinRepository>().getCoinsPager()
    }
    viewModelOf(::CoinListViewModel)
}

