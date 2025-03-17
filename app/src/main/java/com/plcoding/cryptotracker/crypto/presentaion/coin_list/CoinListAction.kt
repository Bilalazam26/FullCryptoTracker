package com.plcoding.cryptotracker.crypto.presentaion.coin_list

import com.plcoding.cryptotracker.crypto.presentaion.models.CoinUi

sealed interface CoinListAction {
    data class OnCoinListItemClick(val coinUi: CoinUi): CoinListAction
    data object OnRefresh: CoinListAction
}