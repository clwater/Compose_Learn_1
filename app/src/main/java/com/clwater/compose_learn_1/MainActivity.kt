package com.clwater.compose_learn_1

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.clwater.compose_learn_1.ui.theme.Compose_Learn_1Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * https://developer.android.com/jetpack/compose/side-effects?hl=zh-cn#state-effect-use-cases
 */
class MainActivity : ComponentActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    class LaunchedEffectTestViewModel : ViewModel() {
//        val showSnackbar = mutableStateOf(false)
        val snackbarCount = mutableStateOf(0)
        val showScaffold = mutableStateOf(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Compose_Learn_1Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    TestLifecycleCompose()
                }
            }
        }
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun TestLifecycleCompose() {
        val viewModel = LaunchedEffectTestViewModel()
        Test1(viewModel)
    }

    @Composable
    fun Test1(viewModel: LaunchedEffectTestViewModel) {
        if (viewModel.showScaffold.value) {
            LaunchedEffectTestScreen(
                snackbarHostState = SnackbarHostState(),
                viewModel = viewModel
            )
        }
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun LaunchedEffectTestScreen(
        snackbarHostState: SnackbarHostState,
        viewModel: LaunchedEffectTestViewModel
    ) {
        val scope = rememberCoroutineScope()

        LaunchedEffect(viewModel.snackbarCount.value) {
            Log.d(
                "clwater",
                "LaunchedEffect: displaying launched effect for count " +
                    "${viewModel.snackbarCount.value}"
            )
            try {
                snackbarHostState.showSnackbar(
                    "LaunchedEffect: LaunchedEffect snackbar: " +
                        " ${viewModel.snackbarCount.value}"
                )
                delay(1000)
                Log.d(
                    "clwater",
                    "delay finish for count ${viewModel.snackbarCount.value}"
                )
            } catch (e: Exception) {
                Log.d(
                    "clwater",
                    "LaunchedEffect: launched Effect coroutine cancelled exception $e"
                )
            }
        }

        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) {
            Column(Modifier.padding(12.dp)) {
                Text(text = "Test LaunchedEffect")
                Text(text = "Snackbar count: ${viewModel.snackbarCount.value}")
                Button(
                    onClick = {
                        viewModel.snackbarCount.value += 1
                    }
                ) {
                    Text(
                        text = "(without scope): Add current snackbar, count: " +
                            "${viewModel.snackbarCount.value}"
                    )
                }

                Button(
                    onClick = {
                        viewModel.snackbarCount.value += 1
                    }
                ) {
                    Text(
                        text = "(in scope): Add current snackbar, count: " +
                            "${viewModel.snackbarCount.value}"
                    )
                }

                Button(
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "(snackbarCount change): LaunchedEffect snackbar: " +
                                    " ${viewModel.snackbarCount.value}"
                            )
                            delay(1000)
                            Log.d(
                                "clwater",
                                "(snackbarCount change): delay finish for count ${viewModel.snackbarCount.value}"
                            )
                        }
                    }
                ) {
                    Text(
                        text = "(in scope): Show current snackbar, " +
                            "count: ${viewModel.snackbarCount.value}"
                    )
                }

                Button(
                    onClick = {
                        viewModel.showScaffold.value = false
                    }
                ) {
                    Text(
                        text = "hide Scaffold"
                    )
                }
            }
        }
    }
}
