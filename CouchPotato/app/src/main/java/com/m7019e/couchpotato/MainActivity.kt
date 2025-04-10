package com.m7019e.couchpotato

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.m7019e.couchpotato.ui.theme.CouchPotatoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CouchPotatoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val tabs = listOf(
        TabItem.Home,
        TabItem.Search,
        TabItem.Favorites
    )
    val selectedTab = remember { mutableStateOf(tabs[0]) }

    Scaffold(
        bottomBar = {
            BottomTabBar(
                tabs = tabs,
                selectedTab = selectedTab.value,
                onTabSelected = { selectedTab.value = it }
            )
        }
    ) { innerPadding ->
        // Display the selected tab content
        Surface(
            modifier = Modifier.padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            selectedTab.value.screen()
        }
    }
}

@Composable
fun BottomTabBar(
    tabs: List<TabItem>,
    selectedTab: TabItem,
    onTabSelected: (TabItem) -> Unit
) {
    NavigationBar {
        tabs.forEach { tab ->
            NavigationBarItem(
                icon = { Icon(tab.icon, contentDescription = tab.title) },
                label = { Text(tab.title) },
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) }
            )
        }
    }
}

sealed class TabItem(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val screen: @Composable () -> Unit) {
    object Home : TabItem("Home", Icons.Default.Home, { HomeScreen() })
    object Search : TabItem("Search", Icons.Default.Search, { SearchScreen() })
    object Favorites : TabItem("Favorites", Icons.Default.Favorite, { FavoritesScreen() })
}

@Composable
fun HomeScreen() {
    Text(
        text = "Home Screen",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun SearchScreen() {
    Text(
        text = "Search Screen",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun FavoritesScreen() {
    Text(
        text = "Favorites Screen",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.fillMaxSize()
    )
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    CouchPotatoTheme {
        MainScreen()
    }
}