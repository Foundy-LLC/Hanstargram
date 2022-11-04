package io.foundy.hanstargram.view.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import io.foundy.hanstargram.R
import io.foundy.hanstargram.base.ViewBindingActivity
import io.foundy.hanstargram.databinding.ActivityHomeBinding

class HomeActivity : ViewBindingActivity<ActivityHomeBinding>() {

    private val viewModel: HomeViewModel by viewModels()

    override val bindingInflater: (LayoutInflater) -> ActivityHomeBinding
        get() = ActivityHomeBinding::inflate

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, HomeActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val nhf =
            supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment
        val navController = nhf.navController
        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(
                R.id.postListFragment
            ),
            fallbackOnNavigateUpListener = ::onSupportNavigateUp
        )

        binding.apply {
            toolBar.setupWithNavController(navController, appBarConfiguration)
            // TODO(민성): 검색 버튼으로 바꾸기
            toolBar.inflateMenu(R.menu.menu_home_bottom_navigation)
            toolBar.setOnMenuItemClickListener(::onMenuItemClick)
            bottomNav.setupWithNavController(navController)
        }
    }

    private fun onMenuItemClick(item: MenuItem): Boolean {
        // TODO(민성): 게시글 수정 삭제 bottom sheet 보이도록 수정
        return false
    }

}