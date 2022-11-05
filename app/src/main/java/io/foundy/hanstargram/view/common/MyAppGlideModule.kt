package io.foundy.hanstargram.view.common

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.google.firebase.storage.StorageReference
import java.io.InputStream
import com.firebase.ui.storage.images.FirebaseImageLoader

@GlideModule
class MyAppGlideModule : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.append(
            StorageReference::class.java,
            InputStream::class.java,
            FirebaseImageLoader.Factory()
        )
    }
}