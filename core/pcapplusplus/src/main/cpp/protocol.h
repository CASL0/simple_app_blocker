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

#ifndef SIMPLEAPPBLOCKER_PROTOCOL_H
#define SIMPLEAPPBLOCKER_PROTOCOL_H

#include <string>
#include "Packet.h"

namespace Jni::PcapPlusPlus::Protocol {
    /**
     * プロトコルの文字列を取得する関数
     * @param packet
     * @return プロトコル(取得できなかった場合はUNKNOWN)
     */
    std::string getProtocolTypeAsString(const pcpp::Packet &packet);

}

#endif //SIMPLEAPPBLOCKER_PROTOCOL_H
