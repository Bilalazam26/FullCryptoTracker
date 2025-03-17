package com.plcoding.cryptotracker.crypto.presentaion.coin_list

import androidx.compose.runtime.Immutable
import com.plcoding.cryptotracker.crypto.presentaion.models.CoinUi

@Immutable //this class can never changes if it's changed a whole instance should be replaced
data class CoinListState(
    val isLoading: Boolean = false,
    val coins: List<CoinUi> = emptyList<CoinUi>(),
    val selectedCoinUi: CoinUi? = null
)
