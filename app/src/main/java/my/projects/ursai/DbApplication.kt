package my.projects.ursai

import android.app.Application
import com.google.firebase.FirebaseApp

class DbApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}