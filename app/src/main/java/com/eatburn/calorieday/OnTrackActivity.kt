package com.eatburn.calorieday

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_on_track.*

class OnTrackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_track)

        val exampleList = generateDummyList(5)
        recycler_view.adapter = ExampleAdapter(exampleList)
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)

        ontrack_profileBtn.setOnClickListener {
            startActivity(Intent(this@OnTrackActivity, ProfileActivity::class.java))
        }
        ontrack_mygoalBtn.setOnClickListener {
            startActivity(Intent(this@OnTrackActivity, MyGoalActivity::class.java))
        }
        ontrack_addBtn.setOnClickListener {
            startActivity(Intent(this@OnTrackActivity, AddOnActivity::class.java))
        }
        ontrack_homeBtn.setOnClickListener{
            startActivity(Intent(this@OnTrackActivity, HomeActivity::class.java))
        }
    }

    private fun generateDummyList(size: Int): List<ExampleItem> {
        val list = ArrayList<ExampleItem>()
        for(i in 1 until size+1) {
            val drawable = when (i % 3) {
                0 -> R.drawable.ic_android
                1 -> R.drawable.ic_home_black_24dp
                else -> R.drawable.ic_arrow_back_black_24dp
            }
            val item = ExampleItem(drawable, "Breakfast1112", "KraPhao", "550", "Thu, 23 Apr 2020, 01:40", "37.4220004", "-122.0840154")
            list += item
        }
        return list
    }
}
