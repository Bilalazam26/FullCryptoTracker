package com.plcoding.cryptotracker.crypto.mappers

import com.plcoding.cryptotracker.crypto.data.local.entities.CoinEntity
import com.plcoding.cryptotracker.crypto.domain.Coin

fun Coin.toCoinEntity(): CoinEntity {
    return CoinEntity(
        id = id,
        rank = rank,
        name = name,
        symbol = symbol,
        marketCapUsd = marketCapUsd,
        priceUsd = priceUsd,
        changePercent24Hr = changePercent24Hr
    )
}