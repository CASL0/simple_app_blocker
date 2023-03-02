FROM openjdk:19-jdk-slim

ARG GRADLE_VERSION="8.0.1"
ARG ANDROID_SDK_VERSION="9123335"
ARG ANDROID_BUILD_TOOLS_VERSION="33.0.1"
ARG ANDROID_API_LEVEL="33"
ARG ANDROID_NDK_VERSION="25.2.9519653"
ARG CMAKE_VERSION="3.18.1"

RUN apt-get update -qqy \
    && apt-get install -qqy unzip \
    && rm -rf /var/lib/apt/lists/*

# Gradleのインストール
WORKDIR /opt/gradle
ADD https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip gradle.zip
RUN unzip -q gradle.zip && rm -f gradle.zip
ENV PATH /opt/gradle/gradle-${GRADLE_VERSION}/bin:${PATH}

# Android SDKのインストール
WORKDIR /opt/android/sdk
ENV ANDROID_SDK_ROOT /opt/android/sdk
ADD https://dl.google.com/android/repository/commandlinetools-linux-${ANDROID_SDK_VERSION}_latest.zip commandlinetools.zip
RUN unzip -q -d ${ANDROID_SDK_ROOT}/cmdline-tools commandlinetools.zip \
    # sdkmanagerのrequirementで <sdk>/cmdline-tools/latest というパスに配置する必要がある
    && mv ${ANDROID_SDK_ROOT}/cmdline-tools/cmdline-tools ${ANDROID_SDK_ROOT}/cmdline-tools/latest \
    && rm -f commandlinetools.zip
ENV PATH ${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin:${PATH}
RUN touch repositories.cfg
RUN yes | sdkmanager --licenses && yes | sdkmanager --update
RUN sdkmanager "platform-tools" "build-tools;${ANDROID_BUILD_TOOLS_VERSION}" "platforms;android-${ANDROID_API_LEVEL}"

# Android NDKのインストール
RUN sdkmanager --install "ndk;${ANDROID_NDK_VERSION}"

# cmakeのインストール
RUN sdkmanager --install "cmake;${CMAKE_VERSION}"

WORKDIR /simple_app_blocker
