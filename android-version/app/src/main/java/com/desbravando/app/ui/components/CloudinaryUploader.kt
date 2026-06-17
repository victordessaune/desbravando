package com.desbravando.app

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

private const val CLOUDINARY_CLOUD_NAME = "dopr7jbfd"
private const val CLOUDINARY_UPLOAD_PRESET = "locations_upload"

fun uploadImageToCloudinary(
    context: Context,
    uri: Uri,
    onResult: (String?) -> Unit
) {
    val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
    if (bytes == null) {
        onResult(null)
        return
    }

    val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("upload_preset", CLOUDINARY_UPLOAD_PRESET)
        .addFormDataPart("file", "cover.jpg", bytes.toRequestBody("image/*".toMediaTypeOrNull()))
        .build()

    val request = Request.Builder()
        .url("https://api.cloudinary.com/v1_1/$CLOUDINARY_CLOUD_NAME/image/upload")
        .post(requestBody)
        .build()

    OkHttpClient().newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Handler(Looper.getMainLooper()).post { onResult(null) }
        }

        override fun onResponse(call: Call, response: Response) {
            val body = response.body?.string()
            val url = body?.let { JSONObject(it).optString("secure_url") }
            Handler(Looper.getMainLooper()).post { onResult(url) }
        }
    })
}