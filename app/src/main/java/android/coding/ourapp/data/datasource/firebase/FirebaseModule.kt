package android.coding.ourapp.data.datasource.firebase

import android.coding.ourapp.data.repository.asesment.AssessmentRepository
import android.coding.ourapp.data.repository.asesment.AssessmentRepositoryImpl
import android.coding.ourapp.data.repository.auth.AuthRepository
import android.coding.ourapp.data.repository.auth.AuthRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun firebaseAuth():FirebaseAuth{
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun providesAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl



    @Provides
    @Singleton
    fun firebaseReference():FirebaseDatabase = FirebaseDatabase.getInstance()

    @Provides
    @Singleton
    fun providesAssessmentRepository(impl: AssessmentRepositoryImpl): AssessmentRepository = impl







}