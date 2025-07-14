package com.silverguard.cam.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silverguard.cam.core.config.SilverguardCAM
import com.silverguard.cam.core.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun onAction(action: HomeUiAction) {
        when (action) {
            is HomeUiAction.Load -> loadData()
            is HomeUiAction.Retry -> loadData()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            val response = RetrofitClient.api.postMedRequest(SilverguardCAM.getRequestUrlModel())
            if (response.isSuccessful) {
                val url = response.body()?.data?.url
                if (!url.isNullOrEmpty())
                    _uiState.value = HomeUiState.Success(url)
                else
                    _uiState.value = HomeUiState.Error
            } else {
                _uiState.value = HomeUiState.Error
            }
        }
    }
}

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(val url: String) : HomeUiState()
    data object Error : HomeUiState()
}

sealed class HomeUiAction {
    data object Load : HomeUiAction()
    data object Retry : HomeUiAction()
}