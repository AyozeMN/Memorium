package com.ayoze.memorium.repository

import android.util.Log
import com.ayoze.memorium.model.Roles
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val listener: OnAuthActionPerformedListener) {

    interface OnAuthActionPerformedListener {
        fun onUserAuthenticationPerformed(uId: String, email: String)
        fun onUserRegisteredPerformed(uId: String, email: String, rol: Roles)
        fun onError(errorMessage: String)
    }

    suspend fun registerUser(email: String, password: String, rol: Roles) {
        withContext(Dispatchers.IO) {
            try {
                var uId = ""
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        uId = it.user?.uid.toString()
                        Log.i(TAG, "UID es este -> $uId")
                        Log.i(TAG, "Usuario registrado $uId")
                        listener.onUserRegisteredPerformed(uId, email, rol)

                    }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    listener.onError(e.message ?: "Error registrando usuario.")
                }
            }
        }
    }

    suspend fun authenticateUser(email: String, password: String) {
        Log.i(TAG, "2.- INICIANDO AUTENTICACIÓN....")
        withContext(Dispatchers.IO) {
            try {
                Log.i(TAG, "3.- TRY AUTENTICACIÓN....")
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.i(TAG, "4.- UID ANTES LLAMAR null")
                            val uId = FirebaseAuth.getInstance().currentUser?.uid.toString()
                            Log.i(TAG, "5.- UID DSPS LLAMAR $uId")
                            listener.onUserAuthenticationPerformed(uId, email)
                        } else {
                            // Manejar error de autenticación aquí si es necesario
                            listener.onError("Error logeando usuario.")
                        }
                    }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    listener.onError(e.message ?: "Error logeando usuario.")
                }
            }
        }
    }

    companion object {
        private const val TAG = "AuthRepository"
    }

}