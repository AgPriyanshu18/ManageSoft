package com.example.managesoft.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.app.ActivityCompat.startActivityForResult
import com.example.managesoft.activities.ProfileActivity

object  Constants{
    const val ASSIGNED_TO : String = "assignedTo"
    const val CREATED_BY : String = "createdBy"
    const val BOARDS : String = "boards"
    const val USERS : String = "users"
    const val IMAGE : String = "image"
    const val  NAME : String = "name"
    const val MOBILE : String = "mobile"
    const val READ_STORAGE_PERMISSION_CODE = 1
    const val PICK_IMAGE_REQUEST_CODE = 2
    const val DOCUMENT_ID = "documentId"
    const val TASK_LIST = "taskList"
    const val BOARD_DETAILS: String = "board_details"
    const val ID : String = "id"
    const val EMAIL : String = "email"
    const val TASK_LIST_ITEM_POSITION : String = "task_list_item_position"
    const val CARD_LIST_ITEM_POSITION : String = "card_list_item_position"

    fun showImageChooser(activity : Activity ){

        val galleryIntent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent,PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity : Activity, uri : Uri?) : String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}