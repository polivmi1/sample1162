package com.example.sample1162

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.example.sample1162.destinations.Screen1Destination
import com.example.sample1162.destinations.Screen2Destination
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.NestedNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberAnimatedNavController()
                AppScreen(navController = navController,
                        viewModel = viewModel(),)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
@Composable
fun AppScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: AppScreenViewModel,
) {
    val scaffoldState = rememberScaffoldState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val state by viewModel.state.collectAsState()
    Scaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        content = { scaffoldPadding ->
            Box(Modifier.padding(scaffoldPadding)) {
                val navHostEngine = rememberAnimatedNavHostEngine(
                    navHostContentAlignment = Alignment.TopCenter,
                    rootDefaultAnimations = RootNavGraphDefaultAnimations.ACCOMPANIST_FADING,
                    defaultAnimationsForNestedNavGraph = mapOf(
                        NavGraphs.root to NestedNavGraphDefaultAnimations(
                            enterTransition = { slideInHorizontally() },
                            exitTransition = { slideOutHorizontally() }
                        ),
                    )
                )
                val scope = rememberCoroutineScope()
                ModalDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        Column {
                            Button(onClick = {
                                viewModel.event(AppScreenViewModel.Event.DrawerItemClicked("screen1"))
                            }) {
                                Text("Screen 1")
                            }
                            Button(onClick = {
                                viewModel.event(AppScreenViewModel.Event.DrawerItemClicked("screen2"))
                            }) {
                                Text("Screen 2")
                            }
                        }
                    }
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        DestinationsNavHost(navGraph = NavGraphs.root, navController = navController, engine = navHostEngine)
                        {
                            composable(Screen1Destination) {
                                Screen1(
                                    navigator = destinationsNavigator,
                                )
                            }
                            composable(Screen2Destination) {
                                Screen2(
                                    navigator = destinationsNavigator,
                                )
                            }
                        }
                    }
                }
            }
        })

    if (state.effects.isNotEmpty()) {
        val effect = remember(state) { state.effects.first() }
        when (effect) {
            is AppScreenViewModel.Effect.Navigate -> {
                navController.navigate(effect.route) {
                    popUpTo(NavGraphs.root.route)
                    launchSingleTop = true
                }
            }
        }}
}

@RootNavGraph(start = true)
@com.ramcosta.composedestinations.annotation.Destination(
    route = "screen1",
)
@Composable
fun Screen1(
    navigator: DestinationsNavigator
) {
    Row(Modifier.fillMaxSize()) {
        Button(onClick = { navigator.navigateUp() }) {
            Text("Back")
        }
        Button(onClick = {
            navigator.navigate(Screen2Destination()) {
                launchSingleTop = true
            }
        }) {
            Text("Screen 2")
        }
    }
}

@com.ramcosta.composedestinations.annotation.Destination(
    route = "screen2",
)
@Composable
fun Screen2(
    navigator: DestinationsNavigator
) {
    Row(Modifier.fillMaxSize()) {
        Button(onClick = { navigator.navigateUp() }) {
            Text("Back")
        }
        Button(onClick = {
            navigator.navigate("screen1") {
                popUpTo(NavGraphs.root.route)
                launchSingleTop = true
            }
        }) {
            Text("Screen 1")
        }
    }
}