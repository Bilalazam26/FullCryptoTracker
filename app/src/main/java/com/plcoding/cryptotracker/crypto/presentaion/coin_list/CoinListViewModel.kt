package com.plcoding.cryptotracker.crypto.presentaion.coin_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.plcoding.cryptotracker.core.domain.util.onError
import com.plcoding.cryptotracker.core.domain.util.onSuccess
import com.plcoding.cryptotracker.crypto.data.CoinRepository
import com.plcoding.cryptotracker.crypto.data.mappers.toCoin
import com.plcoding.cryptotracker.crypto.presentaion.coin_details.chart.DataPoint
import com.plcoding.cryptotracker.crypto.presentaion.models.CoinUi
import com.plcoding.cryptotracker.crypto.presentaion.models.toCoinUi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


class CoinListViewModel(
    private val repository: CoinRepository
): ViewModel() {
    var coinPagingFlow = repository.getCoinsPager()
        .flow
        .map { pagingData ->
            pagingData.map {
                it.toCoin().toCoinUi()
            }
        }
        .cachedIn(viewModelScope)

    private val _selectedCoinUiState = MutableStateFlow<CoinUi?>(null)
    val selectedCoinUiState = _selectedCoinUiState.asStateFlow()

    //chanel is one time
    private val _events = Channel<CoinListEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: CoinListAction) {
        when (action) {
            is CoinListAction.OnCoinListItemClick -> {
                selectCoin(action.coinUi)
            }
        }
    }


    private fun selectCoin(coinUi: CoinUi) {
        _selectedCoinUiState.update {
            coinUi
        }
        viewModelScope.launch {
            repository.getCoinHistory(
                coinId = coinUi.id,
                start = ZonedDateTime.now().minusDays(5),
                end = ZonedDateTime.now()
            )
                .onSuccess { history ->
                    val dataPoints = history
                        .sortedBy { it.dateTime }
                        .map {
                            DataPoint(
                                x = it.dateTime.hour.toFloat(),
                                y = it.priceUsd.toFloat(),
                                xLabel = DateTimeFormatter
                                    .ofPattern("ha\nM/d")
                                    .format(it.dateTime)
                            )
                        }
                    _selectedCoinUiState.update {
                        it?.copy(
                            coinPriceHistory = dataPoints
                        )
                    }

                }
                .onError { error ->
                    _events.send(CoinListEvent.Error(error))
                }
        }
    }
}