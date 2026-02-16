package com.example.veterinariaapp

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.veterinariaapp.utils.ImageLoader

/**
 * Activity temporal SOLO para testing de Glide
 */
class TestImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Crear layout programáticamente
        val imageView = ImageView(this).apply {
            layoutParams = android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        setContentView(imageView)

        // URLs de prueba
        val testUrls = listOf(
            // Imagen de perro
            "https://images.unsplash.com/photo-1587300003388-59208cc962cb?w=800",
            // Imagen de gato
            "https://raw.githubusercontent.com/bumptech/glide/master/static/glide_logo.png",
            // Imagen de conejo
            "https://images.unsplash.com/photo-1585110396000-c9ffd4e4b308?w=800"
        )

        // Probar con la primera URL
        ImageLoader.loadImage(
            context = this,
            url = testUrls[1],
            imageView = imageView
        )
    }
}