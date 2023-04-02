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

#include "TcpLayer.h"
#include "UdpLayer.h"
#include "transport.h"

int Jni::PcapPlusPlus::Transport::getSrcPort(const pcpp::Packet &packet) {
    if (auto tcpLayer = packet.getLayerOfType<pcpp::TcpLayer>(); tcpLayer != nullptr) {
        return tcpLayer->getSrcPort();
    }
    if (auto udpLayer = packet.getLayerOfType<pcpp::UdpLayer>(); udpLayer != nullptr) {
        return udpLayer->getSrcPort();
    }
    return 0;
}

int Jni::PcapPlusPlus::Transport::getDstPort(const pcpp::Packet &packet) {
    if (auto tcpLayer = packet.getLayerOfType<pcpp::TcpLayer>(); tcpLayer != nullptr) {
        return tcpLayer->getDstPort();
    }
    if (auto udpLayer = packet.getLayerOfType<pcpp::UdpLayer>(); udpLayer != nullptr) {
        return udpLayer->getDstPort();
    }
    return 0;
}
