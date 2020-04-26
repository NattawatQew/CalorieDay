package com.eatburn.calorieday

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_on_track.*
import java.text.FieldPosition

class OnTrackActivity : AppCompatActivity() {

    private var swipeBackground: ColorDrawable = ColorDrawable(Color.parseColor("#FF6666"))
    private lateinit var deleteIcon: Drawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_track)

        val exampleList = generateDummyList(20)
        recycler_view.adapter = ExampleAdapter(exampleList)
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)
        deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_delete)!!

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(holder: RecyclerView.ViewHolder, position: Int) {
                (recycler_view.adapter as ExampleAdapter).removeItem(holder)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView

                val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2

                if (dX > 0) {
                    swipeBackground.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                    deleteIcon.setBounds(itemView.left + iconMargin, itemView.top + iconMargin, itemView.left + iconMargin + deleteIcon.intrinsicWidth, itemView.bottom - iconMargin)
                } else {
                    swipeBackground.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    deleteIcon.setBounds(itemView.right - iconMargin - deleteIcon.intrinsicWidth, itemView.top + iconMargin, itemView.right - iconMargin, itemView.bottom - iconMargin)
                }

                swipeBackground.draw(c)

                c.save()

                if (dX > 0){
                    c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                } else {
                    c.clipRect(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                }

                c.restore()

                deleteIcon.draw(c)

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recycler_view)

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

    private fun generateDummyList(size: Int): MutableList<ExampleItem> {
        val list = ArrayList<ExampleItem>()
        for(i in 1 until size+1) {
            val drawable = when (i % 3) {
                0 -> R.drawable.ic_android
                1 -> R.drawable.ic_home_black_24dp
                else -> R.drawable.ic_arrow_back_black_24dp
            }
            val item = ExampleItem(drawable, "Breakfast", "KraPhaoKai", "550", "Thu, 23 Apr 2020, 01:40", "37.4220004", "-122.0840154")
            list.add(item)
        }
        return list
    }
}
