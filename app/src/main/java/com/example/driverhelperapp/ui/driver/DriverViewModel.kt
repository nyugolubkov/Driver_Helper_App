package com.example.driverhelperapp.ui.driver

import android.text.InputFilter
import androidx.lifecycle.MutableLiveData
import com.example.driverhelperapp.BaseViewModel
import com.example.driverhelperapp.CoroutinesErrorHandler
import com.example.driverhelperapp.models.Driver
import com.example.driverhelperapp.repositories.DriverRepository
import com.example.driverhelperapp.utils.ApiResponse
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DriverViewModel @Inject constructor(
    private val driverRepository: DriverRepository,
): BaseViewModel() {

    private val _driverGet = MutableLiveData<ApiResponse<Driver>>()
    val driverGet = _driverGet

    private val _driverUp = MutableLiveData<ApiResponse<Driver>>()
    val driverUp = _driverUp

    fun getDriver(coroutinesErrorHandler: CoroutinesErrorHandler)  = baseRequest(
        _driverGet,
        coroutinesErrorHandler
    ) {
        driverRepository.getDriver()
    }

    fun updateDriver(
        driver: Driver,
        coroutinesErrorHandler: CoroutinesErrorHandler
    )  = baseRequest(
        _driverUp,
        coroutinesErrorHandler
    ) {
        driverRepository.updateDriver(driver)
    }

    fun setupOneLineInputFilter(editText: TextInputEditText) {
        editText.filters = arrayOf(InputFilter.LengthFilter(15))
    }

    fun setupMultiLineInputFilter(editText: TextInputEditText) {
        editText.filters = arrayOf(InputFilter.LengthFilter(100))
    }

    fun setupPhoneInputFilter(editText: TextInputEditText) {
        editText.filters = arrayOf(
            InputFilter.LengthFilter(16),
            InputFilter { source, start, end, _, _, _ ->
                for (i in start until end) {
                    if (source[i].isLetter()) {
                        return@InputFilter ""
                    }
                }
                null
            })
    }
}