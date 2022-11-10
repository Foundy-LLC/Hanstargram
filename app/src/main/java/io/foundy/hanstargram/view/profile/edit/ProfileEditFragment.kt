package io.foundy.hanstargram.view.profile.edit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.foundy.common.base.ViewBindingFragment
import io.foundy.domain.model.UserDetail
import io.foundy.hanstargram.databinding.FragmentProfileEditBinding

class ProfileEditFragment(
    private val userDetail: UserDetail?
) : ViewBindingFragment<FragmentProfileEditBinding>(){

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentProfileEditBinding
        get() = FragmentProfileEditBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("test", userDetail.toString())
    }
}