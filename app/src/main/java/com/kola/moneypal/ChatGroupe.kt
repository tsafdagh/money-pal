package com.kola.moneypal

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.media.RingtoneManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.kola.moneypal.RecycleView.item.ImageMessageItemGroup
import com.kola.moneypal.RecycleView.item.TextMessageItemGroup
import com.kola.moneypal.entities.ImageMessage
import com.kola.moneypal.entities.MessageType
import com.kola.moneypal.entities.ObjectiveGroup
import com.kola.moneypal.entities.TextMessage
import com.kola.moneypal.utils.FireStoreUtil
import com.kola.moneypal.utils.GobalConfig
import com.kola.moneypal.utils.StorageUtil
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.activity_chat_groupe.*
import org.jetbrains.anko.toast
import java.io.ByteArrayOutputStream
import java.util.*


private const val RC_SELECT_IMAGE = 2
class ChatGroupe : AppCompatActivity() {

    private lateinit var currentGroupeUID: String
    private var sizeOfmember: Int = 0

    private lateinit var messageListenerRegistration: ListenerRegistration
    private var shouldInitRecycleView = true
    private lateinit var messageSection: Section

    private lateinit var objGroupe: ObjectiveGroup
    private lateinit var groupId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_groupe)


        objGroupe = (intent.getSerializableExtra(GobalConfig.EXTRAT_REFERENCE_OBJ_GROUP_STRING) as ObjectiveGroup?)!!
        groupId = intent.getStringExtra(GobalConfig.EXTRAT_REFERENCE_OBJ_GROUP_ID_STRING)!!
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        currentGroupeUID = intent.getStringExtra(GobalConfig.EXTRAT_REFERENCE_OBJ_GROUP_ID_STRING)!!

        supportActionBar?.title =objGroupe.groupeName

        messageListenerRegistration =
            FireStoreUtil.addGroupeChatMessagesListener("", currentGroupeUID, this, onListner = {
                updateRecycleView(it)
            })
        imageView_send_groupe.setOnClickListener {
            var textMessage = editText_message_groupe.text.toString()
/*            if (Configuration.istranslateMessaActived) {
                val progressdialog = indeterminateProgressDialog("Traduction en cours...")
                FirebaseMlKitUtil.translateToAnyLanguage(
                    textMessage,
                    Configuration.oldLanguage,
                    Configuration.translete_language,
                    onComplete = { stransletedMessage: String ->
                        if (stransletedMessage == "-1") {
                            toast("Traduction échouée")
                            progressdialog.dismiss()
                        } else {
                            progressdialog.dismiss()
                            val messageText = TextMessage(
                                stransletedMessage,
                                Calendar.getInstance().time,
                                FirebaseAuth.getInstance().currentUser!!.uid, MessageType.TEXT
                            )
                            editText_message_groupe.setText("")
                            FireStoreUtil.sendGroupeMessage(messageText, currentGroupeUID)
                        }
                    })*/

            // } else {
            val messageText = TextMessage(
                textMessage,
                Calendar.getInstance().time,
                FirebaseAuth.getInstance().currentUser!!.phoneNumber!!, MessageType.TEXT
            )
            editText_message_groupe.setText("")
            FireStoreUtil.sendGroupeMessage(messageText, currentGroupeUID)
            //}
        }

        fab_send_image_groupe.setOnClickListener {

            val intent = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
            }

            startActivityForResult(Intent.createChooser(intent, "Sélectionner une image"), RC_SELECT_IMAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK &&
            data != null && data.data != null
        ) {
            val selectedimagePath = data.data
            val selectedImageBmp = MediaStore.Images.Media.getBitmap(contentResolver, selectedimagePath)
            val outputStream = ByteArrayOutputStream()

            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)

            StorageUtil.uploadChatImageOfObjcGroup(selectedimagePath!!,onSuccess = { imagepath: String ->
                val messageToSend = ImageMessage(
                    imagepath, Calendar.getInstance().time,
                    FirebaseAuth.getInstance().currentUser!!.phoneNumber!!
                )

                FireStoreUtil.sendGroupeMessage(messageToSend, currentGroupeUID)
            })
        }
    }

    private fun updateRecycleView(messages: List<Item>) {
        fun init() {
            recycler_view_messages_groupe.apply {
                layoutManager = LinearLayoutManager(this@ChatGroupe)
                adapter = GroupAdapter<ViewHolder>().apply {
                    messageSection = Section(messages)
                    this.add(messageSection)
                }
            }

            shouldInitRecycleView = false
        }

        fun updateItem() = messageSection.update(messages)

        if (shouldInitRecycleView)
            init()
        else {
            updateItem()
            // on joue le sons de la notification
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(applicationContext, notification)
            r.play()
        }

        recycler_view_messages_groupe.scrollToPosition((recycler_view_messages_groupe.adapter?.itemCount ?: 1) - 1)
    }

}
