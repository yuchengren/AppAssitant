package com.ycrsky.appassistant.wechat.core


interface IWechatProvider {

    fun notificationRedEnvelopesText(): String

    fun msgItemContainerId(): String
    fun msgItemRedEnvelopesFlagId(): String
    fun msgItemRedEnvelopesInvalidId(): String

    fun redEnvelopesCoverOpenId(): String
    fun redEnvelopesCoverCloseId(): String

    fun redEnvelopesDetailCloseId(): String
}