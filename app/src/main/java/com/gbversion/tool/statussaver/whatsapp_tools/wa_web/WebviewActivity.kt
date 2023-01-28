package com.gbversion.tool.statussaver.whatsapp_tools.wa_web

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.*
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.webkit.*
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.gbversion.tool.statussaver.R
import com.gbversion.tool.statussaver.databinding.ActivityWaWebBinding
import com.gbversion.tool.statussaver.remote_config.RemoteConfigUtils
import com.gbversion.tool.statussaver.utils.AdsUtils
import com.gbversion.tool.statussaver.utils.NetworkState
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import gun0912.tedimagepicker.extenstion.toggle
import java.util.*

open class WebviewActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    companion object {
        private val CHROME_FULL =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36"
        private val USER_AGENT = CHROME_FULL

        private val STORAGE_PERMISSION =
            Manifest.permission.WRITE_EXTERNAL_STORAGE // "android.permission.WRITE_EXTERNAL_STORAGE"

        private val CAMERA_PERMISSION = Manifest.permission.CAMERA // "android.permission.CAMERA"

        private val AUDIO_PERMISSION =
            Manifest.permission.RECORD_AUDIO // "android.permission.RECORD_AUDIO"

        private val VIDEO_PERMISSION = arrayOf(CAMERA_PERMISSION, AUDIO_PERMISSION)

        private val WHATSAPP_HOMEPAGE_URL = "https://www.whatsapp.com/"

        private val WHATSAPP_WEB_BASE_URL = "web.whatsapp.com"
        private val WORLD_ICON = "\uD83C\uDF10"
        private val WHATSAPP_WEB_URL = ("https://" + WHATSAPP_WEB_BASE_URL
                + "/" + WORLD_ICON + "/"
                + Locale.getDefault().language)

        private val FILECHOOSER_RESULTCODE = 200
        private val CAMERA_PERMISSION_RESULTCODE = 201
        private val AUDIO_PERMISSION_RESULTCODE = 202
        private val VIDEO_PERMISSION_RESULTCODE = 203
        private val STORAGE_PERMISSION_RESULTCODE = 204

        val DEBUG_TAG = "WAWEBTOGO"
    }

    val binding by lazy { ActivityWaWebBinding.inflate(layoutInflater) }

    val activity by lazy { this@WebviewActivity }

    val mSharedPrefs by lazy {
        getSharedPreferences(
            this@WebviewActivity.packageName,
            MODE_PRIVATE
        )
    }

    private var mLastBackClick: Long = 0

    var mKeyboardEnabled = false
    var mDarkMode = false

    var mUploadMessage: ValueCallback<Array<Uri>>? = null
    private var mCurrentPermissionRequest: PermissionRequest? = null
    private var mCurrentDownloadRequest: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {
            drawer.addDrawerListener(object : DrawerListener {
                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                }

                override fun onDrawerOpened(drawerView: View) {
                }

                override fun onDrawerClosed(drawerView: View) {
                }

                override fun onDrawerStateChanged(newState: Int) {
                }
            })

            val navigationView = findViewById<NavigationView>(R.id.nav_view)
            navigationView.setNavigationItemSelectedListener(this@WebviewActivity)
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            mDarkMode = mSharedPrefs.getBoolean("darkMode", false)

            appBarMain.imgBack.setOnClickListener { drawer.toggle() }
            appBarMain.imgMore.setOnClickListener {
                showMorePopup(it)
            }

            // webview stuff
            appBarMain.contentMain.webview.setDownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
                mCurrentDownloadRequest = url
                if (checkPermission(STORAGE_PERMISSION)) {
                    appBarMain.contentMain.webview.loadUrl(
                        BlobDownloader.getBase64StringFromBlobUrl(
                            url
                        )
                    )
                    triggerDownload()
                } else {
                    requestPermission(STORAGE_PERMISSION)
                }
            }
            appBarMain.contentMain.webview.addJavascriptInterface(
                BlobDownloader(applicationContext),
                BlobDownloader.JsInstance
            )
            appBarMain.contentMain.webview.settings.javaScriptEnabled = true //for wa web
            appBarMain.contentMain.webview.settings.allowContentAccess = true // for camera
            appBarMain.contentMain.webview.settings.allowFileAccess = true
            appBarMain.contentMain.webview.settings.allowFileAccessFromFileURLs = true
            appBarMain.contentMain.webview.settings.allowUniversalAccessFromFileURLs = true
            appBarMain.contentMain.webview.settings.mediaPlaybackRequiresUserGesture =
                false //for audio messages
            appBarMain.contentMain.webview.settings.domStorageEnabled =
                true //for html5 app
            appBarMain.contentMain.webview.settings.databaseEnabled = true
            appBarMain.contentMain.webview.settings.cacheMode = WebSettings.LOAD_DEFAULT
            appBarMain.contentMain.webview.settings.loadWithOverviewMode = true
            appBarMain.contentMain.webview.settings.useWideViewPort = true
            appBarMain.contentMain.webview.settings.setSupportZoom(true)
            appBarMain.contentMain.webview.settings.builtInZoomControls = true
            appBarMain.contentMain.webview.settings.displayZoomControls = false
            appBarMain.contentMain.webview.settings.saveFormData = true
            appBarMain.contentMain.webview.settings.loadsImagesAutomatically = true
            appBarMain.contentMain.webview.settings.blockNetworkImage = false
            appBarMain.contentMain.webview.settings.blockNetworkLoads = false
            appBarMain.contentMain.webview.settings.javaScriptCanOpenWindowsAutomatically =
                true
            appBarMain.contentMain.webview.settings.setNeedInitialFocus(false)
            appBarMain.contentMain.webview.settings.setGeolocationEnabled(true)
            appBarMain.contentMain.webview.settings.layoutAlgorithm =
                WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            appBarMain.contentMain.webview.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
            appBarMain.contentMain.webview.isScrollbarFadingEnabled = true
            appBarMain.contentMain.webview.webChromeClient = object : WebChromeClient() {
                override fun onCreateWindow(
                    view: WebView,
                    dialog: Boolean,
                    userGesture: Boolean,
                    resultMsg: Message
                ): Boolean {
                    Toast.makeText(applicationContext, "OnCreateWindow", Toast.LENGTH_LONG).show()
                    return true
                }

                override fun onPermissionRequest(request: PermissionRequest) {
                    if (request.resources[0] == PermissionRequest.RESOURCE_VIDEO_CAPTURE) {
                        if (ContextCompat.checkSelfPermission(
                                activity,
                                CAMERA_PERMISSION
                            ) == PackageManager.PERMISSION_DENIED
                            && ContextCompat.checkSelfPermission(
                                activity,
                                AUDIO_PERMISSION
                            ) == PackageManager.PERMISSION_DENIED
                        ) {
                            ActivityCompat.requestPermissions(
                                activity,
                                VIDEO_PERMISSION,
                                VIDEO_PERMISSION_RESULTCODE
                            )
                            mCurrentPermissionRequest = request
                        } else if (ContextCompat.checkSelfPermission(
                                activity,
                                CAMERA_PERMISSION
                            ) == PackageManager.PERMISSION_DENIED
                        ) {
                            ActivityCompat.requestPermissions(
                                activity,
                                arrayOf(CAMERA_PERMISSION),
                                CAMERA_PERMISSION_RESULTCODE
                            )
                            mCurrentPermissionRequest = request
                        } else if (ContextCompat.checkSelfPermission(
                                activity,
                                AUDIO_PERMISSION
                            ) == PackageManager.PERMISSION_DENIED
                        ) {
                            ActivityCompat.requestPermissions(
                                activity,
                                arrayOf(AUDIO_PERMISSION),
                                AUDIO_PERMISSION_RESULTCODE
                            )
                            mCurrentPermissionRequest = request
                        } else {
                            request.grant(request.resources)
                        }
                    } else if (request.resources[0] == PermissionRequest.RESOURCE_AUDIO_CAPTURE) {
                        if (ContextCompat.checkSelfPermission(
                                activity,
                                AUDIO_PERMISSION
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            request.grant(request.resources)
                        } else {
                            ActivityCompat.requestPermissions(
                                activity,
                                arrayOf(AUDIO_PERMISSION),
                                AUDIO_PERMISSION_RESULTCODE
                            )
                            mCurrentPermissionRequest = request
                        }
                    } else {
                        try {
                            request.grant(request.resources)
                        } catch (e: RuntimeException) {
                            Log.d(DEBUG_TAG, "Granting permissions failed", e)
                        }
                    }
                }

                override fun onConsoleMessage(cm: ConsoleMessage): Boolean {
                    Log.d(DEBUG_TAG, "WebView console message: " + cm.message())
                    return super.onConsoleMessage(cm)
                }

                override fun onShowFileChooser(
                    webView: WebView,
                    filePathCallback: ValueCallback<Array<Uri>>,
                    fileChooserParams: FileChooserParams
                ): Boolean {
                    mUploadMessage = filePathCallback
                    val chooserIntent = fileChooserParams.createIntent()
                    this@WebviewActivity.startActivityForResult(
                        chooserIntent,
                        FILECHOOSER_RESULTCODE
                    )
                    return true
                }
            }
            appBarMain.contentMain.webview.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)

                    view?.let {
                        setContentSize(it)
                        if (mDarkMode) {
                            addDarkMode(it)
                        }
                    }
                }

                override fun onPageCommitVisible(view: WebView?, url: String?) {
                    super.onPageCommitVisible(view, url)

                    view?.let {
                        setContentSize(it)
                        if (mDarkMode) {
                            addDarkMode(it)
                        }
                    }
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)

                    view?.let {
                        it.scrollTo(0, 0)
                        setContentSize(it)
                        if (mDarkMode) {
                            addDarkMode(it)
                        }
                    }
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    request?.let {
                        val url = it.url
                        Log.d(DEBUG_TAG, url.toString())
                        if (url.toString() == WHATSAPP_HOMEPAGE_URL) {
                            // when whatsapp somehow detects that waweb is running on a phone (e.g. trough
                            // the user agent, but apparently somehow else), it automatically redicts to the
                            // WHATSAPP_HOMEPAGE_URL. It's higly unlikely that a user wants to visit the
                            // WHATSAPP_HOMEPAGE_URL from within waweb.
                            // -> block the request and reload waweb
                            showToast("WA Web has to be reloaded to keep the app running")
                            loadWhatsapp()
                            return true
                        } else if (url.host == WHATSAPP_WEB_BASE_URL) {
                            // whatsapp web request -> fine
                            return super.shouldOverrideUrlLoading(view, request)
                        } else {
                            val intent = Intent(Intent.ACTION_VIEW, url)
                            startActivity(intent)
                            return true
                        }
                    }

                    return super.shouldOverrideUrlLoading(view, request)
                }

                @RequiresApi(api = Build.VERSION_CODES.M)
                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    val msg = String.format("Error: %s - %s", error?.errorCode, error?.description)
                    Log.d(DEBUG_TAG, msg)
                }

                override fun onUnhandledKeyEvent(view: WebView?, event: KeyEvent?) {
                    Log.d(DEBUG_TAG, "Unhandled key event: $event")
                }
            }
            if (savedInstanceState == null) {
                loadWhatsapp()
            } else {
                Log.d(DEBUG_TAG, "savedInstanceState is present")
            }
            appBarMain.contentMain.webview.settings.userAgentString = USER_AGENT
        }

        if (NetworkState.isOnline()) {
            loadInterstitialAd(this@WebviewActivity, RemoteConfigUtils.adIdInterstital())
        }
    }

    private fun showMorePopup(v: View) {
        val popupMenu = PopupMenu(this, v)
        popupMenu.menuInflater.inflate(R.menu.action_bar, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.toggle_keyboard -> toggleKeyboard()
                R.id.scroll_left -> {
                    showToast("scroll left")
                    runOnUiThread { binding.appBarMain.contentMain.webview.scrollTo(0, 0) }
                }
                R.id.scroll_right -> {
                    showToast("scroll right")
                    runOnUiThread { binding.appBarMain.contentMain.webview.scrollTo(2000, 0) }
                }
            }
            return@setOnMenuItemClickListener true
        }
        popupMenu.show()
    }

    override fun onResume() {
        super.onResume()
        binding.appBarMain.contentMain.webview.onResume()
        mKeyboardEnabled = mSharedPrefs.getBoolean("keyboardEnabled", true)
        setAppbarEnabled(mSharedPrefs.getBoolean("appbarEnabled", true))
        setKeyboardEnabled(mKeyboardEnabled)
    }

    override fun onPause() {
        super.onPause()
        binding.appBarMain.contentMain.webview.onPause()
    }

    private fun checkPermission(permission: String): Boolean {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            activity,
            permission
        )
    }

    private fun requestPermission(permission: String) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(permission),
                STORAGE_PERMISSION_RESULTCODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            VIDEO_PERMISSION_RESULTCODE -> if (permissions.size == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                try {
                    mCurrentPermissionRequest!!.grant(mCurrentPermissionRequest!!.resources)
                } catch (e: RuntimeException) {
                    Log.e(DEBUG_TAG, "Granting permissions failed", e)
                }
            } else {
                showSnackbar("Permission not granted, can't use video.")
                mCurrentPermissionRequest!!.deny()
            }
            CAMERA_PERMISSION_RESULTCODE, AUDIO_PERMISSION_RESULTCODE ->                 //same same
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        mCurrentPermissionRequest!!.grant(mCurrentPermissionRequest!!.resources)
                    } catch (e: RuntimeException) {
                        Log.e(DEBUG_TAG, "Granting permissions failed", e)
                    }
                } else {
                    showSnackbar(
                        "Permission not granted, can't use " +
                                if (requestCode == CAMERA_PERMISSION_RESULTCODE) "camera" else "microphone"
                    )
                    mCurrentPermissionRequest!!.deny()
                }
            STORAGE_PERMISSION_RESULTCODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                triggerDownload()
            } else {
                showToast("Permission not granted, can't download")
                mCurrentDownloadRequest = null
            }
            else -> Log.d(
                DEBUG_TAG, "Got permission result with unknown request code " +
                        requestCode + " - " + Arrays.asList(*permissions).toString()
            )
        }
        mCurrentPermissionRequest = null
    }

    private fun triggerDownload() {
        if (null != mCurrentDownloadRequest) {
            binding.appBarMain.contentMain.webview.loadUrl(
                BlobDownloader.getBase64StringFromBlobUrl(
                    mCurrentDownloadRequest
                )
            )
        }
        mCurrentDownloadRequest = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.appBarMain.contentMain.webview.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.appBarMain.contentMain.webview.restoreState(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            FILECHOOSER_RESULTCODE -> if (data != null) {
                if (resultCode == RESULT_CANCELED || data.data == null) {
                    mUploadMessage?.onReceiveValue(null)
                } else {
                    val result = data.data
                    if (result != null) {
                        val results = arrayOf(result)
                        mUploadMessage?.onReceiveValue(results)
                    }
                }
            }
            else -> Log.d(
                DEBUG_TAG, "Got activity result with unknown request code " +
                        requestCode + " - " + data.toString()
            )
        }
    }

    private fun showPopupDialog(message: String) {
        val msg = SpannableString(message)
        Linkify.addLinks(msg, Linkify.WEB_URLS or Linkify.EMAIL_ADDRESSES)
        val builder = AlertDialog.Builder(this)
        builder.setMessage(msg)
            .setCancelable(false)
            .setPositiveButton("Ok", null)
        val alert = builder.create()
        alert.show()
        (alert.findViewById<View>(android.R.id.message) as TextView).movementMethod =
            LinkMovementMethod.getInstance()
    }

    private fun showPopupDialog(resId: Int) {
        showPopupDialog(getString(resId))
    }

    private fun showToast(msg: String) {
        runOnUiThread({ Toast.makeText(this, msg, Toast.LENGTH_LONG).show() })
    }

    private fun showSnackbar(msg: String) {
        runOnUiThread({
            val snackbar: Snackbar =
                Snackbar.make(findViewById(android.R.id.content), msg, 900)
            snackbar.setAction(
                "dismiss",
                View.OnClickListener { view: View? -> snackbar.dismiss() })
            snackbar.setActionTextColor(Color.parseColor("#075E54"))
            snackbar.show()
        })
    }

    private fun toggleKeyboard() {
        setKeyboardEnabled(!mKeyboardEnabled)
    }

    private fun setKeyboardEnabled(enable: Boolean) {
        mKeyboardEnabled = enable
        val inputMethodManager =
            activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (enable && binding.appBarMain.contentMain.layout.descendantFocusability == ViewGroup.FOCUS_BLOCK_DESCENDANTS) {
            binding.appBarMain.contentMain.layout.descendantFocusability =
                ViewGroup.FOCUS_AFTER_DESCENDANTS
            showSnackbar("Unblocking keyboard...")
            //inputMethodManager.showSoftInputFromInputMethod(activity.getCurrentFocus().getWindowToken(), 0);
        } else if (!enable) {
            binding.appBarMain.contentMain.layout.descendantFocusability =
                ViewGroup.FOCUS_BLOCK_DESCENDANTS
            binding.appBarMain.contentMain.webview.rootView.requestFocus()
            showSnackbar("Blocking keyboard...")
            inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        }
        mSharedPrefs.edit().putBoolean("keyboardEnabled", enable).apply()
    }

    private fun setAppbarEnabled(enable: Boolean) {
        val actionBar = supportActionBar
        if (actionBar != null) {
            if (enable) {
                actionBar.show()
            } else {
                actionBar.hide()
            }
            mSharedPrefs.edit().putBoolean("appbarEnabled", enable).apply()
        }
    }

    private fun toggleDarkMode() {
        val currentState = mSharedPrefs.getBoolean("darkMode", false)
        mSharedPrefs.edit().putBoolean("darkMode", !currentState).apply()
        Log.d(DEBUG_TAG, "Dark Mode Enabled: " + !currentState)
        recreate()
    }

    override fun onBackPressed() {
        //close binding.drawer if open and impl. press back again to leave
        if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
            binding.drawer.closeDrawer(GravityCompat.START)
        } else {
//            binding.appBarMain.contentMain.webview.dispatchKeyEvent(
//                KeyEvent(
//                    KeyEvent.ACTION_DOWN,
//                    KeyEvent.KEYCODE_ESCAPE
//                )
//            )
//            showToast("Click back again to close")
//            mLastBackClick = System.currentTimeMillis()
            if (!AdsUtils.clicksAlternate) {
                showFullScreenAd()
            } else {
                finish()
            }
        }
    }

    var interstitialAd: InterstitialAd? = null
    var failedToLoad = false

    fun loadInterstitialAd(
        activity: Activity,
        adId: String
    ) {
        if (!NetworkState.isOnline()) {
            return
        }

        InterstitialAd.load(
            activity,
            adId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    failedToLoad = false
                    interstitialAd = ad
//                    interstitialAd?.fullScreenContentCallback = object :
//                        FullScreenContentCallback() {
//                        override fun onAdShowedFullScreenContent() {
//
//                        }
//
//                        override fun onAdDismissedFullScreenContent() {
//                            startActivity(intent)
//                        }
//
//                        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
//                            startActivity(intent)
//                        }
//                    }
                    Log.e("TAG", "onAdLoaded: ${interstitialAd}")
                }

                override fun onAdFailedToLoad(ad: LoadAdError) {
                    failedToLoad = true
                    Log.e("TAG", "adException: ${ad.responseInfo}")
                }
            })
    }


    fun showFullScreenAd() {
        if (failedToLoad) {
            continueExec()
            return
        }
        Log.e("TAG", "showFullScreenAd: ${interstitialAd}")
        interstitialAd?.let {
            it.fullScreenContentCallback = object :
                FullScreenContentCallback() {
                override fun onAdShowedFullScreenContent() {

                }

                override fun onAdDismissedFullScreenContent() {

                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                }
            }
            continueExec()
            it.show(this@WebviewActivity)
        } ?: let {
            failedToLoad = true
            if (NetworkState.isOnline()) {
                loadInterstitialAd(this@WebviewActivity, RemoteConfigUtils.adIdInterstital())
                val pd = AdsUtils.ProgressDialogMine()
                pd.showDialog(this@WebviewActivity, "Please wait...", false)
                Handler(Looper.getMainLooper()).postDelayed({
                    pd.dismissDialog()
                    showFullScreenAd()
                }, 3000)
            } else continueExec()
        }
    }

    fun continueExec() {
        finish()
    }

    private fun loadWhatsapp() {
        binding.appBarMain.contentMain.webview.settings.userAgentString = USER_AGENT
        binding.appBarMain.contentMain.webview.loadUrl(WHATSAPP_WEB_URL)
    }

    fun addDarkMode(webview: WebView) {
        window.navigationBarColor = Color.BLACK
        window.statusBarColor = Color.BLACK
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            WebSettingsCompat.setForceDark(webview.settings, WebSettingsCompat.FORCE_DARK_ON)
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK_STRATEGY)) {
                WebSettingsCompat.setForceDarkStrategy(
                    webview.settings,
                    WebSettingsCompat.DARK_STRATEGY_PREFER_WEB_THEME_OVER_USER_AGENT_DARKENING
                )
            }
        } else {
            webview.loadUrl(
                "javascript:(" +
                        "function(){ " +
                        "try {  document.body.classList.add('dark') } catch(err) { }" +
                        "})()"
            )
        }
    }

    fun setContentSize(webview: WebView) {
        if (resources.getBoolean(R.bool.isTablet)) {
            // only change content sizes if device has a smaller screen than normally used for
            // whatsapp web
            // see https://stackoverflow.com/questions/9279111/determine-if-the-device-is-a-smartphone-or-tablet
            return
        }

        webview.loadUrl(
            ("javascript:(function(){" +
                    "  try { " +
                    "	var css = '.two > div:nth-child(4){flex: 1 0 100vmin;}.two{overflow:visible}'," +
                    "    	head = document.head || document.getElementsByTagName('head')[0]," +
                    "    	style = document.createElement('style');" +
                    "	head.appendChild(style);" +
                    "	style.type = 'text/css';" +
                    "	if (style.styleSheet){" +
                    "  		style.styleSheet.cssText = css;" +
                    "	} else {" +
                    "  		style.appendChild(document.createTextNode(css));" +
                    "	}" +
                    "} catch(err) { }" +
                    "})()")
        )
    }

    fun logout() {
        AlertDialog.Builder(this)
            .setTitle("Do you want to log out?")
            .setMessage("When logging out, you will need to scan the QR code again with your phone to connect Whatsapp Web.")
            .setPositiveButton("Yes") { dialog: DialogInterface, which: Int ->
                showSnackbar("logging out...")
                binding.appBarMain.contentMain.webview.loadUrl("javascript:localStorage.clear()")
                WebStorage.getInstance().deleteAllData()
                loadWhatsapp()
                dialog.dismiss()
            }
            .setNegativeButton("No", { dialog: DialogInterface, which: Int -> dialog.dismiss() })
            .show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId
        if (id == R.id.appbar_hide) {
            if (supportActionBar?.isShowing == true) {
                showSnackbar("hiding... swipe right to show navigation bar")
                setAppbarEnabled(false)
            } else {
                setAppbarEnabled(true)
            }
        } else if (id == R.id.nav_logout) {
            logout()
        } else if (id == R.id.nav_new) {
            //showToast("nav_new");
        } else if (id == R.id.nav_switch) {
            //showToast("nav_switch");
        } else if (id == R.id.nav_settings) {
            //showToast("nav_settings");
        } else if (id == R.id.nav_reload) {
            showSnackbar("reloading...")
            loadWhatsapp()
        } else if (id == R.id.nav_dark_mode) {
            toggleDarkMode()
        } else if (id == R.id.nav_keyboard) {
            toggleKeyboard()
        }
        binding.drawer.closeDrawer(GravityCompat.START)
        return true
    }
}