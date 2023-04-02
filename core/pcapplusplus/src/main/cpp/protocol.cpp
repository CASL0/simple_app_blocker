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

#include <optional>
#include "protocol.h"

/**
 * プロトコル種別を文字列に変換します
 * @param protocolType
 * @return プロトコル文字列（不明なプロトコルの場合は無効値）
 */
static std::optional<std::string> convertToString(const pcpp::ProtocolType protocolType) {
    switch (protocolType) {
        case pcpp::TCP:
            return "TCP";
        case pcpp::UDP:
            return "UDP";
        default:
            return std::nullopt;
    }
}

std::string Jni::PcapPlusPlus::Protocol::getProtocolTypeAsString(const pcpp::Packet &packet) {
    for (pcpp::Layer *curLayer = packet.getFirstLayer();
         curLayer != nullptr; curLayer = curLayer->getNextLayer()) {
        if (std::optional<std::string> parsedProtocol = convertToString(
                    curLayer->getProtocol());
                parsedProtocol) {
            return parsedProtocol.value();
        }
    }
    return "UNKNOWN";
}
