# exp5：端上微型训练框架（TinyTorch On-device）

> 参考 [TinyTorch](https://github.com/keith2018/TinyTorch)（MIT License）——将「Tensor/Function 计算图 + 自动微分 + Module/Optimizer」最小内核用纯 Kotlin 在 Android 端实现。配套解读文档：[docs/tinytorch-neural-network-training-framework-robot9.md](../../docs/tinytorch-neural-network-training-framework-robot9.md)

纯 Kotlin + Jetpack Compose，零第三方 ML 框架依赖。MNIST MLP（784→128→10）从零训练全程在手机上完成。

## 架构

```
cn.qsbye.tinytorch
├── core/
│   ├── TensorImpl.kt   # 纯数值张量：shape/strides/float32 存储，matmul(NN/NT/TN)、relu、logSoftmax
│   ├── Function.kt     # 计算图节点 + AccumulateGrad（叶子梯度收集）+ Engine（拓扑调度反传）
│   └── Tensor.kt       # 对外张量 + 算子函数（ReluFn/LogSoftmaxFn/LinearFn/ViewFn）
├── nn/Module.kt        # Module 基类（递归参数汇总、train/eval）、Linear、NLLLoss
├── optim/Optimizer.kt  # SGD / Adam（pow 查表）/ StepLR
├── data/Data.kt        # MnistDataset（gzip IDX 解析）+ DataLoader（batch/shuffle）
├── model/MnistNet.kt   # 784→128→10 MLP
├── train/Serializer.kt # 极简 checkpoint（shape + float 序列）
└── ui/                 # Trainer（StateFlow 驱动）+ MnistScreen（损失曲线/样本预览/保存加载）
```

## 与 TinyTorch（C++）的对应

| TinyTorch | 本工程 | 说明 |
| --- | --- | --- |
| TensorImpl / Tensor 分层 | 同名分层 | impl 只做数值；Tensor 挂 grad/gradFn |
| Function::callForward 建图 | Function.apply | 输入 gradFn 链入 nextFunctions |
| AutogradMeta::backward | Engine.backward | 消费者计数 + 梯度累加，支持分叉 DAG |
| FuncLeaf | AccumulateGrad | 叶子梯度写回 |
| Module.registerModules | registerModule/registerParam | Kotlin 无需手动注册集合，构造函数内完成 |
| Optimizer（Adam 等） | Adam（pow 查表，与 exp4 同源） | |
| DataLoader 迭代器 | Iterable\<Batch\> | shuffle + batch |
| Serializer | Serializer | 简化版，仅参数序列 |

## 与实验四（专用 MLP）的区别

实验四 DepthNet 是固定层序手写前向/反向；本实验为**通用计算图**——任意算子组合、分叉结构（同一 Tensor 多处消费时梯度自动累加）都可用。代价是图节点分配与调度开销。

## 数据与限制

- MNIST 来自 TinyTorch 仓库 `examples/mnist/data/`（原始 Yann LeCun IDX），gzip 后约 11.5MB 打包进 assets
- 端上训练取子集：训练 10000 / 测试 2000（全量 60k 对纯 CPU Kotlin 过重）
- 纯 CPU float32，无 SIMD/NNAPI；算子集仅覆盖 MNIST 必需（matmul/bias-add/relu/log_softmax/nll/view）
- matmul 为朴素三重循环，784×128@64 批次量级可实时；卷积算子未实现

## 验证

- `AutogradTest`：Linear 权重与偏置的解析梯度 vs 有限差分数值梯度（2e-2 容差）✅
- `./gradlew :app:testDebugUnitTest :app:assembleDebug` 通过

## 构建

```bash
cd sections/exp5-tiny-torch-on-device
./gradlew :app:installDebug
```
