package com.ycrsky.appassistant.wechat.core.impl

import com.ycrsky.appassistant.wechat.core.IWechatProvider


class Wechat8015ProviderImpl : IWechatProvider {

    override fun notificationRedEnvelopesText(): String = "[微信红包]"

    override fun msgItemContainerId(): String = "com.tencent.mm:id/atz"
    override fun msgItemRedEnvelopesFlagId(): String = "com.tencent.mm:id/tv"
    override fun msgItemRedEnvelopesInvalidId(): String = "com.tencent.mm:id/tt"

    override fun redEnvelopesCoverOpenId(): String = "com.tencent.mm:id/f4f"
    override fun redEnvelopesCoverCloseId(): String = "com.tencent.mm:id/f4e"

    override fun redEnvelopesDetailCloseId(): String = "com.tencent.mm:id/eh"
}