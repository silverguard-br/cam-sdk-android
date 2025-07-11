package com.silverguard.cam.ui.home

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
import com.silverguard.cam.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModel()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initScreen()
    }

    private fun initScreen() {
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
                        is HomeUiState.Loading -> showLoading(true)
                        is HomeUiState.Success -> navigateToWebView(state.url)
                        is HomeUiState.Error -> showError(true)
                    }
                }
            }
        }

        setupButtons()
    }

    private fun setupButtons() {
        with(binding) {
            btnAlert.setOnClickListener {
                requireActivity().finish()
            }
            toolbarAlert.setNavigationOnClickListener {
                requireActivity().finish()
            }
        }
    }

    private fun showError(show: Boolean) {
        binding.clError.isVisible = show
    }

    private fun navigateToWebView(url: String) {
        showLoading(false)
        val action = HomeFragmentDirections.actionHomeFragmentToWebViewFragment(url)
        findNavController().navigate(action)
    }

    private fun showLoading(show: Boolean) {
        binding.llProgress.isVisible = show
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}