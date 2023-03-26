package com.example.driverhelperapp.services

import com.example.driverhelperapp.models.Contact
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ContactApiService {
    @GET("/contact")
    suspend fun getContacts(): Response<List<Contact>>

    @POST("/contact")
    suspend fun addNewContact(
        @Body contact: Contact
    ): Response<Contact>

    @PUT("/contact/{contactId}")
    suspend fun putContact(
        @Body contact: Contact,
        @Path("contactId") contactId: Long
    ): Response<Contact>

    @DELETE("/contact/{contactId}")
    suspend fun deleteContact(@Path("contactId") contactId: Long): Response<ResponseBody>
}