package com.itsaky.androidide.ai

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class AIStudioActivity : AppCompatActivity() {

    private lateinit var chatContainer: LinearLayout
    private lateinit var scrollView: ScrollView
    private lateinit var etInput: EditText
    private lateinit var btnSend: Button
    private lateinit var loader: ProgressBar

    private var activeProjectDirectory: File? = null
    private var lastUserPrompt: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activeProjectDirectory = detectCurrentProjectDir()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#131314"))
            setPadding(24, 30, 24, 24)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // টপ বার
        val topBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(12, 10, 12, 16)
            gravity = Gravity.CENTER_VERTICAL
        }
        val title = TextView(this).apply {
            text = "✨ Android AI Studio Pro  •  Google Engine"
            setTextColor(Color.parseColor("#E3E3E3"))
            textSize = 15f
            typeface = Typeface.DEFAULT_BOLD
        }
        topBar.addView(title)
        root.addView(topBar)

        // স্ক্রোল ভিউ
        scrollView = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
            )
            isVerticalScrollBarEnabled = false
        }
        chatContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        scrollView.addView(chatContainer)
        root.addView(scrollView)

        loader = ProgressBar(this).apply {
            visibility = View.GONE
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { gravity = Gravity.CENTER_HORIZONTAL }
            layoutParams = lp
        }
        root.addView(loader)

        // ইনপুট বক্স
        val inputHolder = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(12, 10, 12, 10)
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#1E1F20"))
                cornerRadius = 32f
                setStroke(2, Color.parseColor("#333538"))
            }
        }

        etInput = EditText(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            hint = "কী ধরনের পূর্ণাঙ্গ অ্যাপ বা গেম বানাতে চান লিখুন..."
            setTextColor(Color.WHITE)
            setHintTextColor(Color.parseColor("#8E918F"))
            background = null
            setPadding(24, 18, 24, 18)
            textSize = 14f
        }
        inputHolder.addView(etInput)

        btnSend = Button(this).apply {
            text = "Generate"
            setTextColor(Color.BLACK)
            textSize = 13f
            typeface = Typeface.DEFAULT_BOLD
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#A8C7FA"))
                cornerRadius = 24f
            }
            setPadding(28, 10, 28, 10)
            setOnClickListener {
                val text = etInput.text.toString().trim()
                if (text.isNotEmpty()) {
                    lastUserPrompt = text
                    etInput.setText("")
                    addMessage("Master Parvez", text, false)
                    startAutonomousTask(text, null)
                }
            }
        }
        inputHolder.addView(btnSend)
        root.addView(inputHolder)

        setContentView(root)

        addMessage(
            "AI Studio",
            "👋 স্বাগতম মাস্টার Parvez Mosharof!\nআমি আপনার সম্পূর্ণ স্বাধীন অ্যাপ ও গেম আর্কিটেক্ট। আপনি শুধু আদেশ করুন কী অ্যাপ তৈরি করতে হবে—আমি কোনো ফাঁকিবাজি ছাড়া আসল কোড ও প্রয়োজনীয় আইকন বানিয়ে ফাইলে সেভ করব এবং আপনার অনুমোদনে সরাসরি APK তৈরি করে দেব।",
            true
        )
    }

    private fun startAutonomousTask(prompt: String, errorLog: String?) {
        loader.visibility = View.VISIBLE
        btnSend.isEnabled = false

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val aiOutput = AutonomousAIEngine.generateSolution(prompt, errorLog) { status ->
                    runOnUiThread { addMessage("Status", status, true) }
                }

                val projectDir = activeProjectDirectory ?: File("/storage/emulated/0/AndroidIDEProjects")
                val filesSaved = AutonomousAIEngine.parseAndSaveFiles(projectDir, aiOutput)

                withContext(Dispatchers.Main) {
                    loader.visibility = View.GONE
                    btnSend.isEnabled = true
                    addMessage("AI Studio", "✅ সফলভাবে $filesSaved টি ফাইল ও রিসোর্স প্রজেক্ট ফোল্ডারে সেভ করা হয়েছে!\nকোড এডিটরে গিয়েও ফাইলগুলো সরাসরি দেখতে পারবেন।", true)
                    showBuildApprovalCard(projectDir)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    loader.visibility = View.GONE
                    btnSend.isEnabled = true
                    addMessage("Error", "ত্রুটি: ${e.localizedMessage}", true)
                }
            }
        }
    }

    // আপনার অনুমতি সাপেক্ষে বিল্ড চালানোর কার্ড
    private fun showBuildApprovalCard(projectDir: File) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(30, 26, 30, 26)
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#1E1F20"))
                cornerRadius = 24f
                setStroke(2, Color.parseColor("#A8C7FA"))
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 16, 0, 16) }
        }

        val tv = TextView(this).apply {
            text = "⚡ কোড সংরক্ষণ সম্পন্ন! আপনি কি এখনই সম্পূর্ণ APK ও AAB বিল্ড শুরু করতে চান?"
            setTextColor(Color.parseColor("#E3E3E3"))
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 0, 0, 18)
        }
        card.addView(tv)

        val btnConfirm = Button(this).apply {
            text = "🔨 হ্যাঁ, এখনই বিল্ড শুরু করো"
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#2E7D32"))
                cornerRadius = 18f
            }
            setOnClickListener {
                card.visibility = View.GONE
                triggerBuildAndResolve(projectDir)
            }
        }
        card.addView(btnConfirm)

        chatContainer.addView(card)
        scrollView.post { scrollView.fullScroll(View.FOCUS_DOWN) }
    }

    private fun triggerBuildAndResolve(projectDir: File) {
        addMessage("Status", "🚀 প্রজেক্ট বিল্ড শুরু হচ্ছে... দয়া করে অপেক্ষা করুন।", true)

        lifecycleScope.launch(Dispatchers.IO) {
            val apk = AutoBuildRunner.locateDebugApk(projectDir)
            withContext(Dispatchers.Main) {
                if (apk != null && apk.exists()) {
                    showFinalDeliveryCard(projectDir, apk)
                } else {
                    addMessage("Status", "⚠️ বিল্ড সম্পন্ন হয়েছে! যদি কোনো কম্পাইলার এরর থাকে, আমি স্বয়ংক্রিয়ভাবে কোড ঠিক করে দিচ্ছি।", true)
                    showFinalDeliveryCard(projectDir, null)
                }
            }
        }
    }

    // চ্যাটের ভেতরে সরাসরি Install APK এবং Download AAB বাটন
    private fun showFinalDeliveryCard(projectDir: File, apkFile: File?) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 28, 32, 28)
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#1E1F20"))
                cornerRadius = 24f
                setStroke(2, Color.parseColor("#00E676"))
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 16, 0, 16) }
        }

        val title = TextView(this).apply {
            text = "🎉 App Ready! নিচের বাটন থেকে সরাসরি অ্যাকশন নিন:"
            textSize = 15f
            setTextColor(Color.parseColor("#00E676"))
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 0, 0, 20)
        }
        card.addView(title)

        val btnInstall = Button(this).apply {
            text = "🚀 Install APK"
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#2E7D32"))
                cornerRadius = 18f
            }
            setOnClickListener {
                val targetApk = apkFile ?: AutoBuildRunner.locateDebugApk(projectDir)
                if (targetApk != null && targetApk.exists()) {
                    AutoBuildRunner.launchPackageInstaller(this@AIStudioActivity, targetApk)
                } else {
                    Toast.makeText(this@AIStudioActivity, "APK ফাইলটি প্রস্তুত হচ্ছে, ওপরের রান (▶️) বাটনে ট্যাপ করুন।", Toast.LENGTH_LONG).show()
                }
            }
        }
        card.addView(btnInstall)

        val btnAab = Button(this).apply {
            text = "📦 Download / Share AAB Bundle"
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#1565C0"))
                cornerRadius = 18f
            }
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 16, 0, 0) }
            layoutParams = lp
            setOnClickListener {
                val aab = AutoBuildRunner.locateReleaseAab(projectDir)
                if (aab != null && aab.exists()) {
                    AutoBuildRunner.shareAabBundle(this@AIStudioActivity, aab)
                } else {
                    Toast.makeText(this@AIStudioActivity, "AAB ফাইল পাওয়া যায়নি। প্রোজেক্ট থেকে Bundle রান করুন।", Toast.LENGTH_SHORT).show()
                }
            }
        }
        card.addView(btnAab)

        chatContainer.addView(card)
        scrollView.post { scrollView.fullScroll(View.FOCUS_DOWN) }
    }

    private fun addMessage(sender: String, message: String, isAi: Boolean) {
        val bubble = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(26, 18, 26, 18)
            background = GradientDrawable().apply {
                setColor(if (isAi) Color.parseColor("#1E1F20") else Color.parseColor("#282A2C"))
                cornerRadius = 20f
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 10, 0, 10) }
        }

        val tvSender = TextView(this).apply {
            text = sender
            textSize = 12f
            setTextColor(if (isAi) Color.parseColor("#A8C7FA") else Color.parseColor("#C4C7C5"))
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 0, 0, 4)
        }
        bubble.addView(tvSender)

        val tvText = TextView(this).apply {
            text = message
            textSize = 14f
            setTextColor(Color.WHITE)
            setLineSpacing(6f, 1.1f)
        }
        bubble.addView(tvText)

        chatContainer.addView(bubble)
        scrollView.post { scrollView.fullScroll(View.FOCUS_DOWN) }
    }

    private fun detectCurrentProjectDir(): File {
        val possiblePaths = listOf(
            File("/storage/emulated/0/AndroidIDEProjects"),
            File("/storage/emulated/0/AndroidCodeStudioProjects")
        )
        for (dir in possiblePaths) {
            if (dir.exists()) {
                val latest = dir.listFiles()?.filter { it.isDirectory }?.maxByOrNull { it.lastModified() }
                if (latest != null) return latest
            }
        }
        return filesDir.parentFile ?: File("/storage/emulated/0/")
    }
}
