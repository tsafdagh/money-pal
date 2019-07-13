package com.kola.moneypal.utils

import android.net.Uri
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.util.*

object StorageUtil {

    val storageRef = FirebaseStorage.getInstance().reference

    /**Permet d'uploader les images dans une reference de firestore**/
    fun uploadFromLocalFile(filePath: Uri, refStorage: String, onSuccess: (String) -> Unit) {
        val file = filePath
        val riversRef = storageRef.child(
            refStorage + FirebaseAuth.getInstance().currentUser!!.phoneNumber
        )

        val uploadTask = file.let { riversRef.putFile(it) }

        uploadTask.addOnFailureListener {

        }.addOnSuccessListener {
            val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation riversRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    onSuccess(downloadUri.toString())
                } else {
                    // Handle failures
                    onSuccess("error")

                }
            }
        }
    }


    /**Permet d'uploader les images dans la reference d'image de groupe de firestore**/
    fun uploadImageGroupFromLocalFile(filePath: Uri, idGroupe: String, onSuccess: (String) -> Unit) {
        val file = filePath
        val riversRef = storageRef.child(
             "objective_groupe_image/$idGroupe"
        )

        val uploadTask = file.let { riversRef.putFile(it) }

        uploadTask.addOnFailureListener {

        }.addOnSuccessListener {
            val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation riversRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    onSuccess(downloadUri.toString())
                } else {
                    // Handle failures
                    onSuccess("error")

                }
            }
        }
    }

    /**Permet d'uploader les images dans la reference d'image de groupe de firestore**/
    fun uploadChatImageOfObjcGroup(filePath: Uri, onSuccess: (String) -> Unit) {
        val file = filePath
        val riversRef = storageRef.child(
            "objective_groupe_image/chat_images/${UUID.randomUUID()}"
        )

        val uploadTask = file.let { riversRef.putFile(it) }

        uploadTask.addOnFailureListener {

        }.addOnSuccessListener {
            val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation riversRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    onSuccess(downloadUri.toString())
                } else {
                    // Handle failures
                    onSuccess("error")

                }
            }
        }
    }

}