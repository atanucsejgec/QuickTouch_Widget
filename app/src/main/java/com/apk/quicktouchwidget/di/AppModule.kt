package com.apk.quicktouchwidget.di

import android.app.admin.DevicePolicyManager
import android.content.Context
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing system service dependencies as singletons.
 * Installed in the [SingletonComponent] so these services are available
 * throughout the entire application lifecycle.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides the system [AudioManager] for volume control operations.
     *
     * @param context The application context injected by Hilt
     * @return The [AudioManager] system service instance
     */
    @Provides
    @Singleton
    fun provideAudioManager(@ApplicationContext context: Context): AudioManager {
        return context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    /**
     * Provides the system [CameraManager] for flashlight control operations.
     *
     * @param context The application context injected by Hilt
     * @return The [CameraManager] system service instance
     */
    @Provides
    @Singleton
    fun provideCameraManager(@ApplicationContext context: Context): CameraManager {
        return context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    /**
     * Provides the system [DevicePolicyManager] for device admin operations
     * such as screen lock.
     *
     * @param context The application context injected by Hilt
     * @return The [DevicePolicyManager] system service instance
     */
    @Provides
    @Singleton
    fun provideDevicePolicyManager(@ApplicationContext context: Context): DevicePolicyManager {
        return context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    }
}
