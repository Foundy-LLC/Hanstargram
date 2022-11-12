package io.foundy.hanstargram.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore

fun Uri.toBitmap(context: Context): Bitmap =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        ImageDecoder.decodeBitmap(
            ImageDecoder.createSource(context.contentResolver, this)
        ) { decoder: ImageDecoder, _: ImageDecoder.ImageInfo?, _: ImageDecoder.Source? ->
            decoder.isMutableRequired = true
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
        }
    } else {
        @Suppress("DEPRECATION")
        BitmapDrawable(
            context.resources,
            MediaStore.Images.Media.getBitmap(context.contentResolver, this)
        ).bitmap
    }