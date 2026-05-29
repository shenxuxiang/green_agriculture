package com.example.green_agriculture.components

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.IdRes
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.green_agriculture.extend.dp

class ImageWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ImageView(context, attrs, defStyleAttr) {
    var shapeCorner: Int = 0
        set(value) {
            if (value == field) return
            field = value

            background = GradientDrawable().apply {
                cornerRadius = value.dp
            }
        }

    var placeholderDrawable: Drawable? = null

    var imageSrc: String? = null
        set(value) {
            if (value == field) return

            field = value

            val transitionOptions = DrawableTransitionOptions.withCrossFade(300)

            Glide.with(context)
                .load(value)
                .error(placeholderDrawable)
                .transition(transitionOptions)
                .placeholder(placeholderDrawable)
                .into(this)
        }

    init {
        clipToOutline = true
    }

    companion object {
        @JvmStatic
        @BindingAdapter("shapeCorner")
        fun bindShapeCorner(view: ImageWidget, shapeCorner: Int) {
            view.shapeCorner = shapeCorner
        }

        @JvmStatic
        @BindingAdapter("imageSrc", "placeholder", requireAll = false)
        fun bindImageSrc(view: ImageWidget, imageSrc: String?, @IdRes placeholder: Int?) {
            if (placeholder != null) view.placeholderDrawable =
                view.context.getDrawable(placeholder)

            view.imageSrc = imageSrc
        }
    }
}