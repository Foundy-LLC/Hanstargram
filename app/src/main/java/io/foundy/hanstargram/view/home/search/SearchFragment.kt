package io.foundy.hanstargram.view.home.search

import android.view.LayoutInflater
import android.view.ViewGroup
import io.foundy.hanstargram.base.ViewBindingFragment
import io.foundy.hanstargram.databinding.FragmentSearchBinding

class SearchFragment : ViewBindingFragment<FragmentSearchBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSearchBinding
        get() = FragmentSearchBinding::inflate


}