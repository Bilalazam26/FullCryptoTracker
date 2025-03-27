package com.plcoding.cryptotracker.crypto.presentaion.coin_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.plcoding.cryptotracker.core.domain.util.onError
import com.plcoding.cryptotracker.core.domain.util.onSuccess
import com.plcoding.cryptotracker.crypto.data.CoinRepository
import com.plcoding.cryptotracker.crypto.domain.CoinDataSource
import com.plcoding.cryptotracker.crypto.presentaion.coin_details.chart.DataPoint
import com.plcoding.cryptotracker.crypto.presentaion.models.CoinUi
import com.plcoding.cryptotracker.crypto.presentaion.models.toCoinUi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


class CoinListViewModel(
    private val repository: CoinRepository
): ViewModel() {
    val coinPagingFlow = repository.getCoinsPager()
        .flow
        .cachedIn(viewModelScope)

    private val _state = MutableStateFlow(CoinListState())
    val state = _state.asStateFlow()

    //chanel is one time
    private val _events = Channel<CoinListEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: CoinListAction) {
        when (action) {
            is CoinListAction.OnCoinListItemClick -> {
                selectCoin(action.coinUi)
            }
            CoinListAction.OnRefresh -> {
                refreshCoins()

            }
        }
    }

    private fun refreshCoins() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
        }
    }

    private fun selectCoin(coinUi: CoinUi) {
        _state.update { it.copy(
            selectedCoinUi = coinUi
        ) }
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
                    _state.update {
                        it.copy(
                            selectedCoinUi = it.selectedCoinUi?.copy(
                                coinPriceHistory = dataPoints
                            )
                        )
                    }

                }
                .onError { error ->
                    _events.send(CoinListEvent.Error(error))
                }
        }
    }
}