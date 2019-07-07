package com.kola.moneypal.utils

import android.net.Uri
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks


object DynamicLinkUtil {

    fun generateContentLink(groupId: String): Uri {
        val baseUrl = Uri.parse("https://kola.moneypal.com/idGroup=$groupId")
        val domain = "https://workstation.page.link"

        val link = FirebaseDynamicLinks.getInstance()
            .createDynamicLink()
            .setLink(baseUrl)
            .setDomainUriPrefix(domain)
            .setIosParameters(DynamicLink.IosParameters.Builder("com.kola.monepal.moneypal").build())
            .setAndroidParameters(DynamicLink.AndroidParameters.Builder("com.kola.moneypal.").build())
            .buildDynamicLink()

        return link.uri
    }

    private fun ManualiGenerateContentLink(groupId: String): String {
        return "https://workstation.page.link/?" +
                "link=" +
                "https://kola.moneypal.com/idGroup=$groupId" +
                "&apn=" +
                "com.kola.moneypal" +
                "&st=" +
                "Share+this+App" +
                "&sd=" +
                "looking+to+learn+how+to+use+Firebase+in+Android?+this=app+is+what+you+are+looking+for." +
                "&utm_source=" +
                "AndroidApp"
    }
}