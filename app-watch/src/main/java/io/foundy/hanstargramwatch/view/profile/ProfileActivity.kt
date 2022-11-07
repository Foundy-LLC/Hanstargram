package io.foundy.hanstargramwatch.view.profile

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.google.android.material.color.MaterialColors
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.foundy.common.base.ViewBindingActivity
import io.foundy.hanstargramwatch.R
import io.foundy.hanstargramwatch.databinding.ActivityProfileBinding
import kotlinx.coroutines.launch

class ProfileActivity : ViewBindingActivity<ActivityProfileBinding>() {

    private val viewModel: ProfileViewModel by viewModels()

    override val bindingInflater: (LayoutInflater) -> ActivityProfileBinding
        get() = ActivityProfileBinding::inflate

    companion object {
        fun getIntent(context: Context, userUuid: String): Intent {
            return Intent(context, ProfileActivity::class.java).apply {
                putExtra("userUuid", userUuid)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userUuid = intent.getStringExtra("userUuid")!!
        viewModel.loadUserProfile(userUuid)

        binding.followButton.setOnClickListener {
            viewModel.toggleFollow()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect(::updateUi)
            }
        }
    }

    private fun updateUi(uiState: ProfileUiState) {
        val storageReference = Firebase.storage.reference
        val userDetail = uiState.userDetail

        if (uiState.userMessage != null) {
            Toast.makeText(this, uiState.userMessage, Toast.LENGTH_LONG).show()
            viewModel.userMessageShown()
        }

        if (userDetail != null) {
            binding.apply {
                userDetail.profileImageUrl?.let {
                    Glide.with(this@ProfileActivity)
                        .load(storageReference.child(it))
                        .into(profileImage)
                }

                name.text = userDetail.name
                postsCount.text = userDetail.postCount.toString()
                followersCount.text = userDetail.followersCount.toString()
                followingCount.text = userDetail.followingCount.toString()

                followButton.apply {
                    isVisible = !userDetail.isMe
                    if (userDetail.isCurrentUserFollowing) {
                        setText(R.string.unfollow)
                        setBackgroundColor(Color.TRANSPARENT)
                        setTextColor(
                            MaterialColors.getColor(
                                root,
                                androidx.appcompat.R.attr.colorPrimary
                            )
                        )
                    } else {
                        setText(R.string.follow)
                        setBackgroundColor(
                            MaterialColors.getColor(
                                root,
                                androidx.appcompat.R.attr.colorPrimary
                            )
                        )
                        setTextColor(
                            MaterialColors.getColor(
                                root,
                                androidx.appcompat.R.attr.colorBackgroundFloating
                            )
                        )
                    }
                }
            }
        }
    }
}