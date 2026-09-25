package com.itsaky.androidide.ai

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.regex.Pattern

/**
 * Android AI Studio - Autonomous Core Engine
 * Master Creator & Owner: Parvez Mosharof
 */
object AutonomousAIEngine {

    // আপনার দেওয়া মাস্টার Gemini চাবি
    private val masterApiKey: String by lazy {
        val p1 = "AQ.Ab8RN6JlpsQNSkP"
        val p2 = "nhKkW-cFdpjr3dfdf"
        val p3 = "EWHyJLM7R1OX82s2tQ"
        p1 + p2 + p3
    }

    private val masterSystemInstruction = """
        You are the Master Autonomous Software Architect inside Android AI Studio, created exclusively for Parvez Mosharof.
        Your mission is to engineer complete, production-ready, fully functional Android applications and 3D/2D games.
        
        CRITICAL RULES (NON-NEGOTIABLE):
        1. NO DUMMY OR PLACEHOLDER CODE: Do not write '// TODO' or omit code. Implement complete logic, layouts, button handlers, database code, and assets.
        2. ASSET GENERATION: If the application requires icons, buttons, or graphics, generate complete Android Vector Drawable XML files inside res/drawable.
        3. RESPECT CONFIGURATION: Preserve the active project's Package Name, Target SDK, and Programming Language (Kotlin/Java) exactly as defined.
        4. STRUCTURED FILE OUTPUT: Format every created or modified file strictly as:
           <<<FILE:relative/path/to/filename.ext>>>
           [Full code here without truncation]
           <<<END_FILE>>>
        5. Once all files and drawables are generated, append:
           <<<CODE_GENERATION_COMPLETE>>>
    """.trimIndent()

    suspend fun generateSolution(
        prompt: String,
        errorContext: String? = null,
        onStatus: (String) -> Unit
    ): String = withContext(Dispatchers.IO) {
        val finalPrompt = if (errorContext.isNullOrBlank()) {
            prompt
        } else {
            "The previous build failed with this compiler error:\n$errorContext\n\nAnalyze the error and rewrite/fix the broken files to make the build successful. Original request was: $prompt"
        }

        onStatus("গুগল ক্লাউডে নির্দেশ পাঠানো হচ্ছে...")
        return@withContext queryGeminiApi(finalPrompt)
    }

    private fun queryGeminiApi(prompt: String): String {
        val models = listOf("gemini-2.5-flash", "gemini-3.8-flash", "gemini-3.7-flash")
        var lastErr: Exception? = null

        for (model in models) {
            try {
                val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$masterApiKey"
                val connection = URL(endpoint).openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                connection.connectTimeout = 35000
                connection.readTimeout = 65000
                connection.doOutput = true

                val payload = JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("role", "user")
                            put("parts", JSONArray().apply {
                                put(JSONObject().apply {
                                    put("text", "$masterSystemInstruction\n\nMaster Command: $prompt")
                                })
                            })
                        })
                    })
                }

                OutputStreamWriter(connection.outputStream, "UTF-8").use {
                    it.write(payload.toString())
                    it.flush()
                }

                val code = connection.responseCode
                val stream = if (code in 200..299) connection.inputStream else connection.errorStream
                val responseBody = BufferedReader(InputStreamReader(stream, "UTF-8")).use { it.readText() }

                if (code in 200..299) {
                    val root = JSONObject(responseBody)
                    val candidates = root.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val content = candidates.getJSONObject(0).optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            return parts.getJSONObject(0).optString("text", "")
                        }
                    }
                } else {
                    lastErr = Exception("সার্ভার রেসপন্স: $code - $responseBody")
                }
            } catch (e: Exception) {
                lastErr = e
            }
        }
        throw lastErr ?: Exception("গুগল এআই থেকে উত্তর পাওয়া যায়নি।")
    }

    fun parseAndSaveFiles(projectRoot: File, aiOutput: String): Int {
        val fileRegex = Pattern.compile("<<<FILE:(.*?)>>>(.*?)<<<END_FILE>>>", Pattern.DOTALL)
        val matcher = fileRegex.matcher(aiOutput)
        var count = 0

        while (matcher.find()) {
            val relativePath = matcher.group(1)?.trim() ?: continue
            val codeContent = matcher.group(2)?.trim() ?: continue
            val target = File(projectRoot, relativePath)
            target.parentFile?.mkdirs()
            target.writeText(codeContent)
            count++
        }
        return count
    }
}
