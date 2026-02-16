package com.example.veterinariaapp.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import android.util.Log

/**
 * Utilidad para cargar imágenes con Glide
 */
object ImageLoader {

    private const val TAG = "ImageLoader"

    /**
     * Carga una imagen desde una URL en un ImageView
     */
    fun loadImage(
        context: Context,
        url: String,
        imageView: ImageView,
        placeholder: Int = android.R.drawable.ic_menu_gallery,
        error: Int = android.R.drawable.ic_menu_report_image
    ) {
        Log.d(TAG, "Cargando imagen desde: $url")

        Glide.with(context)
            .load(url)
            .placeholder(placeholder) // Imagen mientras carga
            .error(error) // Imagen si falla
            .centerCrop()
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.e(TAG, "Error al cargar imagen: $url", e)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d(TAG, "Imagen cargada correctamente desde: $dataSource")
                    return false
                }
            })
            .into(imageView)
    }

    /**
     * Carga una imagen circular (para fotos de perfil)
     */
    fun loadCircularImage(
        context: Context,
        url: String,
        imageView: ImageView
    ) {
        Log.d(TAG, "Cargando imagen circular desde: $url")

        Glide.with(context)
            .load(url)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_report_image)
            .circleCrop()
            .into(imageView)
    }

    /**
     * Pre-carga una imagen en caché para mejorar rendimiento
     */
    fun preloadImage(context: Context, url: String) {
        Log.d(TAG, "Pre-cargando imagen en caché: $url")
        Glide.with(context).load(url).preload()
    }
}