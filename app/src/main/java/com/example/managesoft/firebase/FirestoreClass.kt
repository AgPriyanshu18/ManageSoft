package com.example.managesoft.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.managesoft.activities.ProfileActivity
import com.example.managesoft.activities.activities.MainActivity
import com.example.managesoft.activities.activities.SignInActivity
import com.example.managesoft.activities.activities.SignUpActivity
import com.example.managesoft.activities.createBoardActivity
import com.example.managesoft.model.Board
import com.example.managesoft.model.User
import com.example.managesoft.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {

    private  val mFireStore = FirebaseFirestore.getInstance()


    fun registerUser(activity : SignUpActivity , userInfo : User){
        mFireStore.collection(Constants.USERS).document(getCurrentUserId())
            .set(userInfo,SetOptions.merge()).addOnSuccessListener {
                activity.userRegisteredSuccess()
            }
    }

    fun getCurrentUserId()  :String{
        var currentUser  =FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null){
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun createBoard(activity: createBoardActivity , board : Board){
        mFireStore.collection(Constants.BOARDS).document().set(board, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName , "Board created successfully")
                Toast.makeText(activity,"Board created successfully",Toast.LENGTH_SHORT).show()
                activity.boardCreatedSuccessfully()
            }.addOnFailureListener {
                exception ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating Board",exception
                )
            }
    }

    fun updateUserProfileData(activity: ProfileActivity ,
                              userHashMap : HashMap<String , Any>){
        mFireStore.collection(Constants.USERS).document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName,"Profile data updated successfully")
                Toast.makeText(activity,"Profile updated successfully!",
                    Toast.LENGTH_SHORT).show()
                activity.profileUpdateSuccess()
            }.addOnFailureListener {
                e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,
                "Error while creating a board.",e)
                Toast.makeText(activity,"Error in updating Profile !",
                    Toast.LENGTH_SHORT).show()
            }
    }

    fun loadUserData(activity: Activity){
        mFireStore.collection(Constants.USERS).document(getCurrentUserId())
            .get().addOnSuccessListener {document ->
                val loggedInUser = document.toObject(User::class.java)

                when(activity){
                    is SignInActivity ->{
                        if (loggedInUser != null) {
                        activity.signInSuccess(loggedInUser) }
                    }
                    is MainActivity ->{
                        activity.updateNavigationUserDetails(loggedInUser!!)
                    }
                    is ProfileActivity ->{
                        activity.setMyProfileUserDetails(loggedInUser!!)
                    }
                }

            }.addOnFailureListener{

                e->
                when(activity){
                    is SignInActivity ->{
                            activity.hideProgressDialog()
                    }
                    is MainActivity ->{
                        activity.hideProgressDialog()
                    }

                }
                Log.e(activity.javaClass.simpleName,"Error writing document",e)
            }
    }
}