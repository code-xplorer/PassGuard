package com.ismail.creatvt.passguard.helpers

import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.util.Log
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.ismail.creatvt.passguard.R
import java.util.Random

object ColorFactory {

    data class Color(val name: String, val colorRes: Int)

    val colors = arrayListOf(
        Color("color_red", R.color.color_red),
        Color("color_light_red", R.color.color_light_red),
        Color("color_orange_dark", R.color.color_orange_dark),
        Color("color_orange", R.color.color_orange),
        Color("color_yellow", R.color.color_yellow),
        Color("color_light_yellow", R.color.color_light_yellow),
        Color("color_yellow_green", R.color.color_yellow_green),
        Color("color_light_green", R.color.color_light_green),
        Color("color_green", R.color.color_green),
        Color("color_mid_green", R.color.color_mid_green),
        Color("color_dark_green", R.color.color_dark_green),
        Color("color_dark_green_blue", R.color.color_dark_green_blue),
        Color("color_green_blue", R.color.color_green_blue),
        Color("color_light_blue", R.color.color_light_blue),
        Color("color_blue", R.color.color_blue),
        Color("color_dark_blue", R.color.color_dark_blue),
        Color("color_light_indigo", R.color.color_light_indigo),
        Color("color_indigo", R.color.color_indigo),
        Color("color_dark_indigo", R.color.color_dark_indigo),
        Color("color_light_purple", R.color.color_light_purple),
        Color("color_purple", R.color.color_purple),
        Color("color_dark_purple", R.color.color_dark_purple),
        Color("color_dark_pink", R.color.color_dark_pink),
        Color("color_pink", R.color.color_pink),
        Color("color_brown", R.color.color_brown),
        Color("color_light_brown", R.color.color_light_brown),
        Color("color_light_grey", R.color.color_light_grey),
        Color("color_grey", R.color.color_grey),
        Color("color_dark_grey", R.color.color_dark_grey),
        Color("other", R.color.colorAvatarBackground)
    )

    fun getColorForName(name: String?): Color {
        val color = colors.firstOrNull {
            it.name == name
        }
        Log.d("ColorFactory", "getColorForName : $name, ${color?.name}")
        return color ?: colors[0]
    }

    val Int.textColor
        get() = if((((0.299 * red) + (0.587 * green) + (0.114 * blue)) / 255) > 0.5)
            BLACK
        else
            WHITE

    fun getRandomColor(): Color {
        val generator = Random()
        return colors[generator.nextInt(colors.size)]
    }
}