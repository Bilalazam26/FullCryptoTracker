package com.plcoding.cryptotracker.crypto.presentaion.coin_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.plcoding.cryptotracker.crypto.presentaion.models.CoinUi
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.ui.unit.dp
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.plcoding.cryptotracker.crypto.presentaion.coin_list.components.CoinListItem


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinListScreen(
    coins: LazyPagingItems<CoinUi>,
    onAction: (CoinListAction) -> Unit,
    modifier: Modifier = Modifier
) {

    val isRefreshing = coins.loadState.refresh is LoadState.Loading

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { coins.refresh() }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                count = coins.itemCount, // Total items count
                key = coins.itemKey { it.id }, // Unique key for each item
                contentType = coins.itemContentType() // Optimized content type handling
            ) { index ->
                coins[index]?.let { coinUi ->
                    CoinListItem(
                        coinUi = coinUi,
                        onClick = {
                            onAction(CoinListAction.OnCoinListItemClick(coinUi))
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                    HorizontalDivider()
                }
            }

        }
    }
}
/*

@PreviewLightDark
@PreviewDynamicColors
@Composable
private fun PreviewCoinListScreen() {
    CryptoTrackerTheme {
        CoinListScreen(
            state = CoinListState(
                isLoading = false,
                coins = (1 .. 100).map { previewCoin.copy(id = it.toString()) }
            ),
            onAction = {},
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        )
    }
}*/
