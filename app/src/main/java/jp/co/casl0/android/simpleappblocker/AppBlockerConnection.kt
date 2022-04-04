package jp.co.casl0.android.simpleappblocker

import android.os.ParcelFileDescriptor
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Runnable
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ReadOnlyBufferException

class AppBlockerConnection(private val tunnelInterface: ParcelFileDescriptor) : Runnable {
    companion object {
        private const val MAX_PACKET_SIZE = Short.MAX_VALUE.toInt()
    }

    override fun run() {
        val fis = FileInputStream(tunnelInterface.fileDescriptor)
        val packet = ByteBuffer.allocate(MAX_PACKET_SIZE)
        while (true) {
            val length: Int = try {
                fis.read(packet.array())
            } catch (e: ReadOnlyBufferException) {
                val errMsg = e.localizedMessage
                if (errMsg != null) Logger.d(errMsg)
                0
            } catch (e: UnsupportedOperationException) {
                val errMsg = e.localizedMessage
                if (errMsg != null) Logger.d(errMsg)
                0
            } catch (e: IOException) {
                val errMsg = e.localizedMessage
                if (errMsg != null) Logger.d(errMsg)
                0
            }
            packet.clear()
            try {
                Thread.sleep(10)
            } catch (e: InterruptedException) {
                Logger.d("AppBlockerConnection interrupted")
                tunnelInterface.close()
                return
            }
        }
    }
}