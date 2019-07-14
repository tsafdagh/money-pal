package com.kola.moneypal.utils

import android.net.Uri
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks


object DynamicLinkUtil {

    private val urlApk = "https://drive.google.com/file/d/1EHogwhuVe9OgTpH7As_qG8fYT7kelHvK/view?usp=sharing"

    fun generateLongLink(groupId: String): Uri {
        val baseUrl = Uri.parse("https://kola.moneypal.com/idGroup=$groupId")
        val domain = "https://tsafixpc.page.link"

        val link = FirebaseDynamicLinks.getInstance()
            .createDynamicLink()
            .setLink(baseUrl)
            .setDomainUriPrefix(domain)
            .setIosParameters(DynamicLink.IosParameters.Builder("com.kola.monepal.moneypal")
                .setFallbackUrl(Uri.parse(urlApk))
                .build())
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder("com.kola.moneypal")
                    .setFallbackUrl(Uri.parse(urlApk))
                    .build())
            .setSocialMetaTagParameters(DynamicLink.SocialMetaTagParameters.Builder()
                .setTitle("MoneyPal")
                .setDescription("Le numero 1 dans la gestion de vos transactions mobile. Nous vous" +
                        " prions de suivre ce lien")
                .setImageUrl(Uri.parse("https://firebasestorage.googleapis.com/v0/b/moneypal-7d622.appspot.com/o/logo_money_pal.jpg?alt=media&token=524d5921-057e-4e67-971f-d8bf6d896600"))
                .build())
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