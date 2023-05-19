package com.clwater.compose_learn_1

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.clwater.compose_learn_1.ui.theme.Compose_Learn_1Theme
import kotlinx.coroutines.delay

/**
 * https://developer.android.com/jetpack/compose/side-effects?hl=zh-cn#state-effect-use-cases
 * https://blog.appcircle.io/article/jetpack-compose-side-effects-with-examples
 * https://medium.com/@callmeryan/jetpack-compose-effect-apis-f572712004a4
 */
class MainActivity : ComponentActivity() {
    companion object {
        const val TAG = "MainActivity"
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

    // #1.1
    suspend fun suspendFunTest() {
        Log.d("clwater", "suspendFunTest Start")
        delay(3000)
        Log.d("clwater", "suspendFunTest Finish")
    }

    // #1.1
    // https://gist.github.com/clwater/5454deed3ae258ba3980d260a0ff3299
//    @Composable
//    fun TestLifecycleCompose() {
//        LaunchedEffect(Unit) {
//            suspendFunTest()
//        }
//        suspendFunTest()
//        Button(
//            onClick = {
//            suspendFunTest()
//        }) {
//            Text(text = "suspendFunTest")
//        }
//    }

    // #1.2
    // https://gist.github.com/clwater/4b9b1bedca4365732de7d8e2c8519190
//    @Composable
//    fun TestLifecycleCompose() {
//        LaunchedEffect(Unit) {
//            Log.d("clwater", "TestLifecycleCompose Enter")
//        }
//        Text(text = "TestLifecycleCompose")
//    }

    // #1.3
    // https://gist.github.com/clwater/440af58717e699d4963f915f520d494a
//    @Composable
//    fun TestLifecycleCompose() {
//        var isShow by remember {
//            mutableStateOf(true)
//        }
//
//        Column {
//            if (isShow) {
//                TestLifecycleComposeText()
//            }
//
//            Button(
//                onClick = { isShow = !isShow }
//            ) {
//                Text(text = "TestLifecycleCompose show: $isShow")
//            }
//        }
//    }
//
//    @Composable
//    fun TestLifecycleComposeText() {
//        LaunchedEffect(Unit) {
//            Log.d("clwater", "TestLifecycleCompose Enter")
//            try {
//                delay(10 * 1000)
//                Log.d("clwater", "TestLifecycleCompose Finish")
//            } catch (e: Exception) {
//                Log.d("clwater", "TestLifecycleCompose Error: $e")
//            }
//        }
//        Text(text = "TestLifecycleCompose")
//    }

    // # 1.4
    // https://gist.github.com/clwater/e38e6e686a52652eda7c0ed23c7d9e0c
    @Composable
    fun TestLifecycleCompose() {
        var clickCount by remember {
            mutableStateOf(0)
        }

        LaunchedEffect(clickCount) {
            Log.d("clwater", "TestLifecycleCompose clickCount: $clickCount")
        }

        Column {
            Button(onClick = { clickCount++ }) {
                Text("clickCount $clickCount")
            }
        }
    }
}
