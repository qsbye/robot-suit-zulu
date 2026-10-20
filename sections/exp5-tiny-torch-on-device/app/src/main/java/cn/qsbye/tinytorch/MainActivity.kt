package cn.qsbye.tinytorch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cn.qsbye.tinytorch.data.MnistDataset
import cn.qsbye.tinytorch.ui.MnistScreen
import cn.qsbye.tinytorch.ui.Trainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val trainer = Trainer(this)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    var trainDs by remember { mutableStateOf<MnistDataset?>(null) }
                    var testDs by remember { mutableStateOf<MnistDataset?>(null) }
                    val scope = rememberCoroutineScope()

                    LaunchedEffect(Unit) {
                        scope.launch {
                            val (t, e) = withContext(Dispatchers.IO) {
                                MnistDataset.load(assets, train = true) to MnistDataset.load(assets, train = false)
                            }
                            trainDs = t; testDs = e
                        }
                    }

                    MnistScreen(
                        trainer = trainer,
                        trainDs = trainDs,
                        testDs = testDs,
                        onStartTrain = {}
                    )
                }
            }
        }
    }
}
