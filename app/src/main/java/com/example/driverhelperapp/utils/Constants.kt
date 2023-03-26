package com.example.driverhelperapp.utils

import com.example.driverhelperapp.models.ErrorResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.Response

fun<T> apiRequestFlow(call: suspend () -> Response<T>): Flow<ApiResponse<T>> = flow {
    emit(ApiResponse.Loading)

    withTimeoutOrNull(20000L) {
        val response = call()

        try {
            if (response.isSuccessful) {
                response.body()?.let { data ->
                    emit(ApiResponse.Success(data))
                }
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Неверный запрос"
                    401 -> "Неавторизованный"
                    403 -> "Запрещено"
                    404 -> "Не найдено"
                    500 -> "Внутренняя ошибка сервера"
                    else -> "Неизвестная ошибка"
                }
                emit(ApiResponse.Failure(errorMessage + ". " + (response.errorBody()?.string() ?: ""), response.code()))
            }
        } catch (e: Exception) {
            emit(ApiResponse.Failure(e.message ?: e.toString(), 400))
        }
    } ?: emit(ApiResponse.Failure("Тайм-аут! Пожалуйста, попробуйте еще раз.", 408))
}.flowOn(Dispatchers.IO)