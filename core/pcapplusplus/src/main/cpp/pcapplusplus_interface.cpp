/*
 * Copyright 2022 CASL0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <jni.h>
#include <android/log.h>
#include <string>
#include "Packet.h"
#include "IPv4Layer.h"
#include "IPv6Layer.h"
#include "TcpLayer.h"
#include "UdpLayer.h"
#include "SSLLayer.h"
#include "HttpLayer.h"

#ifdef __cplusplus
extern "C" {
#endif

const char *TAG = "PcapPlusPlusNativeInterface";

#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)

static pcpp::Packet rawPacketBytesToPacket(const uint8_t *packetBytes, int packetLength);
static std::string getTlsServerName(const pcpp::Packet &packet);
static std::string getHttpHostName(const pcpp::Packet &packet);
static std::string getSrcIpAddress(const pcpp::Packet &packet);
static std::string getDstIpAddress(const pcpp::Packet &packet);
static int getSrcPort(const pcpp::Packet &packet);
static int getDstPort(const pcpp::Packet &packet);
static std::string getProtocol(const pcpp::Packet &packet);

JNIEXPORT jobject JNICALL
Java_jp_co_casl0_android_simpleappblocker_core_pcapplusplus_PcapPlusPlusInterface_analyzePacket(
        JNIEnv *env,
        jobject thiz,
        jbyteArray packet,
        jint packetLength) {
    const jbyte *packetBytes = env->GetByteArrayElements(packet, nullptr);
    auto parsedPacket = rawPacketBytesToPacket(
            reinterpret_cast<const uint8_t *>(packetBytes),
            packetLength
    );

    jclass clazz = env->FindClass(
            "jp/co/casl0/android/simpleappblocker/core/pcapplusplus/model/ParsedPacket"
    );
    if (clazz == nullptr) {
        LOGE(TAG, "class not found");
        return nullptr;
    }

    jmethodID ctor = env->GetMethodID(
            clazz,
            "<init>",
            "(Ljava/lang/String;ILjava/lang/String;ILjava/lang/String;)V"
    );
    if (ctor == nullptr) {
        LOGE(TAG, "method not found");
        return nullptr;
    }

    return env->NewObject(
            clazz,
            ctor,
            env->NewStringUTF(getSrcIpAddress(parsedPacket).c_str()),
            getSrcPort(parsedPacket),
            env->NewStringUTF(getDstIpAddress(parsedPacket).c_str()),
            getDstPort(parsedPacket),
            env->NewStringUTF(getProtocol(parsedPacket).c_str())
    );

}

JNIEXPORT jstring JNICALL
Java_jp_co_casl0_android_simpleappblocker_core_pcapplusplus_PcapPlusPlusInterface_getServerNameNative(
        JNIEnv *env,
        jobject thiz,
        jbyteArray packet,
        jint packetLength) {
    const jbyte *packetBytes = env->GetByteArrayElements(packet, nullptr);
    auto parsedPacket = rawPacketBytesToPacket(reinterpret_cast<const uint8_t *>(packetBytes),
                                               packetLength);

    if (auto hostName = getTlsServerName(parsedPacket); !hostName.empty()) {
        LOGD("sni: %s", hostName.c_str());
        return env->NewStringUTF(hostName.c_str());
    }

    if (auto hostName = getHttpHostName(parsedPacket); !hostName.empty()) {
        LOGD("host name: %s", hostName.c_str());
        return env->NewStringUTF(hostName.c_str());
    }

    return env->NewStringUTF("");
}

/**
 * パケットバイト列をpcpp::Packetに変換する関数
 * @param packetBytes パケットバイト列
 * @param packetLength packetBytesのバイト長
 * @return 変換後のpcpp::Packetインスタンス
 */
pcpp::Packet rawPacketBytesToPacket(const uint8_t *packetBytes, int packetLength) {
    if (packetBytes == nullptr) {
        LOGE("packet is nullptr!");
        return pcpp::Packet();
    }

    timeval time;    //RawPacketはコンストラクトに時刻が必要
    gettimeofday(&time, nullptr);
    pcpp::RawPacket rawPacket(packetBytes, packetLength, time, false, pcpp::LINKTYPE_RAW);
    return pcpp::Packet(&rawPacket);
}

/**
 * SNIからサーバー名を取得する関数
 * @param packet SNIを取得したいパケット
 * @return SNIの文字列(取得できなかった場合は空文字)
 * */
std::string getTlsServerName(const pcpp::Packet &packet) {
    pcpp::SSLHandshakeLayer *sslHandshakeLayer = nullptr;
    if (sslHandshakeLayer = packet.getLayerOfType<pcpp::SSLHandshakeLayer>(); sslHandshakeLayer ==
                                                                              nullptr) {
        // TLSのレイヤーがない場合は空文字を返す
        return std::string();
    }

    pcpp::SSLClientHelloMessage *clientHelloMessage = nullptr;
    if (clientHelloMessage = sslHandshakeLayer->getHandshakeMessageOfType<pcpp::SSLClientHelloMessage>();
            clientHelloMessage == nullptr) {
        // TLSハンドシェイクClientHelloが見つからなかった場合は空文字を返す
        return std::string();
    }

    if (auto sniExt = clientHelloMessage->getExtensionOfType<pcpp::SSLServerNameIndicationExtension>();
            sniExt != nullptr) {
        // SNIを指定している場合は当該文字列を返す
        return sniExt->getHostName();
    }
    return std::string();
}

/**
 * HTTPのHostヘッダの文字列を取得する関数
 * @param packet Hostヘッダを取得したいパケット
 * @return Hostヘッダの文字列(取得できなかった場合は空文字)
 * */
std::string getHttpHostName(const pcpp::Packet &packet) {
    pcpp::HttpRequestLayer *httpRequestLayer = nullptr;
    if (httpRequestLayer = packet.getLayerOfType<pcpp::HttpRequestLayer>(); httpRequestLayer ==
                                                                            nullptr) {
        // HTTPリクエストではない場合は空文字を返す
        return std::string();
    }

    if (auto hostField = httpRequestLayer->getFieldByName(PCPP_HTTP_HOST_FIELD); hostField !=
                                                                                 nullptr) {
        // Hostヘッダが存在すれば当該文字列を返す
        return hostField->getFieldValue();
    }
    return std::string();
}

/**
 * 送信元IPアドレスの文字列を取得する関数
 * @param packet パケット
 * @return 送信元IPアドレスの文字列(取得できなかった場合は空文字)
 */
std::string getSrcIpAddress(const pcpp::Packet &packet) {
    if (packet.isPacketOfType(pcpp::IPv4)) {
        return packet.getLayerOfType<pcpp::IPv4Layer>()->getSrcIPAddress().toString();
    } else if (packet.isPacketOfType(pcpp::IPv6)) {
        return packet.getLayerOfType<pcpp::IPv6Layer>()->getSrcIPAddress().toString();
    }
    return std::string();
}

/**
 * 宛先IPアドレスの文字列を取得する関数
 * @param packet パケット
 * @return 宛先IPアドレスの文字列(取得できなかった場合は空文字)
 */
std::string getDstIpAddress(const pcpp::Packet &packet) {
    if (packet.isPacketOfType(pcpp::IPv4)) {
        return packet.getLayerOfType<pcpp::IPv4Layer>()->getDstIPAddress().toString();
    } else if (packet.isPacketOfType(pcpp::IPv6)) {
        return packet.getLayerOfType<pcpp::IPv6Layer>()->getDstIPAddress().toString();
    }
    return std::string();
}

/**
 * 送信元ポートの文字列を取得する関数
 * @param packet パケット
 * @return 送信元ポート(取得できなかった場合は0)
 */
int getSrcPort(const pcpp::Packet &packet) {
    if (auto tcpLayer = packet.getLayerOfType<pcpp::TcpLayer>(); tcpLayer != nullptr) {
        return tcpLayer->getSrcPort();
    }
    if (auto udpLayer = packet.getLayerOfType<pcpp::UdpLayer>(); udpLayer != nullptr) {
        return udpLayer->getSrcPort();
    }
    return 0;
}

/**
 * 宛先ポートの文字列を取得する関数
 * @param packet パケット
 * @return 宛先ポート(取得できなかった場合は0)
 */
int getDstPort(const pcpp::Packet &packet) {
    if (auto tcpLayer = packet.getLayerOfType<pcpp::TcpLayer>(); tcpLayer != nullptr) {
        return tcpLayer->getDstPort();
    }
    if (auto udpLayer = packet.getLayerOfType<pcpp::UdpLayer>(); udpLayer != nullptr) {
        return udpLayer->getDstPort();
    }
    return 0;
}

/**
 * プロトコルの文字列を取得する関数
 * @param packet
 * @return プロトコル(取得できなかった場合はUNKNOWN)
 */
std::string getProtocol(const pcpp::Packet &packet) {
    if (packet.getLayerOfType<pcpp::TcpLayer>() != nullptr) {
        return "TCP";
    } else if (packet.getLayerOfType<pcpp::UdpLayer>() != nullptr) {
        return "UDP";
    }
    return "UNKNOWN";
}

#ifdef __cplusplus
}
#endif
