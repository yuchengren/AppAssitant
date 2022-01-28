package com.ycrsky.appassistant.wechat.util

import com.ycrsky.appassistant.BaseApplication
import com.ycrsky.appassistant.wechat.constants.WeChatConfig
import com.ycrsky.appassistant.wechat.core.IWechatProvider
import com.ycrsky.appassistant.wechat.core.impl.Wechat8015ProviderImpl


class WechatManager private constructor() {

    companion object {
        @JvmStatic
        fun getInstance(): WechatManager {
            return Holder.instance
        }
    }

    val versionName: String
    val versionCode: Int
    val provider: IWechatProvider

    init {
        val packageInfo = BaseApplication.appContext.packageManager
                .getInstalledPackages(0)
                .firstOrNull { it.packageName == WeChatConfig.PACKAGE_NAME_WECHAT }
        versionName = packageInfo?.versionName ?: "unknown"
        versionCode = packageInfo?.versionCode ?: 0

        provider = when (versionCode) {
            WeChatConfig.VERSION_CODE_OF_NAME_8015 -> Wechat8015ProviderImpl()
            else -> Wechat8015ProviderImpl()
        }
    }

    private object Holder {
        val instance = WechatManager()
    }
}