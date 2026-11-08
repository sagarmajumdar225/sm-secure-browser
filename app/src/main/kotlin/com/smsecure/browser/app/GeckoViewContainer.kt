package com.smsecure.browser.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoView

@Composable
fun GeckoViewContainer(
    session: GeckoSession,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            GeckoView(context).apply {
                setSession(session)
            }
        },
        update = { view ->
            view.setSession(session)
        },
        modifier = modifier
    )
}
