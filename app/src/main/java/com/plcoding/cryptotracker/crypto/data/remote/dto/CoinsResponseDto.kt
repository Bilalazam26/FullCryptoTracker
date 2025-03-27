package com.plcoding.cryptotracker.crypto.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CoinsResponseDto(
    val data: List<CoinDto>

)