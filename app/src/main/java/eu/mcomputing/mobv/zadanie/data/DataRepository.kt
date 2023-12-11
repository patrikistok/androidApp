package eu.mcomputing.mobv.zadanie.data

import android.content.Context
import android.util.Log
import eu.mcomputing.mobv.zadanie.data.api.ApiService
import eu.mcomputing.mobv.zadanie.data.api.model.GeofenceUpdateRequest
import eu.mcomputing.mobv.zadanie.data.api.model.UserChangePasswordRequest
import eu.mcomputing.mobv.zadanie.data.api.model.UserLoginRequest
import eu.mcomputing.mobv.zadanie.data.api.model.UserRegistrationRequest
import eu.mcomputing.mobv.zadanie.data.api.model.UserResetPasswordRequest
import eu.mcomputing.mobv.zadanie.data.db.AppRoomDatabase
import eu.mcomputing.mobv.zadanie.data.db.entities.GeofenceEntity
import eu.mcomputing.mobv.zadanie.data.db.entities.UserEntity
import eu.mcomputing.mobv.zadanie.data.model.User
import eu.mcomputing.mobv.zadanie.data.db.LocalCache
import java.io.IOException

class DataRepository private constructor(
    private val service: ApiService,
    private val cache: LocalCache
) {
    companion object {
        const val TAG = "DataRepository"

        @Volatile
        private var INSTANCE: DataRepository? = null
        private val lock = Any()

        fun getInstance(context: Context): DataRepository =
            INSTANCE ?: synchronized(lock) {
                INSTANCE
                    ?: DataRepository(
                        ApiService.create(context),
                        LocalCache(AppRoomDatabase.getInstance(context).appDao())
                    ).also { INSTANCE = it }
            }
    }

    suspend fun apiRegisterUser(
        username: String,
        email: String,
        password: String,
        repeat_password: String
    ): Pair<String, User?> {
        if (username.isEmpty()) {
            return Pair("Username can not be empty", null)
        }
        if (email.isEmpty()) {
            return Pair("Email can not be empty", null)
        }
        if (password.isEmpty()) {
            return Pair("Password can not be empty", null)
        }
        if (password !== repeat_password) {
            return Pair("Repeated password is different", null)
        }
        try {
            val response = service.registerUser(UserRegistrationRequest(username, email, password))
            if (response.isSuccessful) {
                response.body()?.let { json_response ->
                    return Pair(
                        "",
                        User(
                            username,
                            email,
                            json_response.uid,
                            json_response.access,
                            json_response.refresh
                        )
                    )
                }
            }
            return Pair("Failed to create user", null)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to create user.", null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to create user.", null)
    }

    suspend fun apiLoginUser(
        username: String,
        password: String
    ): Pair<String, User?> {
        if (username.isEmpty()) {
            return Pair("Username can not be empty", null)
        }
        if (password.isEmpty()) {
            return Pair("Password can not be empty", null)
        }
        try {
            val response = service.loginUser(UserLoginRequest(username, password))
            if (response.isSuccessful) {
                response.body()?.let { json_response ->
                    if (json_response.uid == "-1") {
                        return Pair("Wrong password or username.", null)
                    }
                    return Pair(
                        "",
                        User(
                            username,
                            "",
                            json_response.uid,
                            json_response.access,
                            json_response.refresh
                        )
                    )
                }
            }
            return Pair("Failed to login user", null)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to login user.", null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to login user.", null)
    }

    suspend fun apiChangePassword(
        old_password: String,
        new_password: String,
        again_new_password: String,
    ): Pair<String, Boolean> {
        if (old_password.isEmpty()) {
            return Pair("Old password can not be empty", false)
        }
        if (new_password.isEmpty()) {
            return Pair("New password can not be empty", false)
        }
        if (new_password.isEmpty()) {
            return Pair("Repeated new password can not be empty", false)
        }
        if (!new_password.equals(again_new_password)) {
            return Pair("Repeated password is different", false)
        }
        try {
            val response = service.changePassword(UserChangePasswordRequest(old_password, new_password))
            if (response.isSuccessful) {
                response.body()?.let { json_response ->
                    return Pair("Password changed successfully", true)
                }
            }
            return Pair("Failed to change password", false)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to change password.", false)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to change password", false)
    }

    suspend fun apiResetPassword(
        email: String,
    ): Pair<String, Boolean> {
        if (email.isEmpty()) {
            return Pair("Reset email can not be empty", false)
        }
        try {
            val response = service.resetPassword(UserResetPasswordRequest(email))
            Log.d("repository", response.toString())
            Log.d("repository", email)
            if (response.isSuccessful) {
                response.body()?.let { json_response ->
                    return Pair("Email with password reset sent successfully", true)
                }
            }
            return Pair("Failed to reset password", false)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to reset password.", false)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to reset password", false)
    }

    suspend fun apiGetUser(
        uid: String
    ): Pair<String, User?> {
        try {
            val response = service.getUser(uid)

            if (response.isSuccessful) {
                response.body()?.let {
                    return Pair("", User(it.name, "", it.id, "", "", it.photo))
                }
            }

            return Pair("Failed to load user", null)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to load user.", null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to load user.", null)
    }

    suspend fun apiListGeofence(): String {
        try {
            val response = service.listGeofence()

            if (response.isSuccessful) {
                response.body()?.list?.let {
                    val users = it.map {
                        UserEntity(
                            it.uid, it.name, it.updated,
                            0.0, 0.0, it.radius, it.photo
                        )
                    }
                    cache.insertUserItems(users)
                    return ""
                }
            }

            return "Failed to load users"
        } catch (ex: IOException) {
            ex.printStackTrace()
            return "Check internet connection. Failed to load users."
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return "Fatal error. Failed to load users."
    }

    fun getUsers() = cache.getUsers()

    suspend fun getUsersList() = cache.getUsersList()

    suspend fun insertGeofence(item: GeofenceEntity) {
        cache.insertGeofence(item)
        try {
            val response =
                service.updateGeofence(GeofenceUpdateRequest(item.lat, item.lon, item.radius))

            if (response.isSuccessful) {
                response.body()?.let {

                    item.uploaded = true
                    cache.insertGeofence(item)
                    return
                }
            }

            return
        } catch (ex: IOException) {
            ex.printStackTrace()
            return
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    suspend fun removeGeofence() {
        try {
            val response = service.deleteGeofence()

            if (response.isSuccessful) {
                response.body()?.let {
                    return
                }
            }

            return
        } catch (ex: IOException) {
            ex.printStackTrace()
            return
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}