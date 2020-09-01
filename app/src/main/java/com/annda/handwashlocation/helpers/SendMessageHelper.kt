package com.annda.handwashlocation.helpers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telephony.SmsManager

object SendMessageHelper {

    fun sendViaApi(phoneNumber: String, message: String){
        val smsManager: SmsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null)
    }

    fun sendViaIntent(context: Context, phoneNumber: String, message: String){
        val intent = Intent(Intent.ACTION_SEND, Uri.parse("smsto:$phoneNumber"))
        intent.putExtra("sms_body", message)
        context.startActivity(intent)
    }
}