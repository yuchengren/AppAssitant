package com.ycrsky.appassistant.wechat.service

import android.annotation.SuppressLint
import android.app.Notification
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.ycrsky.appassistant.wechat.constants.WeChatConfig
import com.ycrsky.appassistant.wechat.constants.WechatSpKeys
import com.ycrsky.appassistant.wechat.core.IWechatProvider
import com.ycrsky.appassistant.wechat.util.Logger
import com.ycrsky.appassistant.wechat.util.SP
import com.ycrsky.appassistant.wechat.util.WechatManager


@SuppressLint("OverrideAbstract")
class NotificationService : NotificationListenerService() {

    private var wechatProvider: IWechatProvider = WechatManager.getInstance().provider

    override fun onListenerConnected() {
        super.onListenerConnected()
        Logger.d("通知服务已绑定")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Logger.d("通知服务已解绑")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        sbn ?: return

        when (sbn.packageName) {
            WeChatConfig.PACKAGE_NAME_WECHAT -> processWechatNotification(sbn)
        }
    }

    /**
     * 处理微信通知
     */
    private fun processWechatNotification(sbn: StatusBarNotification) {
        Logger.d("监听到微信的通知到达")
        if (!SP.getInstance().getBoolean(WechatSpKeys.WECHAT_AUTO_CLICK_NOTIFICATION)) {
            Logger.d("自动点击企业微信通知功能已关闭，请在设置里面打开")
            return
        }
        processRedEnvelopesNotification(sbn, wechatProvider.notificationRedEnvelopesText())
    }

    /**
     * 处理红包通知
     */
    private fun processRedEnvelopesNotification(sbn: StatusBarNotification, keywords: String) {
        val notification = sbn.notification
        val extras: Bundle = notification.extras
        val text = extras.getCharSequence(Notification.EXTRA_TEXT) ?: ""
        if (text.contains(keywords)) {
            Logger.d("检测到红包消息，正在打开消息")
            try {
                notification.contentIntent.send()
            } catch (e: Exception) {
            }
        }
    }
}