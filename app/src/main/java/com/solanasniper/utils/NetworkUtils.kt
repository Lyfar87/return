package com.solanasniper.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object NetworkUtils {

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    suspend fun isInternetAvailable(
        context: Context,
        checkWithPing: Boolean = false
    ): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as? ConnectivityManager ?: return false

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkConnectionModern(connectivityManager, checkWithPing)
        } else {
            checkConnectionLegacy(connectivityManager, checkWithPing)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private suspend fun checkConnectionModern(
        cm: ConnectivityManager,
        checkWithPing: Boolean
    ): Boolean {
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ->
                !checkWithPing || pingNetwork()

            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ->
                !checkWithPing || pingNetwork()

            else -> false
        }
    }

    @Suppress("DEPRECATION")
    private suspend fun checkConnectionLegacy(
        cm: ConnectivityManager,
        checkWithPing: Boolean
    ): Boolean {
        val activeNetwork = cm.activeNetworkInfo ?: return false
        return activeNetwork.isConnected &&
                (!checkWithPing || pingNetwork())
    }

    private suspend fun pingNetwork(): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val url = URL("https://1.1.1.1") // Cloudflare DNS
                val connection = url.openConnection() as HttpURLConnection
                connection.setRequestProperty("User-Agent", "NetworkUtils")
                connection.connectTimeout = 1500
                connection.readTimeout = 1500
                val result = connection.responseCode == 200
                connection.disconnect() // Ensure connection is closed
                result
            }
        } catch (e: IOException) {
            false
        }
    }
}
