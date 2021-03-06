package com.ycrsky.appassistant

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ycrsky.appassistant.wechat.constants.SpKeys
import com.ycrsky.appassistant.wechat.util.SP
import kotlinx.android.synthetic.main.activity_input_delay_millis.*


class InputDelayMillisActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_delay_millis)

        val configDelayMillis = SP.getInstance().getLong(SpKeys.DELAY_OPEN_RED_ENVELOPES)
        if (configDelayMillis > 0L) {
            etInput.setText("$configDelayMillis")
            etInput.setSelection(etInput.length())
        }

        tvSave.setOnClickListener {
            val delayMillis = etInput.text?.toString()?.toLong()
            if (delayMillis == null) {
                Toast.makeText(this, "请输入延迟毫秒数", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            SP.getInstance().putLong(SpKeys.DELAY_OPEN_RED_ENVELOPES, delayMillis)
            finish()
        }
    }
}