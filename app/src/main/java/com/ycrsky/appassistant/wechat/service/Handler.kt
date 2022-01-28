package com.ycrsky.appassistant.wechat.service

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.ycrsky.appassistant.wechat.service.RedEnvelopesService
import java.lang.ref.WeakReference


class Handler(service: RedEnvelopesService) : Handler(Looper.getMainLooper()) {

    companion object {
        const val OPEN_WECHAT_RED_ENVELOPES = 0x0001
    }

    private val service: RedEnvelopesService? = WeakReference(service).get()

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        when (msg.what) {
            OPEN_WECHAT_RED_ENVELOPES -> {
                service?.findCloseRedEnvelopesCover()
            }
        }
    }
}