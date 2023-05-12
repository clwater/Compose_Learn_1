package com.clwater.compose_learn_1

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.clwater.compose_learn_1.ui.theme.Compose_Learn_1Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * https://developer.android.com/jetpack/compose/side-effects?hl=zh-cn#state-effect-use-cases
 * https://blog.appcircle.io/article/jetpack-compose-side-effects-with-examples
 */
class MainActivity : ComponentActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    class LaunchedEffectTestViewModel : ViewModel() {
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
//        Test1(viewModel)
//        Test2()
//        Test3()
        Test4()
    }

    @Composable
    fun Test4() {
        var timer by remember { mutableStateOf(0) }
        var timer2 by remember { mutableStateOf(0) }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column {
                Text("Time $timer")
                Text("Time2 $timer2")
            }
        }

        LaunchedEffect(timer) {
            Log.d("clwater", "LaunchedEffect: $timer")
            delay(1000)
            timer++
        }

        SideEffect {
            Log.d("clwater", "SideEffect: $timer")
//            Thread.sleep(1000)
//            delay(1000)
            timer2 = timer
        }
    }

    @Composable
    fun Test3() {
        var showTop by remember { mutableStateOf(true) }
        var showMiddle by remember { mutableStateOf(true) }
        var showChild by remember { mutableStateOf(true) }

        if (showTop) {
            Column {
                DisposableEffect(Unit) {
                    onDispose {
                        Log.d("clwater", "Top: DisposableEffect onDispose")
                    }
                }
                Text(text = "Top")
                Button(onClick = { showTop = false }) {
                    Text(text = "Hide Top")
                }
                if (showMiddle) {
                    Column {
                        DisposableEffect(Unit) {
                            onDispose {
                                Log.d("clwater", "Middle: DisposableEffect onDispose")
                            }
                        }
                        Text(text = "Middle")
                        Button(onClick = { showMiddle = false }) {
                            Text(text = "Hide Middle")
                        }
                        if (showChild) {
                            Column {
                                DisposableEffect(Unit) {
                                    onDispose {
                                        Log.d("clwater", "Child: DisposableEffect onDispose")
                                    }
                                }
                                Text(text = "Child")
                                Button(onClick = { showChild = false }) {
                                    Text(text = "Hide Child")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun DelayText(touchCount: Int, snackbarHostState: SnackbarHostState) {
        Log.d("clwater", "DelayText: $touchCount")
        val rememberedText by rememberUpdatedState(newValue = touchCount)
        LaunchedEffect(Unit) {
            delay(3000)
            snackbarHostState.showSnackbar("DelayText: $rememberedText")
//            snackbarHostState.showSnackbar("DelayText: $touchCount")
        }
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun Test2() {
        var touchEnable by remember {
            mutableStateOf(true)
        }
        var touchCount by remember { mutableStateOf(0) }

        var trigger by remember { mutableStateOf(0) }
        val elapsed = animateIntAsState(
            targetValue = trigger * 1000,
            animationSpec = tween(trigger * 1000, easing = LinearEasing)
        )

        val scope = rememberCoroutineScope()

        val snackbarHostState = SnackbarHostState()
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) {
            Column(Modifier.padding(12.dp)) {
                Text(text = "Test LaunchedEffect")
                Text(text = " Count down: ${(3000 - elapsed.value) / 1000f}")
                Button(
                    enabled = touchEnable,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.5f),
                    onClick = {
                        if (touchCount == 0) {
                            scope.launch {
                                trigger = 3
                                delay(3000)
                                touchEnable = false
                            }
                        }
                        touchCount += 1
                    }
                ) {
                    Text(text = "Touch me")
                }
                Text(text = "Touch count: $touchCount")
                if (touchCount > 0) {
                    DelayText(touchCount, snackbarHostState)
                }

                Button(
                    enabled = !touchEnable,
                    onClick = {
                        touchCount = 0
                        touchEnable = true
                        trigger = 0
                    }
                ) {
                    Text(text = "Reset")
                }
            }
        }
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
