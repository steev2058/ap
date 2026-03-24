package com.apps2you.albaraka.ui.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
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
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.DialogFragment
import com.apps2you.albaraka.R
import com.google.android.material.button.MaterialButton
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
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit

class ChatBotDialogFragment : DialogFragment() {

    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var micBtn: ImageButton
    private lateinit var chatMessages: LinearLayout
    private val REQUEST_CODE_SPEECH_INPUT = 1
    private var conversationId: String? = null
    private var userId: String? = null

    companion object {
        private const val PREFS_NAME = "ChatBotPrefs"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_CONVERSATION_ID = "conversation_id"
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        loadUserAndConversationIds()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_chatbot)
        val closeButton = dialog.findViewById<ImageButton>(R.id.btnClose)
        closeButton.setOnClickListener {
            dismiss()  // Close the chat dialog
        }
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setGravity(Gravity.BOTTOM)

        dialog.window?.setBackgroundDrawableResource(R.drawable.bg_chatbot)
        dialog.window?.apply {
            decorView.elevation = 8f    // يعمل من API 21 بدون مشاكل
        }


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

            if (!userId.isNullOrEmpty() && !conversationId.isNullOrEmpty()) {
                loadChatHistory()
            } else {
                loadBotParameters()
            }
        }, 1500)



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
    private fun loadUserAndConversationIds() {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        userId = prefs.getString(KEY_USER_ID, null)
        conversationId = prefs.getString(KEY_CONVERSATION_ID, null)

        // Generate new IDs if they don't exist
        if (userId.isNullOrEmpty()) {
            userId = UUID.randomUUID().toString()
            prefs.edit().putString(KEY_USER_ID, userId).apply()
        }
    }
    private fun saveConversationId(id: String) {
        conversationId = id
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_CONVERSATION_ID, id).apply()
    }

    private fun loadChatHistory() {
        if (userId.isNullOrEmpty() || conversationId.isNullOrEmpty()) return

        val url = "https://chatbot.albarakasyria.com:3001/v1/messages?user=$userId&conversation_id=$conversationId"

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer albaraka-default-key")
            .get()
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    // If history loading fails, show the bot parameters
                    loadBotParameters()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (!body.isNullOrEmpty()) {
                    try {
                        // Parse the response as JSONObject first
                        val jsonResponse = JSONObject(body)
                        // Extract the "data" array from the response
                        val jsonArray = jsonResponse.optJSONArray("data")

                        activity?.runOnUiThread {
                            // Clear any existing messages
                            chatMessages.removeAllViews()

                            // Add all messages from history if data exists
                            if (jsonArray != null && jsonArray.length() > 0) {
                                for (i in 0 until jsonArray.length()) {
                                    val messageObj = jsonArray.getJSONObject(i)
                                    val query = messageObj.optString("query", "")
                                    val answer = messageObj.optString("answer", "")

                                    // Add user message (query)
                                    if (query.isNotBlank()) {
                                        addMessageToChat(query, isUser = true)
                                    }

                                    // Add bot message (answer)
                                    if (answer.isNotBlank()) {
                                        addMessageToChat(answer, isUser = false)
                                    }
                                }

                                // Scroll to bottom
                                val scrollView = dialog?.findViewById<NestedScrollView>(R.id.scrollView)
                                scrollView?.post {
                                    scrollView.fullScroll(View.FOCUS_DOWN)
                                }
                            }

                            // Always show bot parameters after loading history (or if no history)
                            loadBotParameters()
                        }
                    } catch (e: Exception) {
                        activity?.runOnUiThread {
                            // If parsing fails, show the bot parameters
                            loadBotParameters()
                        }
                    }
                } else {
                    activity?.runOnUiThread {
                        // If no history, show the bot parameters
                        loadBotParameters()
                    }
                }
            }
        })
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
        val scrollView = dialog?.findViewById<NestedScrollView>(R.id.scrollView)
        scrollView?.post {
            scrollView.fullScroll(View.FOCUS_DOWN)
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
        val scrollView = dialog?.findViewById<NestedScrollView>(R.id.scrollView)
        scrollView?.post {
            scrollView.fullScroll(View.FOCUS_DOWN)
        }


        return typingLayout
    }

    private fun removeTypingMessage(view: View) {
        chatMessages.removeView(view)
    }


    private fun showSuggestedQuestions(array: JSONArray) {
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.START
            setPadding(14.dpToPx(), 12.dpToPx(), 14.dpToPx(), 12.dpToPx())
            dividerDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.divider_vertical_8dp)
            showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
        }

        for (i in 0 until array.length()) {
            val question = array.optString(i)
            val btn = MaterialButton(requireContext()).apply {
                text = question
                textSize = 14f
                isAllCaps = false
                gravity = Gravity.CENTER

                setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.white))

                setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))

                cornerRadius = 12.dpToPx()

                strokeWidth = 1.dpToPx()
                strokeColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.outline_variant))

                elevation = 0f

                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                lp.setMargins(12.dpToPx(), 8.dpToPx(), 12.dpToPx(), 8.dpToPx())
                layoutParams = lp

                setOnClickListener {
                    chatMessages.removeView(container)
                    sendMessageToBot(question)
                }
            }

            container.addView(btn)

            // Add margin between buttons
            (btn.layoutParams as LinearLayout.LayoutParams).apply {
                setMargins(0, 4.dpToPx(), 0, 4.dpToPx())
            }
        }

        // Add some margin to the container
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(8.dpToPx(), 8.dpToPx(), 8.dpToPx(), 16.dpToPx())
        }

        chatMessages.addView(container, layoutParams)

        // Smooth scroll to bottom
        val scrollView = dialog?.findViewById<NestedScrollView>(R.id.scrollView)
        scrollView?.postDelayed({
            scrollView.smoothScrollTo(0, scrollView.getChildAt(0).height)
        }, 100)
    }

    // Extension function to convert dp to pixels
    private fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    private fun loadBotParameters() {

        val request = Request.Builder()
            .url("https://chatbot.albarakasyria.com:3001/v1/parameters")
            .addHeader("Authorization", "Bearer albaraka-default-key") // ← عدّل حسب مفتاحك الصحيح
            .get()
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    addMessageToChat("⚠️ فشل الاتصال بالسيرفر: ${e.message}", false)
                }
            }

            override fun onResponse(call: Call, response: Response) {

                val raw = response.body?.string()?.trim() ?: ""

                println("🔵 RAW PARAMETERS RESPONSE:\n$raw")

                // ---------------------------------------------
                // 1. حماية ضد HTML / صفحات خطأ
                // ---------------------------------------------
                if (!raw.startsWith("{")) {
                    activity?.runOnUiThread {
                        addMessageToChat(
                            "⚠️ السيرفر أرسل رد غير صالح (HTML أو صفحة خطأ)\n\n" +
                                    "الرد:\n$raw",
                            false
                        )
                    }
                    return
                }

                try {
                    // ---------------------------------------------
                    // 2. محاولة تحويله إلى JSON
                    // ---------------------------------------------
                    val json = JSONObject(raw)

                    val openingStatement = json.optString("opening_statement", "")
                    val suggested = json.optJSONArray("suggested_questions")

                    activity?.runOnUiThread {

                        // -----------------------------------------
                        // 3. رسالة ترحيب — حتى لو فارغة
                        // -----------------------------------------
                        if (openingStatement.isNotBlank()) {
                            addMessageToChat(openingStatement, false)
                        } else {
                            addMessageToChat("👋 أهلاً بك! كيف يمكنني مساعدتك اليوم؟", false)
                        }

                        // -----------------------------------------
                        // 4. الأسئلة المقترحة
                        // -----------------------------------------
                        if (suggested != null && suggested.length() > 0) {
                            showSuggestedQuestions(suggested)
                        }
                    }

                } catch (e: Exception) {
                    // ---------------------------------------------
                    // 5. في حال JSON غير صالح
                    // ---------------------------------------------
                    activity?.runOnUiThread {
                        addMessageToChat(
                            "⚠️ خطأ ببارسنج JSON:\n${e.message}\n\nالنص الكامل:\n$raw",
                            false
                        )
                    }
                }
            }
        })
    }


    fun getConversationId(context: Context): String {
        val prefs = context.getSharedPreferences("chatbot_prefs", Context.MODE_PRIVATE)
        var conversationId = prefs.getString("conversation_id", null)

        if (conversationId.isNullOrEmpty()) {
            conversationId = UUID.randomUUID().toString()
            prefs.edit().putString("conversation_id", conversationId).apply()
        }

        return conversationId
    }

    private fun sendMessageToBot(question: String) {
        try {
            // 1) أضف رسالة المستخدم
            addMessageToChat(question, isUser = true)

            // 2) رسالة "البوت يكتب..."
            val typingView = addTypingMessage()

            // 3) جسم الطلب (streaming)
            val jsonBody = JSONObject().apply {
                put("inputs", JSONObject())
                put("query", question)
                put("response_mode", "streaming")
                put("conversation_id", conversationId ?: "")
                put("user", userId ?: "")
                put("files", JSONArray())
            }

            val body = jsonBody.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaType())

            val request = Request.Builder()
                .url("https://chatbot.albarakasyria.com:3001/v1/chat-messages")
                .addHeader("Authorization", "Bearer albaraka-default-key")
                .post(body)
                .build()

            val client = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.SECONDS)   // مهم للـ streaming (بدون تايم أوت قراءة)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: java.io.IOException) {
                    activity?.runOnUiThread {
                        removeTypingMessage(typingView)
                        addMessageToChat("خطأ في الاتصال: ${e.message}", isUser = false)
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    // 4) حضّر فقاعة البوت الفارغة وخُذ الـ TextView تبعها
                    var botTextView: TextView? = null

                    activity?.runOnUiThread {
                        removeTypingMessage(typingView)

                        // أضف رسالة بوت فارغة
                        addMessageToChat("", isUser = false)

                        // آخر layout مضاف
                        val lastLayout =
                            chatMessages.getChildAt(chatMessages.childCount - 1) as? LinearLayout

                        // ابحث عن أول TextView داخلها (تجاوز الأفاتار)
                        lastLayout?.let { layout ->
                            for (i in 0 until layout.childCount) {
                                val v = layout.getChildAt(i)
                                if (v is TextView) {
                                    botTextView = v
                                    break
                                }
                            }
                        }
                    }

                    val source = response.body?.source() ?: return

                    var buffer = ""
                    var fullAnswer = ""

                    try {
                        while (!source.exhausted()) {
                            val line = source.readUtf8Line() ?: continue

                            buffer += line + "\n"
                            val lines = buffer.split("\n")
                            buffer = lines.last()              // السطر غير المكتمل
                            val completeLines = lines.dropLast(1)

                            for (raw in completeLines) {
                                // مثل كود الويب: نتعامل فقط مع "data: ..."
                                if (!raw.startsWith("data:")) continue

                                val jsonPart = raw.removePrefix("data:").trim()
                                if (jsonPart.isEmpty()) continue

                                try {
                                    val data = JSONObject(jsonPart)
                                    val event = data.optString("event", "")
                                    val answerChunk = data.optString("answer", "")

                                    if (event == "message") {
                                        // تراكم النص
                                        fullAnswer += answerChunk

                                        val textToShow = fullAnswer

                                        activity?.runOnUiThread {
                                            botTextView?.text = textToShow

                                            // Scroll لأسفل
                                            val scrollView =
                                                dialog?.findViewById<NestedScrollView>(R.id.scrollView)
                                            scrollView?.post {
                                                scrollView.fullScroll(View.FOCUS_DOWN)
                                            }
                                        }

                                        // تحديت conversation_id لو وصل جديد
                                        val convId = data.optString("conversation_id", "")
                                        if (convId.isNotBlank()) {
                                            saveConversationId(convId)
                                        }
                                    } else if (event == "message_end") {
                                        // نفس منطق الويب: قراءة الميتاداتا/الكلفة (اختياري)
                                        val metadata = data.optJSONObject("metadata")
                                        val usage = metadata?.optJSONObject("usage")
                                        val cost = metadata?.optJSONObject("cost")

                                        if (usage != null && cost != null) {
                                            println("━━━━━━━━━━━━━━━━━━━━━━")
                                            println("💰 OpenAI Cost Breakdown")
                                            println("Prompt:     ${usage.optInt("prompt_tokens")}")
                                            println("Completion: ${usage.optInt("completion_tokens")}")
                                            println("Total:      ${usage.optInt("total_tokens")}")
                                            println("Input  \$:  ${cost.optDouble("input_cost_usd")}")
                                            println("Output \$:  ${cost.optDouble("output_cost_usd")}")
                                            println("Total  \$:  ${cost.optDouble("total_cost_usd")}")
                                            println("Model:      ${cost.optString("model")}")
                                            println("━━━━━━━━━━━━━━━━━━━━━━")
                                        }
                                    }
                                } catch (pe: Exception) {
                                    // خطأ بارسنج لسطر واحد → نتجاهله
                                    println("Parse error: ${pe.message}")
                                }
                            }
                        }
                    } catch (e: Exception) {
                        activity?.runOnUiThread {
                            if (fullAnswer.isBlank()) {
                                addMessageToChat(
                                    "خطأ أثناء قراءة الـ Stream: ${e.message}",
                                    isUser = false
                                )
                            } else {
                                botTextView?.text = fullAnswer
                            }
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
