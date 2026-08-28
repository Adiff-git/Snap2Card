package com.snap2card

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class — entry point for Hilt dependency injection.
 * Referenced in AndroidManifest.xml via android:name=".Snap2CardApp"
 */
@HiltAndroidApp
class Snap2CardApp : Application()
