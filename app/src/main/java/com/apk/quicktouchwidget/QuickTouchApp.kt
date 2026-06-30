package com.apk.quicktouchwidget

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for QuickTouch Widget.
 * Annotated with [@HiltAndroidApp] to enable Hilt dependency injection
 * and trigger code generation for the base application class.
 *
 * This class serves as the entry point for the Hilt DI graph and must
 * be referenced in AndroidManifest.xml via `android:name=".QuickTouchApp"`.
 */
@HiltAndroidApp
class QuickTouchApp : Application()
