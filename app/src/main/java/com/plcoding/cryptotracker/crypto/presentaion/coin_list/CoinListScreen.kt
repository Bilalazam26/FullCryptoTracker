package com.plcoding.cryptotracker.crypto.presentaion.coin_list

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.plcoding.cryptotracker.crypto.presentaion.coin_list.components.CoinListItem
import com.plcoding.cryptotracker.crypto.presentaion.coin_list.components.previewCoin
import com.plcoding.cryptotracker.ui.theme.CryptoTrackerTheme
import kotlinx.coroutines.flow.Flow
import com.plcoding.cryptotracker.core.presentaion.util.toString
import com.plcoding.cryptotracker.crypto.data.local.entities.CoinEntity
import com.plcoding.cryptotracker.crypto.data.mappers.toCoin
import com.plcoding.cryptotracker.crypto.domain.Coin
import com.plcoding.cryptotracker.crypto.presentaion.models.toCoinUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.withContext

@Composable
fun CoinListScreen(
    state: CoinListState,
    onAction: (CoinListAction) -> Unit,
    coins: LazyPagingItems<CoinEntity>,
    modifier: Modifier = Modifier
) {
    if (state.isLoading) {
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
                coins[index]?.let { coinEntity ->
                    val coinUi = coinEntity.toCoin().toCoinUi()
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
