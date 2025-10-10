package com.silverguard.cam.ui.home

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.silverguard.cam.core.styles.Stylesheet
import com.silverguard.cam.databinding.FragmentCamHomeBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CamHomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModel()

    private var _binding: FragmentCamHomeBinding? = null
    private val binding get() = _binding!!
    private val camColors = Stylesheet.colors
    private val camFonts = Stylesheet.fonts

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCamHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initScreen()
    }

    private fun initScreen() {
        binding.camHomeContainer.setBackgroundColor(camColors.background)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })

        homeViewModel.onAction(HomeUiAction.Load)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.uiState.collectLatest { state ->
                    when (state) {
                        is HomeUiState.Loading -> {
                            showError(false)
                            showLoading(true)
                        }
                        is HomeUiState.Success -> {
                            showLoading(false)
                            navigateToWebView(state.url)
                        }
                        is HomeUiState.Error -> {
                            showLoading(false)
                            showError(true)
                        }
                    }
                }
            }
        }

        setupButtons()
    }

    private fun setupButtons() {
        with(binding) {
            camHomeBtnAlert.setTextColor(camColors.buttonTitle)
            camHomeBtnAlert.setBackgroundColor(camColors.buttonEnabled)
            camHomeBtnAlert.setOnClickListener {
                requireActivity().finish()
            }
            camHomeToolbarAlert.setNavigationOnClickListener {
                requireActivity().finish()
            }
        }
    }

    private fun showError(show: Boolean) {
        with(binding) {
            //Colors
            camHomeToolbarAlert.navigationIcon?.setTint(camColors.primary)
            camHomeToolbarTitle.setTextColor(camColors.primary)
            camHomeImageAlert.imageTintList =
                ColorStateList.valueOf(camColors.primary)
            camHomeTvAlertTitle.setTextColor(camColors.label)
            camHomeTvAlertSubtitle.setTextColor(camColors.label)
            //Fonts
            camHomeToolbarTitle.textSize = camFonts.button.size
            camHomeToolbarTitle.typeface = Typeface.create(Typeface.DEFAULT, camFonts.button.style)
            camHomeTvAlertTitle.textSize = camFonts.headline3.size
            camHomeTvAlertTitle.typeface = Typeface.create(Typeface.DEFAULT, camFonts.headline3.style)
            camHomeTvAlertSubtitle.textSize = camFonts.body.size
            camHomeTvAlertSubtitle.typeface = Typeface.create(Typeface.DEFAULT, camFonts.body.style)
            //Visibility
            camHomeClError.isVisible = show
        }
    }

    private fun navigateToWebView(url: String) {
        showLoading(false)
        val action = CamHomeFragmentDirections.actionHomeFragmentToWebViewFragment(url)
        findNavController().navigate(action)
    }

    private fun showLoading(show: Boolean) {
        with(binding) {
            camHomeProgressBar.indeterminateTintList =
                ColorStateList.valueOf(camColors.primary)
            camHomeTvMessage.setTextColor(camColors.label)
            camHomeTvMessage.textSize = camFonts.headline2.size
            camHomeTvMessage.typeface = Typeface.create(Typeface.DEFAULT, camFonts.headline2.style)
            camHomeLlProgress.isVisible = show
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}