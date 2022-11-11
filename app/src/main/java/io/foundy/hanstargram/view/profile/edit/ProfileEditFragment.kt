package io.foundy.hanstargram.view.profile.edit

import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
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
import io.foundy.hanstargram.view.welcome.ImageState
import io.foundy.hanstargram.view.welcome.ProfileEditViewModel
import kotlinx.coroutines.launch

class ProfileEditFragment(
    private val userDetail: UserDetail
) : ViewBindingFragment<FragmentProfileEditBinding>(){

    private val viewModel: ProfileEditViewModel by viewModels()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentProfileEditBinding
        get() = FragmentProfileEditBinding::inflate

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { imageUri ->
        val contentResolver = requireActivity().contentResolver;
        if (imageUri != null) {
            @Suppress("DEPRECATION")
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(
                        contentResolver,
                        imageUri
                    )
                )
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            }
            viewModel.imageState = ImageState.Changed
            viewModel.selectedImage = bitmap
            binding.profileEditImage.setImageBitmap(bitmap)
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
            ProfileEditUiState.SuccessToSave -> {
                showSnackBar(getString(R.string.success_to_change_profile))
                setFragmentResult("ProfileEdit", bundleOf("isChanged" to viewModel.isChanged, "uuid" to viewModel.uuid))
                parentFragmentManager.commit {
                    parentFragmentManager.popBackStack()
                }
            }
            is ProfileEditUiState.FailedToSave -> {
                showSnackBar(getString(R.string.failed_to_save_data))
            }
            else -> {}
        }
    }

    private fun initViewModel(){
        viewModel.uuid = userDetail.uuid
        viewModel.name = userDetail.name
        viewModel.introduce = userDetail.introduce
    }

    private fun initToolbar(){
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(binding.toolBar)

        activity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.profile_edit_title)
        }

        binding.toolbarApply.setOnClickListener {
            if(binding.profileEditName.text.toString().isEmpty()){
                showSnackBar(getString(R.string.cant_name_is_empty))
            }
            else{
                viewModel.sendChangedInfo()
            }
        }
    }

    private fun initFragment(){
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

            val storageReference = Firebase.storage.reference
            Glide.with(this@ProfileEditFragment)
                .load(userDetail.profileImageUrl?.let { storageReference.child(it) })
                .fallback(R.drawable.ic_baseline_person_24)
                .circleCrop()
                .into(profileEditImage)
        }
    }

    private fun updateDoneButton() {
        val isLoading = viewModel.uiState.value is ProfileEditUiState.Loading
        val hasName = binding.profileEditName.text.toString().isNotEmpty()

        binding.toolbarApply.apply {
            isEnabled = hasName && !isLoading
        }
    }

    private fun onClickImage() {
        viewModel.isChanged = true
        if (viewModel.selectedImage != null) {
            MaterialAlertDialogBuilder(requireActivity())
                .setItems(R.array.image_options) { _, which ->
                    when (which) {
                        0 -> {
                            showImagePicker()
                        }
                        1 -> {
                            viewModel.imageState = ImageState.Default
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
        } else {
            showImagePicker()
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun showImagePicker() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}