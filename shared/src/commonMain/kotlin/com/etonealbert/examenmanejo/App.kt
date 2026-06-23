package com.etonealbert.examenmanejo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.etonealbert.examenmanejo.core.design.ExamenManejoTheme
import com.etonealbert.examenmanejo.core.navigation.AppNavigator
import com.etonealbert.examenmanejo.core.navigation.AppRoute
import com.etonealbert.examenmanejo.core.navigation.RootCoordinator
import com.etonealbert.examenmanejo.feature.exam.ExamScreen
import com.etonealbert.examenmanejo.feature.exam.ExamUiEffect
import com.etonealbert.examenmanejo.feature.exam.ExamViewModel
import com.etonealbert.examenmanejo.feature.history.HistoryScreen
import com.etonealbert.examenmanejo.feature.history.HistoryUiEffect
import com.etonealbert.examenmanejo.feature.history.HistoryViewModel
import com.etonealbert.examenmanejo.feature.home.HomeScreen
import com.etonealbert.examenmanejo.feature.home.HomeUiEffect
import com.etonealbert.examenmanejo.feature.home.HomeViewModel
import com.etonealbert.examenmanejo.feature.onboarding.OnboardingScreen
import com.etonealbert.examenmanejo.feature.onboarding.OnboardingUiEffect
import com.etonealbert.examenmanejo.feature.onboarding.OnboardingViewModel
import com.etonealbert.examenmanejo.feature.result.ResultScreen
import com.etonealbert.examenmanejo.feature.result.ResultUiEffect
import com.etonealbert.examenmanejo.feature.result.ResultViewModel
import com.etonealbert.examenmanejo.feature.review.ReviewScreen
import com.etonealbert.examenmanejo.feature.review.ReviewUiEffect
import com.etonealbert.examenmanejo.feature.review.ReviewViewModel
import com.etonealbert.examenmanejo.feature.settings.SettingsScreen
import com.etonealbert.examenmanejo.feature.settings.SettingsUiEffect
import com.etonealbert.examenmanejo.feature.settings.SettingsViewModel
import com.etonealbert.examenmanejo.feature.study.StudyScreen
import com.etonealbert.examenmanejo.feature.study.StudyUiEffect
import com.etonealbert.examenmanejo.feature.study.StudyViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun App() {
    val navigator = koinInject<AppNavigator>()
    val rootCoordinator = koinInject<RootCoordinator>()
    val route by navigator.currentRoute.collectAsState()

    LaunchedEffect(rootCoordinator) {
        rootCoordinator.start()
    }

    ExamenManejoTheme {
        Surface(Modifier.fillMaxSize()) {
            Box(Modifier.safeContentPadding().fillMaxSize()) {
                AppRouteHost(route = route, navigator = navigator)
            }
        }
    }
}

@Composable
private fun AppRouteHost(
    route: AppRoute,
    navigator: AppNavigator,
) {
    val routeViewModelStoreOwner = rememberRouteViewModelStoreOwner(route)

    when (route) {
        AppRoute.Onboarding -> {
            val viewModel = koinViewModel<OnboardingViewModel>(viewModelStoreOwner = routeViewModelStoreOwner)
            val state by viewModel.uiState.collectAsState()
            LaunchedEffect(viewModel) {
                viewModel.effects.collect { effect ->
                    when (effect) {
                        is OnboardingUiEffect.Navigate -> navigator.replace(effect.route)
                    }
                }
            }
            OnboardingScreen(state = state, onEvent = viewModel::onEvent)
        }
        AppRoute.Home -> {
            val viewModel = koinViewModel<HomeViewModel>(viewModelStoreOwner = routeViewModelStoreOwner)
            val state by viewModel.uiState.collectAsState()
            LaunchedEffect(viewModel) {
                viewModel.effects.collect { effect ->
                    when (effect) {
                        is HomeUiEffect.Navigate -> navigator.navigate(effect.route)
                    }
                }
            }
            HomeScreen(state = state, onEvent = viewModel::onEvent)
        }
        is AppRoute.Study -> {
            val viewModel = koinViewModel<StudyViewModel>(
                viewModelStoreOwner = routeViewModelStoreOwner,
                parameters = { parametersOf(route.licenseClassId) },
            )
            val state by viewModel.uiState.collectAsState()
            LaunchedEffect(viewModel) {
                viewModel.effects.collect { effect ->
                    when (effect) {
                        is StudyUiEffect.Navigate -> navigator.navigate(effect.route)
                    }
                }
            }
            StudyScreen(state = state, onEvent = viewModel::onEvent)
        }
        is AppRoute.Exam -> {
            val viewModel = koinViewModel<ExamViewModel>(
                viewModelStoreOwner = routeViewModelStoreOwner,
                parameters = { parametersOf(route.licenseClassId) },
            )
            val state by viewModel.uiState.collectAsState()
            LaunchedEffect(viewModel) {
                viewModel.effects.collect { effect ->
                    when (effect) {
                        is ExamUiEffect.Navigate -> navigator.navigate(effect.route)
                    }
                }
            }
            ExamScreen(state = state, onEvent = viewModel::onEvent)
        }
        is AppRoute.Result -> {
            val viewModel = koinViewModel<ResultViewModel>(
                viewModelStoreOwner = routeViewModelStoreOwner,
                parameters = { parametersOf(route.examSessionId) },
            )
            val state by viewModel.uiState.collectAsState()
            LaunchedEffect(viewModel) {
                viewModel.effects.collect { effect ->
                    when (effect) {
                        is ResultUiEffect.Navigate -> navigator.navigate(effect.route)
                    }
                }
            }
            ResultScreen(state = state, onEvent = viewModel::onEvent)
        }
        is AppRoute.Review -> {
            val viewModel = koinViewModel<ReviewViewModel>(
                viewModelStoreOwner = routeViewModelStoreOwner,
                parameters = { parametersOf(route.examSessionId) },
            )
            val state by viewModel.uiState.collectAsState()
            LaunchedEffect(viewModel) {
                viewModel.effects.collect { effect ->
                    when (effect) {
                        is ReviewUiEffect.Navigate -> navigator.navigate(effect.route)
                    }
                }
            }
            ReviewScreen(state = state, onEvent = viewModel::onEvent)
        }
        AppRoute.History -> {
            val viewModel = koinViewModel<HistoryViewModel>(viewModelStoreOwner = routeViewModelStoreOwner)
            val state by viewModel.uiState.collectAsState()
            LaunchedEffect(viewModel) {
                viewModel.effects.collect { effect ->
                    when (effect) {
                        is HistoryUiEffect.Navigate -> navigator.navigate(effect.route)
                    }
                }
            }
            HistoryScreen(state = state, onEvent = viewModel::onEvent)
        }
        AppRoute.StartupError -> UnsupportedMvpRoute("No se pudo cargar el contenido inicial. Reinicia la app e intenta nuevamente.")
        AppRoute.Settings -> {
            val viewModel = koinViewModel<SettingsViewModel>(viewModelStoreOwner = routeViewModelStoreOwner)
            val state by viewModel.uiState.collectAsState()
            LaunchedEffect(viewModel) {
                viewModel.effects.collect { effect ->
                    when (effect) {
                        is SettingsUiEffect.Navigate -> navigator.navigate(effect.route)
                    }
                }
            }
            SettingsScreen(state = state, onEvent = viewModel::onEvent)
        }
        is AppRoute.Paywall -> UnsupportedMvpRoute("Suscripcion no disponible en el MVP offline.")
        is AppRoute.Legal -> UnsupportedMvpRoute("Documento legal no disponible en este MVP.")
        is AppRoute.Tutorial -> UnsupportedMvpRoute("Tutorial no disponible en este MVP.")
    }
}

@Composable
private fun UnsupportedMvpRoute(message: String) {
    Text(message)
}

@Composable
private fun rememberRouteViewModelStoreOwner(route: AppRoute): ViewModelStoreOwner {
    val owner = remember(route) { RouteViewModelStoreOwner() }
    DisposableEffect(owner) {
        onDispose { owner.viewModelStore.clear() }
    }
    return owner
}

private class RouteViewModelStoreOwner : ViewModelStoreOwner {
    override val viewModelStore: ViewModelStore = ViewModelStore()
}
