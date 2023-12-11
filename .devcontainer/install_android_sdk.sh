#!/bin/bash

ANDROID_SDK_VERSION="9123335"
ANDROID_BUILD_TOOLS_VERSION="33.0.1"
ANDROID_API_LEVEL="33"
ANDROID_NDK_VERSION="25.2.9519653"
CMAKE_VERSION="3.18.1"

mkdir -p /home/vscode/.android/sdk
cd /home/vscode/.android/sdk
export ANDROID_SDK_ROOT=/home/vscode/.android/sdk

curl -L -o commandlinetools.zip https://dl.google.com/android/repository/commandlinetools-linux-${ANDROID_SDK_VERSION}_latest.zip
unzip -q -d ${ANDROID_SDK_ROOT}/cmdline-tools commandlinetools.zip
# sdkmanagerのrequirementで <sdk>/cmdline-tools/latest というパスに配置する必要がある
mv ${ANDROID_SDK_ROOT}/cmdline-tools/cmdline-tools ${ANDROID_SDK_ROOT}/cmdline-tools/latest
rm -f commandlinetools.zip

export PATH=${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin:${PATH}
touch repositories.cfg
yes | sdkmanager --licenses && yes | sdkmanager --update
sdkmanager "platform-tools" "build-tools;${ANDROID_BUILD_TOOLS_VERSION}" "platforms;android-${ANDROID_API_LEVEL}"

# Android NDKのインストール
sdkmanager --install "ndk;${ANDROID_NDK_VERSION}"

# cmakeのインストール
sdkmanager --install "cmake;${CMAKE_VERSION}"

cd -
