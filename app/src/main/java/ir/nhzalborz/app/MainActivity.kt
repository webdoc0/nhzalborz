package ir.nhzalborz.app

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.webkit.*
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    companion object {
        const val HOME = "https://nhzalborz.ir/index-app.php"
        const val HOST = "nhzalborz.ir"
        const val OFFLINE = "file:///android_asset/offline.html"
    }

    private lateinit var web: WebView
    private var fileCallback: ValueCallback<Array<Uri>>? = null

    private val pickFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
        fileCallback?.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(res.resultCode, res.data))
        fileCallback = null
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        web = WebView(this)
        setContentView(FrameLayout(this).apply { addView(web) })

        // ---- کوکی: فعال و ماندگار تا لاگین باز بماند ----
        val cm = CookieManager.getInstance()
        cm.setAcceptCookie(true)
        cm.setAcceptThirdPartyCookies(web, true)

        web.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true          // localStorage / sessionStorage
            cacheMode = WebSettings.LOAD_DEFAULT
            mediaPlaybackRequiresUserGesture = false
            allowFileAccess = true            // برای صفحه‌ی آفلاین داخل برنامه
            setSupportZoom(false)
            userAgentString = userAgentString + " NhzAlborzApp/1.0"
        }

        web.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(v: WebView, r: WebResourceRequest): Boolean {
                val u = r.url
                if (u.scheme == "file") return false
                if ((u.scheme == "https" || u.scheme == "http") && u.host?.endsWith(HOST) == true) return false
                try { startActivity(Intent(Intent.ACTION_VIEW, u)) } catch (_: ActivityNotFoundException) {}
                return true
            }

            override fun onReceivedError(v: WebView, r: WebResourceRequest, e: WebResourceError) {
                if (r.isForMainFrame) v.loadUrl(OFFLINE)   // فقط خطای صفحه‌ی اصلی
            }

            override fun onPageStarted(v: WebView, url: String?, f: Bitmap?) { cm.flush() }
            override fun onPageFinished(v: WebView, url: String?) { cm.flush() }
        }

        web.webChromeClient = object : WebChromeClient() {
            // آپلود عکس پروفایل، پیوست گفتگو و تصویر خبر
            override fun onShowFileChooser(w: WebView, cb: ValueCallback<Array<Uri>>, p: FileChooserParams): Boolean {
                fileCallback?.onReceiveValue(null)
                fileCallback = cb
                return try { pickFile.launch(p.createIntent()); true }
                catch (e: Exception) { fileCallback = null; false }
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (web.url?.startsWith("file:") == true) { finish(); return }
                if (web.canGoBack()) web.goBack() else finish()
            }
        })

        if (savedInstanceState != null) web.restoreState(savedInstanceState)
        else web.loadUrl(if (online()) HOME else OFFLINE)
    }

    @Suppress("DEPRECATION")
    private fun online(): Boolean {
        val c = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return c.activeNetworkInfo?.isConnected == true
    }

    override fun onPause() { super.onPause(); CookieManager.getInstance().flush(); web.onPause() }
    override fun onStop() { super.onStop(); CookieManager.getInstance().flush() }
    override fun onResume() { super.onResume(); web.onResume() }
    override fun onSaveInstanceState(o: Bundle) { super.onSaveInstanceState(o); web.saveState(o) }
    override fun onDestroy() { web.destroy(); super.onDestroy() }
}
