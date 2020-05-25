package com.alimoradi.data.util

import android.content.Context
import android.net.Uri
import java.io.ByteArrayOutputStream

object ImageUtils {

    fun getScaledGif(context: Context, uri: Uri, maxWidth: Int, maxHeight: Int, quality: Int = 90): ByteArray {
        val gif = GlideApp
                .with(context)
                .asGif()
                .load(uri)
                .centerInside()
                .encodeQuality(quality)
                .submit(maxWidth, maxHeight)
                .get()

        val outputStream = ByteArrayOutputStream()
        GifEncoder(context, GlideApp.get(context).bitmapPool).encodeTransformedToStream(gif, outputStream)
        return outputStream.toByteArray()
    }

    fun getScaledImage(context: Context, uri: Uri, maxWidth: Int, maxHeight: Int, quality: Int = 90): ByteArray {
        return GlideApp
                .with(context)
                .`as`(ByteArray::class.java)
                .load(uri)
                .centerInside()
                .encodeQuality(quality)
                .submit(maxWidth, maxHeight)
                .get()
    }

}