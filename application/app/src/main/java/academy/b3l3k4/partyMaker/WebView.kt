package academy.b3l3k4.partyMaker

import android.os.Bundle
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.venue_more_info.*

class WebView: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.venue_more_info)

        val webSettings = venueMoreInfo.settings
        webSettings.javaScriptEnabled
        venueMoreInfo.loadUrl(intent.getStringExtra("URL"))
    }
}