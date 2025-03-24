package com.plcoding.cryptotracker.core.navigation

import android.widget.Toast
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.plcoding.cryptotracker.core.presentaion.util.ObserveAsEvent
import com.plcoding.cryptotracker.core.presentaion.util.toString
import com.plcoding.cryptotracker.crypto.presentaion.coin_details.CoinDetailsScreen
import com.plcoding.cryptotracker.crypto.presentaion.coin_list.CoinListAction
import com.plcoding.cryptotracker.crypto.presentaion.coin_list.CoinListEvent
import com.plcoding.cryptotracker.crypto.presentaion.coin_list.CoinListScreen
import com.plcoding.cryptotracker.crypto.presentaion.coin_list.CoinListViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AdaptiveCoinListDetailPane(
    modifier: Modifier = Modifier,
    coinsListVM: CoinListViewModel = koinViewModel()
) {
    val state by coinsListVM.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    ObserveAsEvent(events = coinsListVM.events) { event ->
        when(event) {
            is CoinListEvent.Error -> {
                Toast.makeText(
                    context,
                    event.error.toString(context),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    val navigator = rememberListDetailPaneScaffoldNavigator<Any>()
    NavigableListDetailPaneScaffold(
        navigator = navigator,
        listPane = {
            AnimatedPane {
                CoinListScreen(
                    state = state,
                    onAction = { action ->
                        coinsListVM.onAction(action)
                        if (action is CoinListAction.OnCoinListItemClick) {
                            navigator.navigateTo(
                                pane = ListDetailPaneScaffoldRole.Detail
                            )
                        }
                    }
                )
            }
        },
        detailPane = {
            AnimatedPane {
                CoinDetailsScreen(
                    state = state
                )
            }
        },
        modifier = modifier
    )
}