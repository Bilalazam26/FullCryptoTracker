package com.plcoding.cryptotracker.crypto.presentaion.coin_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.plcoding.cryptotracker.crypto.presentaion.coin_list.components.CoinListItem
import com.plcoding.cryptotracker.crypto.presentaion.models.CoinUi

@Composable
fun CoinListScreen(
    coins: LazyPagingItems<CoinUi>,
    onAction: (CoinListAction) -> Unit,
    modifier: Modifier = Modifier
) {
    if (coins.loadState.refresh is LoadState.Loading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
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
