package com.ayoze.memorium.repository

import android.util.Log
import com.ayoze.memorium.model.Roles
import com.ayoze.memorium.util.Helper
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val listener: OnUserActionPerformedListener) {

    interface OnUserActionPerformedListener {
        fun onUserCreate(uId: String, email: String, rol: Roles)
        fun onRolFetched(rol: Roles)
        fun onError(errorMessage: String)
    }

    suspend fun createUser(uId: String, email: String, rol: Roles) {
        withContext(Dispatchers.IO) {
            try {
                FirebaseFirestore.getInstance().collection("users").document(uId).set(
                    hashMapOf(
                        "email" to email, "rol" to rol
                    )
                )
                withContext(Dispatchers.Main) {
                    listener.onUserCreate(uId, email, rol)
                    Log.i(TAG, "Usuario creado en base de datos $uId")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    listener.onError(e.message ?: "Error creando usuario.")
                }
            }
        }
    }

    suspend fun getRolFromId(uId: String) {
        Log.i(TAG, "7.- GETROLFROMID... $uId")
        withContext(Dispatchers.IO) {
            try {
                Log.i(TAG, "8.- GETROLFROMID try...")
                var rol: Roles
                FirebaseFirestore.getInstance().collection("users").document(uId).get()
                    .addOnCompleteListener {
                        val rolText = it.result.get("rol") as String
                        Log.i(TAG, "9.- rol obtenido... $rolText")
                        rol = Helper().getRolFromString(rolText)
                        Log.i(TAG, "10.- rol desde string... ${rol.name}")
                        listener.onRolFetched(rol)
                    }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    listener.onError(e.message ?: "Error obteniendo rol del usuario.")
                }
            }
        }
    }

    companion object {
        private const val TAG = "UserRepository"
    }
}