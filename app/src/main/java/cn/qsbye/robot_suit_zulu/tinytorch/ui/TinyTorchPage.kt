package cn.qsbye.robot_suit_zulu.tinytorch.ui

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import cn.qsbye.robot_suit_zulu.tinytorch.data.MnistDataset

@Composable
fun TinyTorchPage() {
    val context = LocalContext.current
    val trainer = remember { Trainer(context) }
    var trainDs by remember { mutableStateOf<MnistDataset?>(null) }
    var testDs by remember { mutableStateOf<MnistDataset?>(null) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            MnistDataset.load(context.assets, train = true) to MnistDataset.load(context.assets, train = false)
        }.let { (t, e) ->
            trainDs = t
            testDs = e
        }
    }

    MnistScreen(
        trainer = trainer,
        trainDs = trainDs,
        testDs = testDs,
        onStartTrain = {}
    )
}
