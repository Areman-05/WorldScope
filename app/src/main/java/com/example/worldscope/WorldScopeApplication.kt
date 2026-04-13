package com.example.worldscope

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Punto de entrada de la aplicacion. Hilt se inicializa aqui.
 * Las llamadas de red usan OkHttp/Retrofit configurados en NetworkModule.
 */
@HiltAndroidApp
class WorldScopeApplication : Application()
