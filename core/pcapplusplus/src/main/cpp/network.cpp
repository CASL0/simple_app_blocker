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

#include "network.h"
#include "IPv4Layer.h"
#include "IPv6Layer.h"

std::string Jni::PcapPlusPlus::Network::getSrcIpAddress(const pcpp::Packet &packet) {
    if (packet.isPacketOfType(pcpp::IPv4)) {
        return packet.getLayerOfType<pcpp::IPv4Layer>()->getSrcIPAddress().toString();
    } else if (packet.isPacketOfType(pcpp::IPv6)) {
        return packet.getLayerOfType<pcpp::IPv6Layer>()->getSrcIPAddress().toString();
    }
    return std::string();
}

std::string Jni::PcapPlusPlus::Network::getDstIpAddress(const pcpp::Packet &packet) {
    if (packet.isPacketOfType(pcpp::IPv4)) {
        return packet.getLayerOfType<pcpp::IPv4Layer>()->getDstIPAddress().toString();
    } else if (packet.isPacketOfType(pcpp::IPv6)) {
        return packet.getLayerOfType<pcpp::IPv6Layer>()->getDstIPAddress().toString();
    }
    return std::string();
}
