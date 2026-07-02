package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.example.data.Complaint
import com.example.data.Student
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object NotificationHelper {
    private const val CHANNEL_ID = "ccms_notifications"
    private const val CHANNEL_NAME = "Complaint Status Updates"

    fun showNotification(context: Context, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Notifications for complaint updates"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}

object PdfExporter {
    fun exportComplaintsToPdf(context: Context, complaints: List<Complaint>, studentName: String): File? {
        try {
            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 Size (595x842 pt)
            var page = document.startPage(pageInfo)
            var canvas = page.canvas

            val paint = Paint()
            val textPaint = Paint().apply {
                color = Color.BLACK
                textSize = 12f
            }

            val titlePaint = Paint().apply {
                color = Color.rgb(33, 150, 243)
                textSize = 20f
                isFakeBoldText = true
            }

            val headerPaint = Paint().apply {
                color = Color.DKGRAY
                textSize = 14f
                isFakeBoldText = true
            }

            val subheadPaint = Paint().apply {
                color = Color.GRAY
                textSize = 11f
            }

            // Draw header
            canvas.drawText("College Complaint Management System", 40f, 60f, titlePaint)
            canvas.drawText("Complaint History Report for: $studentName", 40f, 90f, headerPaint)
            canvas.drawText("Generated on: ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())}", 40f, 110f, subheadPaint)

            // Draw line
            paint.color = Color.rgb(200, 200, 200)
            paint.strokeWidth = 2f
            canvas.drawLine(40f, 130f, 555f, 130f, paint)

            var y = 160f
            val lineSpacing = 24f

            if (complaints.isEmpty()) {
                canvas.drawText("No complaints found.", 40f, y, textPaint)
            } else {
                complaints.forEachIndexed { index, complaint ->
                    if (y > 780f) {
                        document.finishPage(page)
                        val newPageInfo = PdfDocument.PageInfo.Builder(595, 842, index + 2).create()
                        page = document.startPage(newPageInfo)
                        canvas = page.canvas
                        y = 60f
                    }

                    // Card top margin
                    y += 10f
                    paint.color = Color.rgb(245, 245, 245)
                    canvas.drawRect(35f, y - 5f, 560f, y + 105f, paint)

                    paint.color = when (complaint.priority) {
                        "High" -> Color.rgb(244, 67, 54)
                        "Medium" -> Color.rgb(255, 152, 0)
                        else -> Color.rgb(76, 175, 80)
                    }
                    canvas.drawRect(35f, y - 5f, 42f, y + 105f, paint)

                    // Draw complaint data
                    val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(complaint.createdDate))
                    canvas.drawText("ID: COMP-${complaint.id} | Date: $dateStr | Category: ${complaint.category}", 50f, y + 15f, subheadPaint)
                    
                    val titlePaintBold = Paint().apply {
                        color = Color.BLACK
                        textSize = 13f
                        isFakeBoldText = true
                    }
                    canvas.drawText(complaint.title, 50f, y + 35f, titlePaintBold)
                    
                    val descStr = if (complaint.description.length > 60) complaint.description.take(57) + "..." else complaint.description
                    canvas.drawText("Desc: $descStr", 50f, y + 55f, textPaint)
                    canvas.drawText("Priority: ${complaint.priority} | Status: ${complaint.status}", 50f, y + 75f, textPaint)
                    
                    if (complaint.remarks.isNotEmpty()) {
                        val remarkPaint = Paint().apply {
                            color = Color.rgb(0, 100, 80)
                            textSize = 11f
                            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.ITALIC)
                        }
                        canvas.drawText("Remarks: ${complaint.remarks}", 50f, y + 95f, remarkPaint)
                    }

                    y += 115f
                }
            }

            document.finishPage(page)

            val dir = File(context.cacheDir, "reports")
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, "ccms_report_${System.currentTimeMillis()}.pdf")
            val fos = FileOutputStream(file)
            document.writeTo(fos)
            fos.close()
            document.close()
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun viewPdf(context: Context, file: File) {
        try {
            val authority = "${context.packageName}.fileprovider"
            val uri = FileProvider.getUriForFile(context, authority, file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

object BackupRestoreHelper {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    data class BackupPayload(
        val students: List<Student>,
        val complaints: List<Complaint>
    )

    fun createBackup(context: Context, students: List<Student>, complaints: List<Complaint>): String? {
        return try {
            val payload = BackupPayload(students, complaints)
            val adapter = moshi.adapter(BackupPayload::class.java)
            val jsonStr = adapter.toJson(payload)

            val dir = File(context.cacheDir, "backups")
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, "ccms_backup_${System.currentTimeMillis()}.json")
            val fos = FileOutputStream(file)
            fos.write(jsonStr.toByteArray())
            fos.close()

            jsonStr
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun parseBackup(jsonStr: String): BackupPayload? {
        return try {
            val adapter = moshi.adapter(BackupPayload::class.java)
            adapter.fromJson(jsonStr)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
