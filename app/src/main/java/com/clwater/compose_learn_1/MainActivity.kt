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
        val showInnerButton = mutableStateOf(true)
        val showCommonButton = mutableStateOf(true)
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
//        if (viewModel.showScaffold.value) {
        LaunchedEffectTestScreen(
            snackbarHostState = SnackbarHostState(),
            viewModel = viewModel
        )
//        }
    }

    @Composable
    fun ScopeButton(
        snackbarHostState: SnackbarHostState,
        viewModel: LaunchedEffectTestViewModel
    ) {
        val scope = rememberCoroutineScope()

        Button(
            onClick = {
                scope.launch {
                    Log.d(
                        "clwater",
                        "(Inner Coroutine Scope): displaying " +
                            "launched effect for count " +
                            "${viewModel.snackbarCount.value}"
                    )
                    try {
                        snackbarHostState.showSnackbar(
                            "(Inner Coroutine Scope): LaunchedEffect snackbar: " +
                                " ${viewModel.snackbarCount.value}"
                        )
                    } catch (e: Exception) {
                        Log.e(
                            "clwater",
                            "(Inner Coroutine Scope): launched Effect coroutine " +
                                "cancelled exception $e"
                        )
                    }
                }
            }
        ) {
            Text(
                text = "(Inner Coroutine Scope): Show current snackbar, " +
                    "count: ${viewModel.snackbarCount.value}"
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
                "(LaunchedEffect): displaying launched effect for count " +
                    "${viewModel.snackbarCount.value}"
            )
            try {
                snackbarHostState.showSnackbar(
                    "(LaunchedEffect): LaunchedEffect snackbar: " +
                        " ${viewModel.snackbarCount.value}"
                )
            } catch (e: Exception) {
                Log.e(
                    "clwater",
                    "(LaunchedEffect): launched Effect coroutine cancelled exception $e"
                )
            }
        }

        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) {
            Column(Modifier.padding(12.dp)) {
                Text(text = "Test LaunchedEffect")
                Text(text = "Snackbar count: ${viewModel.snackbarCount.value}")

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        viewModel.snackbarCount.value += 1
                    }
                ) {
                    Text(
                        text = "(Without Scope): Add current snackbar, count: " +
                            "${viewModel.snackbarCount.value}"
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        viewModel.snackbarCount.value += 1
                    }
                ) {
                    Text(
                        text = "(In Scope): Add current snackbar, count: " +
                            "${viewModel.snackbarCount.value}"
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (viewModel.showCommonButton.value) {
                    Button(
                        onClick = {
                            scope.launch {
                                Log.d(
                                    "clwater",
                                    "(In Common Coroutine Scope): displaying " +
                                        "launched effect for count " +
                                        "${viewModel.snackbarCount.value}"
                                )
                                try {
                                    snackbarHostState.showSnackbar(
                                        "(In Common Coroutine Scope): LaunchedEffect snackbar: " +
                                            " ${viewModel.snackbarCount.value}"
                                    )
                                } catch (e: Exception) {
                                    Log.d(
                                        "clwater",
                                        "(In Common Coroutine Scope): launched Effect coroutine " +
                                            "cancelled exception $e"
                                    )
                                }
                            }
                        }
                    ) {
                        Text(
                            text = "(In Common Coroutine Scope): Show current snackbar, " +
                                "count: ${viewModel.snackbarCount.value}"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (viewModel.showInnerButton.value) {
                    ScopeButton(
                        snackbarHostState = snackbarHostState,
                        viewModel = viewModel
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Button(
                    onClick = {
                        viewModel.showCommonButton.value = false
                    }
                ) {
                    Text(
                        text = "hide showCommonButton"
                    )
                }

                Button(
                    onClick = {
                        viewModel.showInnerButton.value = false
                    }
                ) {
                    Text(
                        text = "hide showInnerButton"
                    )
                }
            }
        }
    }
}
