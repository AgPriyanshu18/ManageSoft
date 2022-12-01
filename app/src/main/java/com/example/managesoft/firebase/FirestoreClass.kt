package com.example.managesoft.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.managesoft.activities.ProfileActivity
import com.example.managesoft.activities.TaskListActivity
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

    fun getBoardsList(activity: MainActivity){
        mFireStore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO,getCurrentUserId()).
                get().addOnSuccessListener {
                    doc->
                Log.e(activity.javaClass.simpleName,doc.documents.toString())
                val boardsList : ArrayList<Board> = ArrayList()
                for (i in doc){
                    val board = i.toObject(Board::class.java)
                    board.documentId = i.id
                    Toast.makeText(activity,"id is ${i.id}",Toast.LENGTH_SHORT).show()
                    boardsList.add(board)
                }
                activity.boardsListInUI(boardsList)
            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName ,
                    "Error while creating View")
            }
    }

    fun updateBoardData(activity: createBoardActivity , boardHashMap: HashMap<String , Any>){
        mFireStore.collection(Constants.BOARDS).document().
        update(boardHashMap).addOnSuccessListener {
            Log.e(activity.javaClass.simpleName,"Board data updated Successfully")
            Toast.makeText(activity,"Board Created Successfully",Toast.LENGTH_SHORT).show()
            activity.boardCreatedSuccessfully()
        }.addOnFailureListener {
                e ->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName,
                "Error while creating a board.",e)
            Toast.makeText(activity,"Error in updating Board !",
                Toast.LENGTH_SHORT).show()
        }
    }

    fun addUpdateTaskList(activity : TaskListActivity , board: Board){
        val taskListHashMap = HashMap<String,Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        mFireStore.collection(Constants.BOARDS).document(board.documentId).update(taskListHashMap).
                addOnSuccessListener {
                    Log.e(activity.javaClass.simpleName,"TaskList Updated Successfully")
                    activity.addUpdateTaskListSuccess()
                }.addOnFailureListener {
                    e->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName,"Error while creating a board" , e)

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

    fun loadUserData(activity: Activity,readBoardsList : Boolean = false){
        mFireStore.collection(Constants.USERS).document(getCurrentUserId())
            .get().addOnSuccessListener {document ->
                val loggedInUser = document.toObject(User::class.java)

                when(activity){
                    is SignInActivity ->{
                        if (loggedInUser != null) {
                        activity.signInSuccess(loggedInUser) }
                    }
                    is MainActivity ->{
                        activity.updateNavigationUserDetails(loggedInUser!!,readBoardsList)
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

    fun getBoardsDetails(activity: TaskListActivity, boardDocumentId: String) {
        mFireStore.collection(Constants.BOARDS)
            .document(boardDocumentId).
            get().addOnSuccessListener {
                    doc->
                Log.e(activity.javaClass.simpleName,doc.toString())
                val board = doc.toObject(Board::class.java)
                board?.documentId = boardDocumentId
                activity.boardDetails(board!!)
            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName ,
                    "Error while creating View")
            }
    }
}