package jp.co.casl0.android.simpleappblocker

object PcapPlusPlusInterface {
    init {
        System.loadLibrary("pcapplusplus-interface")
    }

    /**
     * パケットの送信元IPアドレスを取得する関数
     * @param packet パケットのバイト列
     * @param length パケットのバイト長
     * @return 送信元IPアドレスの文字列
     */
    external fun getSrcIpAddressNative(packet: ByteArray?, length: Int): String

    /**
     * パケットの送信先IPアドレスを取得する関数
     * @param packet パケットのバイト列
     * @param length パケットのバイト長
     * @return 送信先IPアドレスの文字列
     */
    external fun getDstIpAddressNative(packet: ByteArray?, length: Int): String

    /**
     * パケットの送信元ポートを取得する関数
     * @param packet パケットのバイト列
     * @param length パケットのバイト長
     * @return 送信元ポート
     */
    external fun getSrcPortNative(packet: ByteArray?, length: Int): Int

    /**
     * パケットの送信先ポートを取得する関数
     * @param packet パケットのバイト列
     * @param length パケットのバイト長
     * @return 送信先ポート
     */
    external fun getDstPortNative(packet: ByteArray?, length: Int): Int

    /**
     * パケットのプロトコルを取得する関数
     * @param packet パケットのバイト列
     * @param length パケットのバイト長
     * @return プロトコル文字列
     */
    external fun getProtocolAsStringNative(packet: ByteArray?, length: Int): String

    /**
     * サーバー名を取得する関数
     * @param packet パケットのバイト列
     * @param length パケットのバイト長
     * @return サーバー名の文字列
     */
    external fun getServerNameNative(packet: ByteArray?, length: Int): String
}