package io.foundy.hanstargram.view.profile.edit

import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
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
import io.foundy.hanstargram.view.welcome.ProfileEditViewModel
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
                viewModel.isChanged = true
                viewModel.isChangedImage = true
                viewModel.selectedImage = bitmap
                setUserImage(bitmap)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFragment()
        initToolbar()
        initViewModel()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect(::updateUi)
            }
        }
    }

    private fun updateUi(uiState: ProfileEditUiState) {
        when (uiState) {
            is ProfileEditUiState.None -> {
                binding.profileEidtProgressBar.isVisible = false
            }
            is ProfileEditUiState.SuccessToSave -> {
                showSnackBar(getString(R.string.success_to_change_profile))
                setFragmentResult(
                    "ProfileEdit",
                    bundleOf("isChanged" to viewModel.isChanged, "uuid" to viewModel.uuid)
                )
                closeFragment()
            }
            is ProfileEditUiState.FailedToSave -> {
                showSnackBar(getString(R.string.failed_to_save_data))
                binding.profileEidtProgressBar.isVisible = false
            }
            is ProfileEditUiState.Loading -> {
                binding.toolbarApply.isEnabled = false
                binding.profileEidtProgressBar.isVisible = true
            }
        }
    }

    private fun initViewModel() {
        viewModel.uuid = userDetail.uuid
        viewModel.name = userDetail.name
        viewModel.introduce = userDetail.introduce
    }

    private fun initToolbar() {
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(binding.toolBar)

        activity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.profile_edit_title)
        }

        binding.toolbarApply.setOnClickListener {
            if (viewModel.isChanged)
                viewModel.sendChangedInfo()
            else {
                closeFragment()
            }
        }
    }

    private fun initFragment() {
        binding.apply {
            profileEditName.setText(userDetail.name)
            profileEditIntroduce.setText(userDetail.introduce)
            profileEditImage.setOnClickListener {
                onClickImage()
            }

            profileEditName.addTextChangedListener {
                viewModel.isChanged = true
                if (it != null) {
                    viewModel.name = it.toString()
                    updateDoneButton()
                }
            }

            profileEditIntroduce.addTextChangedListener {
                viewModel.isChanged = true
                if (it != null) {
                    viewModel.introduce = it.toString()
                }
            }
            userDetail.profileImageUrl?.let { setUserImage(it) }
        }
    }

    private fun setUserImage(bitmap: Bitmap) {
        Glide.with(this@ProfileEditFragment)
            .load(bitmap)
            .fallback(R.drawable.ic_baseline_person_24)
            .circleCrop()
            .into(binding.profileEditImage)
    }

    private fun setUserImage(url: String) {
        val storageReference = Firebase.storage.reference
        Glide.with(this@ProfileEditFragment)
            .load(storageReference.child(url))
            .fallback(R.drawable.ic_baseline_person_24)
            .circleCrop()
            .into(binding.profileEditImage)
    }

    private fun updateDoneButton() {
        val canUse =
            !(viewModel.isValidName || viewModel.uiState.value is ProfileEditUiState.Loading)
        binding.toolbarApply.apply {
            isEnabled = canUse
            alpha = if (canUse) 1.0F else 0.25F
        }
    }

    private fun onClickImage() {
        MaterialAlertDialogBuilder(requireActivity())
            .setItems(R.array.image_options) { _, which ->
                when (which) {
                    0 -> {
                        showImagePicker()
                    }
                    1 -> {
                        viewModel.isChanged = true
                        viewModel.isChangedImage = true
                        viewModel.selectedImage = null
                        binding.profileEditImage.setImageDrawable(
                            AppCompatResources.getDrawable(
                                requireActivity(),
                                R.drawable.ic_baseline_person_24
                            )
                        )
                    }
                    else -> throw IllegalArgumentException()
                }
            }.create()
            .show()
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