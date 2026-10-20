#!/usr/bin/env bash
#
# build-with-timestamp.sh
# 构建 RobotSuitZulu 的 Release APK，并复制到项目根目录（文件名带时间戳后缀）。
#
# 用法:
#   ./build-with-timestamp.sh
#
# 环境变量（可选）:
#   BUILD_JAVA_HOME  覆盖默认 JDK 路径。本工程 Gradle 8.7 + AGP 8.7.3 要求 JDK 17，
#                    默认优先使用 Android Studio 自带的 JBR 17，缺失时回退
#                    /usr/libexec/java_home -v 17。
#                    注意：不直接读取环境中的 JAVA_HOME，因为它可能指向旧版 JDK 8
#                    （如 shell profile 的全局设置），会导致 AGP 8.x 构建失败。
#
# 说明:
#   - 构建统一使用项目自带 gradle wrapper（gradlew），版本由
#     gradle/wrapper/gradle-wrapper.properties 锁定（8.7），与系统安装的
#     Gradle（可能是 9.x，与 AGP 8.7.3 不兼容）无关。
#   - 项目根存在 keystore.properties（gitignored）时产物为已签名
#     app-release.apk，文件名 RobotSuitZulu-release_<ts>.apk；
#     否则回退为未签名 app-release-unsigned.apk。

set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_NAME="RobotSuitZulu"
GRADLEW="$PROJECT_DIR/gradlew"
RELEASE_OUT="$PROJECT_DIR/app/build/outputs/apk/release"

# JDK 17 探测：BUILD_JAVA_HOME > Android Studio JBR > /usr/libexec/java_home
java_ok() { [ -n "$1" ] && [ -x "$1/bin/java" ] && "$1/bin/java" -version 2>&1 | grep -q 'version "17'; }
if [ -n "${BUILD_JAVA_HOME:-}" ] && java_ok "$BUILD_JAVA_HOME"; then
  export JAVA_HOME="$BUILD_JAVA_HOME"
else
  for c in "/Applications/Android Studio.app/Contents/jbr/Contents/Home" \
           "$HOME/Library/Java/JavaVirtualMachines/"jbr-*/Contents/Home; do
    if java_ok "$c"; then export JAVA_HOME="$c"; break; fi
  done
  if [ -z "${JAVA_HOME:-}" ]; then
    JAVA_HOME="$(/usr/libexec/java_home -v 17 2>/dev/null)" || {
      echo "错误: 未找到 JDK 17" >&2
      exit 1
    }
  fi
fi

if [ ! -x "$GRADLEW" ]; then
  echo "错误: 找不到可执行的 gradlew: $GRADLEW" >&2
  exit 1
fi
if [ ! -x "$JAVA_HOME/bin/java" ]; then
  echo "错误: JAVA_HOME 无效: $JAVA_HOME" >&2
  exit 1
fi

echo "== JAVA_HOME = $JAVA_HOME"
echo "== Gradle wrapper 版本 =="
grep distributionUrl "$PROJECT_DIR/gradle/wrapper/gradle-wrapper.properties"
echo "== 开始构建 Release（使用项目 gradlew，勿用系统 gradle 9.x）=="
cd "$PROJECT_DIR"
"$GRADLEW" :app:assembleRelease

# 已签名优先，未签名回退
if [ -f "$RELEASE_OUT/app-release.apk" ]; then
  APK_SRC="$RELEASE_OUT/app-release.apk"
  SUFFIX="release"
elif [ -f "$RELEASE_OUT/app-release-unsigned.apk" ]; then
  APK_SRC="$RELEASE_OUT/app-release-unsigned.apk"
  SUFFIX="release-unsigned"
  echo "警告: 产物未签名（缺少项目根 keystore.properties），仅可测试分发" >&2
else
  echo "错误: 构建结束但未找到产物（app-release.apk / app-release-unsigned.apk）" >&2
  exit 1
fi

TIMESTAMP="$(date +%Y%m%d_%H%M%S)"
DEST="$PROJECT_DIR/${APP_NAME}-${SUFFIX}_${TIMESTAMP}.apk"
cp "$APK_SRC" "$DEST"

echo ""
echo "== 构建并归档完成 =="
echo "源产物: $APK_SRC"
echo "归档文件: $DEST"
ls -lh "$DEST" | awk '{print "大小: "$5}'
