package com.eatburn.calorieday

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


class ExampleAdapter(private var holder: View) : RecyclerView.ViewHolder(holder) {

    fun setImage(context: Context, img: String?){
        val no_img = RequestOptions.placeholderOf(R.mipmap.ic_launcher_round)
            .error(R.mipmap.ic_launcher_round)
        val show_img = holder.findViewById<View>(R.id.image_view) as ImageView
        Glide.with(context).setDefaultRequestOptions(no_img).load(img).into(show_img)
        Log.d("img",img.toString())
    }

    fun setMeal(meal: String?){
        val show_meal = holder.findViewById<View>(R.id.text_view_1) as TextView
        show_meal.text = meal
    }

    fun setMenu(menu: String?){
        val show_menu = holder.findViewById<View>(R.id.text_view_3) as TextView
        show_menu.text = menu
    }

    fun setCalories(cal: String?){
        val show_cal = holder.findViewById<View>(R.id.text_view_5) as TextView
        show_cal.text = cal
    }

    fun setDate_and_Time(date: String?){
        val show_date = holder.findViewById<View>(R.id.text_view_7) as TextView
        show_date.text = date
    }

    fun setLatitude(lat: String?){
        val show_lat = holder.findViewById<View>(R.id.text_view_9) as TextView
        show_lat.text = lat
    }

    fun setLongitude(ln: String?){
        val show_ln = holder.findViewById<View>(R.id.text_view_11) as TextView
        show_ln.text = ln
    }
}