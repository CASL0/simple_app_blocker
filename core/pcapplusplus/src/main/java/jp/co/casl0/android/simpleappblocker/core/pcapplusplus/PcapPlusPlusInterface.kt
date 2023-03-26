/*
 * Copyright 2022 CASL0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.casl0.android.simpleappblocker.core.pcapplusplus

import jp.co.casl0.android.simpleappblocker.core.pcapplusplus.model.ParsedPacket

object PcapPlusPlusInterface {
    init {
        System.loadLibrary("pcapplusplus_interface")
    }

    /**
     * パケットを解析する関数
     *
     * @param packet パケットのバイト列
     * @param length パケットのバイト長
     * @return 解析結果
     */
    external fun analyzePacket(packet: ByteArray?, length: Int): ParsedPacket?

    /**
     * サーバー名を取得する関数
     *
     * @param packet パケットのバイト列
     * @param length パケットのバイト長
     * @return サーバー名の文字列
     */
    external fun getServerNameNative(packet: ByteArray?, length: Int): String
}
