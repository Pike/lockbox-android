/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package mozilla.lockbox.view

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.fragment_fxa_login.*
import kotlinx.android.synthetic.main.fragment_fxa_login.view.*
import kotlinx.android.synthetic.main.include_backable.view.*
import mozilla.lockbox.R
import mozilla.lockbox.presenter.FxALoginPresenter
import mozilla.lockbox.presenter.FxALoginView
import mozilla.lockbox.support.isDebug

class FxALoginFragment : BackableFragment(), FxALoginView {
    override var webViewObserver: Consumer<String?>? = null
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        presenter = FxALoginPresenter(this)
        val view = inflater.inflate(R.layout.fragment_fxa_login, container, false)
        view.webView.settings.domStorageEnabled = true
        view.webView.settings.javaScriptEnabled = true
        CookieManager.getInstance().setAcceptCookie(true)

        if (!isDebug()) {
            skipFxA.visibility = View.GONE
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.toolbar.setNavigationIcon(R.drawable.ic_close)
    }

    override fun loadURL(url: String) {
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                webViewObserver?.accept(url)

                super.onPageStarted(view, url, favicon)
            }
        }

        webView.loadUrl(url)
    }

    override val skipFxAClicks: Observable<Unit>
        get() = skipFxA.clicks()
}
