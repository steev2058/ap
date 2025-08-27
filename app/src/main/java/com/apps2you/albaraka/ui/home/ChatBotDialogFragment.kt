package com.apps2you.albaraka.ui.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.animation.doOnRepeat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.apps2you.albaraka.R
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatBotDialogFragment : DialogFragment() {

    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var micBtn: ImageButton
    private lateinit var chatMessages: LinearLayout
    private val REQUEST_CODE_SPEECH_INPUT = 1
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_chatbot)
        val closeButton = dialog.findViewById<ImageButton>(R.id.btnClose)
        closeButton.setOnClickListener {
            dismiss()  // Close the chat dialog
        }
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setGravity(Gravity.BOTTOM)

        dialog.window?.setBackgroundDrawableResource(R.drawable.bg_chatbot)
        dialog.window?.setElevation(8f)


        messageInput = dialog.findViewById(R.id.etMessage)
        sendButton = dialog.findViewById(R.id.btnSend)
        chatMessages = dialog.findViewById(R.id.chatMessages)
        micBtn= dialog.findViewById(R.id.btnMic)




        sendButton.setOnClickListener {
            val question = messageInput.text.toString()
            if (question.isNotBlank()) {
//                addMessageToChat(question, isUser = true)
                sendMessageToBot(question)
                messageInput.setText("")
            }
        }
        micBtn.setOnClickListener {
            startVoiceInput()
        }
        val splashLogo = dialog.findViewById<ImageView>(R.id.splashLogo)

// عرض اللوجو
        splashLogo.visibility = View.VISIBLE
        val waveAnim = android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.wave_anim)
        splashLogo.startAnimation(waveAnim)

        splashLogo.postDelayed({
            splashLogo.visibility = View.GONE

            // 🚀 جلب الترحيب والأسئلة من API
            loadBotParameters()

        }, 1500)


// عند الضغط على زر الإرسال، حذف Placeholder إذا موجود
        sendButton.setOnClickListener {
            val question = messageInput.text.toString()
            if (question.isNotBlank()) {
                // حذف Placeholder إذا موجود
                val firstChild = chatMessages.getChildAt(0)
                if (firstChild?.tag == "placeholder") {
                    chatMessages.removeView(firstChild)
                }

                sendMessageToBot(question)
                messageInput.setText("")
            }
        }
        return dialog
    }

    private fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,  Locale("ar", "SY"))
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "تحدث الآن...")
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "الميزة غير مدعومة في جهازك", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!result.isNullOrEmpty()) {
                messageInput.setText(result[0])
            }
        }
    }
    private fun addMessageToChat(message: String, isUser: Boolean) {
        val messageLayout = LinearLayout(context)
        messageLayout.orientation = LinearLayout.VERTICAL
        messageLayout.setPadding(8, 4, 8, 4)

        val textView = TextView(context)
        textView.text = message
        textView.textSize = 16f
        textView.setPadding(16, 8, 16, 8)
        textView.background = ContextCompat.getDrawable(
            requireContext(),
            if (isUser) R.drawable.bg_user_message else R.drawable.bg_bot_message
        )
        // ✅ make links clickable
        textView.autoLinkMask = Linkify.WEB_URLS
        textView.movementMethod = LinkMovementMethod.getInstance()

        if (!isUser) {
            // Add bot avatar
            val avatar = ImageView(context)
            avatar.setImageResource(R.drawable.ic_albaraka_logo) // add your bot image to res/drawable
            val avatarSize = resources.getDimensionPixelSize(R.dimen.avatar_size)
            val layoutParams = LinearLayout.LayoutParams(avatarSize, avatarSize)
            avatar.layoutParams = layoutParams
            avatar.setPadding(4, 4, 8, 4)
            messageLayout.addView(avatar)
        }
        val timeView = TextView(context)
        timeView.text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        timeView.textSize = 10f
        timeView.setTextColor(Color.GRAY)
        timeView.gravity = if (isUser) Gravity.END else Gravity.START

        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.gravity = if (isUser) Gravity.END else Gravity.START
        params.setMargins(8, 8, 8, 8)

        textView.layoutParams = params
        timeView.layoutParams = params

        messageLayout.addView(textView)
        messageLayout.addView(timeView)

        chatMessages.addView(messageLayout)

// ✨ تطبيق الأنيميشن على كل رسالة جديدة
        val anim = android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.message_anim)
        messageLayout.startAnimation(anim)


        // Scroll to bottom automatically
        val scrollView = dialog?.findViewById<ScrollView>(R.id.scrollView)
        scrollView?.post {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }


    }
    private fun addTypingMessage(): View {
        val typingLayout = LinearLayout(context)
        typingLayout.orientation = LinearLayout.HORIZONTAL
        typingLayout.gravity = Gravity.START
        typingLayout.setPadding(8, 8, 8, 8)

        val dot1 = TextView(context)
        val dot2 = TextView(context)
        val dot3 = TextView(context)

        listOf(dot1, dot2, dot3).forEach { dot ->
            dot.text = "."
            dot.textSize = 24f
            dot.setTextColor(Color.GRAY)
            dot.setPadding(4, 0, 4, 0)
            typingLayout.addView(dot)
        }

        // Animation: make dots blink
        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(
            ObjectAnimator.ofFloat(dot1, "alpha", 0f, 1f).setDuration(1200),
            ObjectAnimator.ofFloat(dot2, "alpha", 0f, 1f).setDuration(1200),
            ObjectAnimator.ofFloat(dot3, "alpha", 0f, 1f).setDuration(1200)
        )
        animatorSet.doOnRepeat {ValueAnimator.INFINITE}
        animatorSet.start()

        chatMessages.addView(typingLayout)

        // Scroll down
        val scrollView = dialog?.findViewById<ScrollView>(R.id.scrollView)
        scrollView?.post {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }

        return typingLayout
    }

    private fun removeTypingMessage(view: View) {
        chatMessages.removeView(view)
    }


    private fun showSuggestedQuestions(array: JSONArray) {
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        for (i in 0 until array.length()) {
            val question = array.optString(i)
            val btn = Button(requireContext()).apply {
                text = question
                textSize = 14f
                setPadding(16, 8, 16, 8)
                background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_suggested_question)
                setOnClickListener {
                    // عند الضغط على سؤال -> نحذف القائمة ونرسله
                    chatMessages.removeView(container)
                    sendMessageToBot(question)
                }
            }
            container.addView(btn)
        }

        chatMessages.addView(container)

        val scrollView = dialog?.findViewById<ScrollView>(R.id.scrollView)
        scrollView?.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
    }

    private fun loadBotParameters() {
        val request = Request.Builder()
            .url("https://chatbot.albarakasyria.com:3001/v1/parameters")
            .addHeader("Authorization", "Bearer app-YCk9WxCaKgUgb9rSZRIsTzO4")
            .get()
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    addMessageToChat("⚠️ فشل جلب إعدادات البوت", isUser = false)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (!body.isNullOrEmpty()) {
                    try {
                        val json = JSONObject(body)
                        val openingStatement = json.optString("opening_statement")
                        val suggested = json.optJSONArray("suggested_questions")

                        activity?.runOnUiThread {
                            // ✨ عرض الرسالة الترحيبية
                            addMessageToChat(openingStatement, isUser = false)

                            // ✨ عرض الأسئلة المقترحة كـ Buttons
                            if (suggested != null && suggested.length() > 0) {
                                showSuggestedQuestions(suggested)
                            }
                        }
                    } catch (e: Exception) {
                        activity?.runOnUiThread {
                            addMessageToChat("خطأ بالبارسنج: ${e.message}", isUser = false)
                        }
                    }
                }
            }
        })
    }

    private fun sendMessageToBot(question: String) {
        try {
            addMessageToChat(question, isUser = true)

            // Show "Bot is typing..." message
            val typingView = addTypingMessage()

            val jsonBody = JSONObject().apply {
                put("inputs", JSONObject())             // ✅ empty object {}
                put("query", question)                  // الاستعلام
                put("response_mode", "blocking")        // ✅ blocking mode
                put("conversation_id", "")              // optional
                put("user", "abc-123")
                put("files", JSONArray())               // ✅ empty array []
            }

            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                jsonBody.toString()
            )

            val request = Request.Builder()
                .url("https://chatbot.albarakasyria.com:3001/v1/chat-messages") // ✅ removed stray '
                .addHeader("Authorization", "Bearer app-YCk9WxCaKgUgb9rSZRIsTzO4")
                .post(body)
                .build()

            val client = OkHttpClient()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    activity?.runOnUiThread {
                        removeTypingMessage(typingView)
                        addMessageToChat("خطأ: ${e.message}", isUser = false)
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val responseBody = response.body?.string()
                        val jsonResponse = JSONObject(responseBody ?: "{}")

                        // ✅ Parse "answer" instead of "text"
                        val botReply = jsonResponse.optString("answer", "هناك ضغط على الخدمة يرجى المحاولة لاحقا")

                        activity?.runOnUiThread {
                            removeTypingMessage(typingView)
                            addMessageToChat(botReply, isUser = false)
                        }
                    } catch (e: Exception) {
                        activity?.runOnUiThread {
                            removeTypingMessage(typingView)
                            addMessageToChat("خطأ أثناء معالجة الرد: ${e.message}", isUser = false)
                        }
                    }
                }
            })
        } catch (e: Exception) {
            activity?.runOnUiThread {
                addMessageToChat("خطأ عام: ${e.message}", isUser = false)
            }
        }
    }


}
