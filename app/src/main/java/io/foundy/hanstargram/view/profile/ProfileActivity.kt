package io.foundy.hanstargram.view.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.GridView
import androidx.activity.viewModels
import io.foundy.hanstargram.R
import io.foundy.hanstargram.base.ViewBindingActivity
import io.foundy.hanstargram.databinding.ActivityProfileBinding
import io.foundy.hanstargram.view.profile.profilepostlist.ProfilePostAdapter
import io.foundy.hanstargram.view.profile.profilepostlist.ProfilePostItem

class ProfileActivity : ViewBindingActivity<ActivityProfileBinding>() {

    private val viewModel: ProfileViewModel by viewModels()
    private var gridView:GridView ?= null
    private var arrayList:ArrayList<ProfilePostItem> ?= null
    private var profilePostAdapter:ProfilePostAdapter ?= null

    override val bindingInflater: (LayoutInflater) -> ActivityProfileBinding
        get() = ActivityProfileBinding::inflate

    companion object {
        private const val TAG = "ProfileActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gridView = findViewById(R.id.postGridView)
        arrayList = setDataList()
        profilePostAdapter = ProfilePostAdapter(applicationContext, arrayList!!)
        gridView?.adapter = profilePostAdapter

    }

    private fun setDataList() : ArrayList<ProfilePostItem>{
        val arrayList : ArrayList<ProfilePostItem> = ArrayList()
        arrayList.add(ProfilePostItem(R.drawable.ic_baseline_star_24))
        arrayList.add(ProfilePostItem(R.drawable.ic_baseline_star_24))
        arrayList.add(ProfilePostItem(R.drawable.ic_baseline_star_24))
        arrayList.add(ProfilePostItem(R.drawable.ic_baseline_star_24))
        arrayList.add(ProfilePostItem(R.drawable.ic_baseline_star_24))
        arrayList.add(ProfilePostItem(R.drawable.ic_baseline_star_24))
        arrayList.add(ProfilePostItem(R.drawable.ic_baseline_star_24))
        return arrayList
    }
}