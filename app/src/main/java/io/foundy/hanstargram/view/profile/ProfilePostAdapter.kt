package io.foundy.hanstargram.view.profile

import android.annotation.SuppressLint
import android.content.Context
import android.media.Image
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.foundy.hanstargram.R
import io.foundy.hanstargram.repository.model.PostDto

class ProfilePostAdapter(var context : Context, var postList: MutableList<PostDto>) : BaseAdapter() {
    override fun getCount(): Int {
        return postList.size
    }

    override fun getItem(pos : Int): Any {
        return postList[pos]
    }

    override fun getItemId(pos : Int): Long {
        return pos.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(pos: Int, convertView: View?, parent: ViewGroup?): View {
        val view : View = View.inflate(context, R.layout.profile_gird_item, null)
        val icons : ImageView = view.findViewById(R.id.icons)
        val glide = Glide.with(view)

        glide.load(postList[pos].imageUrl)
            .fallback(R.drawable.ic_baseline_star_24)
            .into(icons)

        return view
    }
}