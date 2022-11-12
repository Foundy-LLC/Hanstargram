package io.foundy.hanstargram.view.profile.edit

import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.commit
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.foundy.common.base.ViewBindingFragment
import io.foundy.domain.model.UserDetail
import io.foundy.hanstargram.R
import io.foundy.hanstargram.databinding.FragmentProfileEditBinding
import io.foundy.hanstargram.util.toBitmap
import kotlinx.coroutines.launch

class ProfileEditFragment(
    private val userDetail: UserDetail,
) : ViewBindingFragment<FragmentProfileEditBinding>() {

    private val viewModel: ProfileEditViewModel by viewModels()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentProfileEditBinding
        get() = FragmentProfileEditBinding::inflate

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { imageUri ->
            if (imageUri != null) {
                val bitmap = imageUri.toBitmap(requireContext())
                viewModel.updateImageBitmap(bitmap)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.bind(userDetail.name, userDetail.introduce)

        initProfile()
        initToolbar()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect(::updateUi)
            }
        }
    }

    private fun initToolbar() {
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(binding.toolBar)

        activity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.profile_edit_title)
        }

        binding.toolbarApply.setOnClickListener {
            viewModel.sendChangedInfo()
        }
    }

    private fun initProfile() {
        binding.apply {
            profileEditName.setText(userDetail.name)
            profileEditIntroduce.setText(userDetail.introduce)
            profileEditImage.setOnClickListener {
                onClickImage()
            }
            profileEditName.addTextChangedListener {
                if (it != null) {
                    viewModel.updateName(it.toString())
                }
            }
            profileEditIntroduce.addTextChangedListener {
                if (it != null) {
                    viewModel.updateIntroduce(it.toString())
                }
            }
            userDetail.profileImageUrl?.let { url ->
                val storageReference = Firebase.storage.reference
                Glide.with(this@ProfileEditFragment)
                    .load(storageReference.child(url))
                    .fallback(R.drawable.ic_baseline_person_24)
                    .circleCrop()
                    .into(binding.profileEditImage)
            }
        }
    }

    private fun updateUi(uiState: ProfileEditUiState) {
        binding.profileEidtProgressBar.isVisible = uiState.isLoading
        binding.toolbarApply.apply {
            val canSave = viewModel.canSave
            isEnabled = canSave
            alpha = if (canSave) 1.0F else 0.25F
        }

        if (uiState.isImageChanged) {
            updateUserImage(uiState.selectedImageBitmap)
        }

        if (uiState.successToSave) {
            showSnackBar(getString(R.string.success_to_change_profile))
            setFragmentResult(
                "ProfileEdit",
                bundleOf("isChanged" to viewModel.isChanged, "uuid" to userDetail.uuid)
            )
            closeFragment()
        }
        if (uiState.userMessage != null) {
            showSnackBar(uiState.userMessage)
            viewModel.userMessageShown()
        }
    }

    private fun updateUserImage(bitmap: Bitmap?) {
        Glide.with(this@ProfileEditFragment)
            .load(bitmap)
            .fallback(R.drawable.ic_baseline_person_24)
            .circleCrop()
            .into(binding.profileEditImage)
    }

    private fun onClickImage() {
        val selectedImage = viewModel.uiState.value.selectedImageBitmap
        val oldProfileImageUrl = userDetail.profileImageUrl
        val isImageChanged = viewModel.uiState.value.isImageChanged

        if (selectedImage == null && (oldProfileImageUrl == null || isImageChanged)) {
            showImagePicker()
        } else {
            MaterialAlertDialogBuilder(requireActivity())
                .setItems(R.array.image_options) { _, which ->
                    when (which) {
                        0 -> {
                            showImagePicker()
                        }
                        1 -> {
                            viewModel.updateImageBitmap(null)
                        }
                        else -> throw IllegalArgumentException()
                    }
                }.create()
                .show()
        }
    }

    private fun closeFragment() {
        parentFragmentManager.commit {
            parentFragmentManager.popBackStack()
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun showImagePicker() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}