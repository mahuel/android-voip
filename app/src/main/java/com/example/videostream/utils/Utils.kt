package com.example.videostream.utils

import android.graphics.Bitmap
import io.github.g0dkar.qrcode.QRCode


class Utils {

    companion object {
        fun createQrCodeAsBitmap(data: String): Bitmap {
            return QRCode(data).render(
                margin = 10
            ).nativeImage() as Bitmap
        }
    }
}