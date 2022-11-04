package io.foundy.hanstargram.view.profile.profilepostlist

import android.annotation.SuppressLint
import android.content.Context
import android.media.Image
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import io.foundy.hanstargram.R

class ProfilePostAdapter(var context : Context, var arrayList: ArrayList<ProfilePostItem>) : BaseAdapter() {
    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getItem(pos : Int): Any {
        return arrayList[pos]
    }

    override fun getItemId(pos : Int): Long {
        return pos.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(pos: Int, convertView: View?, parent: ViewGroup?): View {
        val view : View = View.inflate(context, R.layout.grid_item, null)
        val icons : ImageView = view.findViewById(R.id.icons)
        val profilePostItem: ProfilePostItem = arrayList.get(pos)

        icons.setImageResource(profilePostItem.icons!!)
        return view
    }
}