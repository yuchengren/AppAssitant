package com.ycrsky.appassistant.wechat.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.ycrsky.appassistant.wechat.constants.SpKeys
import com.ycrsky.appassistant.wechat.constants.WeChatConfig
import com.ycrsky.appassistant.wechat.constants.WechatSpKeys
import com.ycrsky.appassistant.wechat.core.IWechatProvider
import com.ycrsky.appassistant.wechat.util.Logger
import com.ycrsky.appassistant.wechat.util.SP
import com.ycrsky.appassistant.wechat.util.WechatManager


class RedEnvelopesService : AccessibilityService() {

    private val handler = Handler(this)

    private var wechatProvider: IWechatProvider = WechatManager.getInstance().provider

    private var currentWindow: String? = null

    private var messageListActivityNodeInfo: AccessibilityNodeInfo? = null
    private var messageListActivityRedEnvelopesFilterList = ArrayList<AccessibilityNodeInfo>()
    private var clickRedEnvelopesNode: AccessibilityNodeInfo? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        Logger.d("助手已绑定")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Logger.d("助手已解绑")
        return super.onUnbind(intent)
    }

    override fun onInterrupt() {
        Logger.d("助手被中断")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return

        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                Logger.d("接收到 WINDOW_STATE_CHANGED 事件")
                onWindowStateChanged(event)
            }
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                Logger.d("接收到 WINDOW_CONTENT_CHANGED 事件")
                onWindowContentChanged()
            }
        }
    }

    private fun onWindowStateChanged(event: AccessibilityEvent) {
        currentWindow = event.className.toString()
        when (event.packageName) {
            WeChatConfig.PACKAGE_NAME_WECHAT -> {
                // 微信主界面和聊天都是LauncherUI
                if ("android.widget.LinearLayout" == currentWindow) {
                    currentWindow = WeChatConfig.ACTIVITY_NAME_MESSAGE_LIST
                }
                Logger.d("当前window: $currentWindow")
                val windowName = currentWindow ?: return

                when (windowName) {
                    WeChatConfig.ACTIVITY_NAME_RED_ENVELOPES_COVER -> handler.sendEmptyMessageDelayed(
                        Handler.OPEN_WECHAT_RED_ENVELOPES, 100L)
                }
            }
        }
    }

    private fun onWindowContentChanged() {
        val windowName = currentWindow ?: return
        when (windowName) {
            WeChatConfig.ACTIVITY_NAME_MESSAGE_LIST -> queryWechatRedEnvelopes()
            WeChatConfig.ACTIVITY_NAME_RED_ENVELOPES_DETAIL -> closeWechatRedEnvelopesDetail()
        }
    }


    /**
     * 查询微信聊天界面是否有红包节点
     */
    private fun queryWechatRedEnvelopes() {
        if (!SP.getInstance().getBoolean(WechatSpKeys.WECHAT_AUTO_CLICK_RED_ENVELOPES_MSG)) {
            Logger.d("微信自动点击红包功能已关闭，请到设置里面开启")
            return
        }

        val rootNode = rootInActiveWindow
        // 查找消息Container Node
        val messageItemContainerNodeList = rootNode.findAccessibilityNodeInfosByViewId(wechatProvider.msgItemContainerId())
        Logger.d("查找到红包消息数量: ${messageItemContainerNodeList.size}")

        if (messageItemContainerNodeList.isEmpty()) {
            return
        }

        messageItemContainerNodeList.reverse()
        for (messageItemContainerNode in messageItemContainerNodeList) {
            val parentNode = messageItemContainerNode.parent
            // 查找是否有红包已领取 TextView的显示，如果有说明
            val invalidNodeList = parentNode.findAccessibilityNodeInfosByViewId(wechatProvider.msgItemRedEnvelopesInvalidId())
            if (invalidNodeList.isNotEmpty()) {
                Logger.d("此条消息是红包消息，但是已经被领取了，忽略")
                continue
            } else {
                Logger.d("此条消息是红包消息，且未被领取，执行点击")
                val clickNode = findAndClickFirstClickableParentNode(parentNode.findAccessibilityNodeInfosByViewId(wechatProvider.msgItemRedEnvelopesFlagId())?.firstOrNull())
                if (clickNode?.isClickable == true) {
                    break
                }
            }
        }
    }

    /**
     * 部分手机不延迟获取拿到的是红包封面之前的那个dialog的root node
     */
    fun findCloseRedEnvelopesCover() {
        if (currentWindow == WeChatConfig.ACTIVITY_NAME_RED_ENVELOPES_COVER) {
            val rootNode = rootInActiveWindow
            val closeRedEnvelopesCoverNodeList = rootNode.findAccessibilityNodeInfosByViewId(wechatProvider.redEnvelopesCoverCloseId())
            if (closeRedEnvelopesCoverNodeList.isEmpty()) {
                handler.sendEmptyMessageDelayed(Handler.OPEN_WECHAT_RED_ENVELOPES, 100L)
            } else {
                openWechatRedEnvelopes()
            }
        } else {
            handler.removeMessages(Handler.OPEN_WECHAT_RED_ENVELOPES)
        }
    }

    /**
     * 打开微信红包
     */
    private fun openWechatRedEnvelopes() {
        val rootNode = rootInActiveWindow
        val openRedEnvelopesNodeList = rootNode.findAccessibilityNodeInfosByViewId(wechatProvider.redEnvelopesCoverOpenId())

        if (openRedEnvelopesNodeList.isEmpty()) {
            Logger.d("此红包已过期")
            // 关闭红包封面
            val closeRedEnvelopesCoverNodeList = rootNode.findAccessibilityNodeInfosByViewId(wechatProvider.redEnvelopesCoverCloseId())
            findAndClickFirstClickableParentNode(closeRedEnvelopesCoverNodeList.firstOrNull())
            return
        }

        if (!SP.getInstance().getBoolean(WechatSpKeys.WECHAT_AUTO_OPEN_RED_ENVELOPES)) {
            Logger.d("微信自动打开红包功能已关闭，请到设置里面开启")
            return
        }

        openRedEnvelopes(openRedEnvelopesNodeList.last())
    }

    /**
     * 微信关闭红包详情页
     */
    private fun closeWechatRedEnvelopesDetail() {
        if (!SP.getInstance().getBoolean(WechatSpKeys.WECHAT_AUTO_CLOSE_RED_ENVELOPES_DETAIL)) {
            Logger.d("微信抢完红包自动关闭界面功能已关闭，请到设置里面开启")
            return
        }
        val rootNode = rootInActiveWindow
        val closeNodeList = rootNode.findAccessibilityNodeInfosByViewId(wechatProvider.redEnvelopesDetailCloseId())
        findAndClickFirstClickableParentNode(closeNodeList.firstOrNull())
    }

    /**
     * 找第一个可点击的节点并进行点击
     */
    private fun findAndClickFirstClickableParentNode(node: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        var clickNode = node
        while (clickNode != null) {
            if (clickNode.isClickable) {
                clickNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                break
            }
            clickNode = clickNode.parent
        }
        return clickNode
    }

    /**
     * 打开红包
     */
    private fun openRedEnvelopes(nodeInfo: AccessibilityNodeInfo) {
        val delay: Long = SP.getInstance().getLong(SpKeys.DELAY_OPEN_RED_ENVELOPES)
        if (delay > 0L) {
            Logger.d("已开启延迟打开红包，将延迟$delay ms后开启红包")
            handler.postDelayed({ findAndClickFirstClickableParentNode(nodeInfo) }, delay)
        } else {
            findAndClickFirstClickableParentNode(nodeInfo)
        }
    }
}