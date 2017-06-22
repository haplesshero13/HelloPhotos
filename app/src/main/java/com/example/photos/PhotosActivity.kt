package com.example.photos

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log


class PhotosActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            if (shouldShowRequestPermissionRationale(
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            }

            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 999)
        } else if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED) {
            val images = getCameraImages(applicationContext)
            Log.i("YO", "Found images: ${images.size}")
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 999) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val images = getCameraImages(applicationContext)

                Log.i("YO", "Found images: ${images.size}")
            } else {
                Log.i("YO", "No permissions :(")
            }
        }
    }
}

val CAMERA_IMAGE_BUCKET_NAME = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera"
val CAMERA_IMAGE_BUCKET_ID = getBucketId(CAMERA_IMAGE_BUCKET_NAME)


fun getBucketId(path: String): String {
    return path.toLowerCase().hashCode().toString()
}

fun getCameraImages(context: Context): List<String> {
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val selection = MediaStore.Images.Media.BUCKET_ID + " = ?"
    val selectionArgs = arrayOf(CAMERA_IMAGE_BUCKET_ID)
    val cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        selection,
        selectionArgs,
        null)
    val result = ArrayList<String>(cursor.count)
    if (cursor.moveToFirst()) {
        val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        do {
            val data = cursor.getString(dataColumn)
            result.add(data)
        } while (cursor.moveToNext())
    }
    cursor.close()
    return result
}
