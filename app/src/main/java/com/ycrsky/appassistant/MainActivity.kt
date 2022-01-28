package com.ycrsky.appassistant

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.ycrsky.appassistant.wechat.constants.WechatSpKeys
import com.ycrsky.appassistant.wechat.util.SP
import com.ycrsky.appassistant.wechat.util.WechatManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		initPermissionGroup()
		initWechatGroup()
		initCommonGroup()
	}

	/**
	 * 系统权限
	 */
	private fun initPermissionGroup() {
		tvOpenAccessibilityServices.setOnClickListener { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
		tvRetrieveNotification.setOnClickListener { startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) }
	}

	/**
	 * 微信
	 */
	@SuppressLint("SetTextI18n")
	private fun initWechatGroup() {
		tvWechat.text = "微信(版本${WechatManager.getInstance().versionName}，${WechatManager.getInstance().versionCode})"

		cbWechatOpenNotification.isChecked = SP.getInstance().getBoolean(WechatSpKeys.WECHAT_AUTO_CLICK_NOTIFICATION)
		cbWechatOpenRedEnvelopesMsg.isChecked = SP.getInstance().getBoolean(WechatSpKeys.WECHAT_AUTO_CLICK_RED_ENVELOPES_MSG)
		cbWechatOpenRedEnvelopes.isChecked = SP.getInstance().getBoolean(WechatSpKeys.WECHAT_AUTO_OPEN_RED_ENVELOPES)
		cbWechatClose.isChecked = SP.getInstance().getBoolean(WechatSpKeys.WECHAT_AUTO_CLOSE_RED_ENVELOPES_DETAIL)

		cbWechatOpenNotification.setOnCheckedChangeListener { _, isChecked ->
			SP.getInstance().putBoolean(WechatSpKeys.WECHAT_AUTO_CLICK_NOTIFICATION, isChecked)
		}
		cbWechatOpenRedEnvelopesMsg.setOnCheckedChangeListener { _, isChecked ->
			SP.getInstance().putBoolean(WechatSpKeys.WECHAT_AUTO_CLICK_RED_ENVELOPES_MSG, isChecked)
		}
		cbWechatOpenRedEnvelopes.setOnCheckedChangeListener { _, isChecked ->
			SP.getInstance().putBoolean(WechatSpKeys.WECHAT_AUTO_OPEN_RED_ENVELOPES, isChecked)
		}
		cbWechatClose.setOnCheckedChangeListener { _, isChecked ->
			SP.getInstance().putBoolean(WechatSpKeys.WECHAT_AUTO_CLOSE_RED_ENVELOPES_DETAIL, isChecked)
		}
	}

	/**
	 * 通用设置
	 */
	private fun initCommonGroup() {
		tvDelayOpenRedEnvelopes.setOnClickListener { startActivity(Intent(this, InputDelayMillisActivity::class.java)) }
	}
}