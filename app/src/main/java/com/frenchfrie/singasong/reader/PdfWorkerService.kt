package com.frenchfrie.singasong.reader

import androidx.core.graphics.createBitmap
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.Handler
import android.os.Looper
import android.os.ParcelFileDescriptor
import android.os.Process
import kotlin.math.roundToInt

/** Only passed descriptors are accessible to this isolated, unexported process. */
class PdfWorkerService : Service() {
    private val handler = Handler(Looper.getMainLooper())
    private val worker = object : IPdfWorker.Stub() {
        override fun inspect(source: ParcelFileDescriptor): Int = guarded {
            source.use {
                PdfRenderer(ParcelFileDescriptor.dup(it.fileDescriptor)).use { renderer ->
                    require(renderer.pageCount in 1..MAX_PAGES) { "Unsupported page count" }
                    // Decode the first page before committing an import, not just its header.
                    renderer.openPage(0).use { page ->
                        val bitmap = createBitmap(64, 64, Bitmap.Config.ARGB_8888)
                        try {
                            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        } finally {
                            bitmap.recycle()
                        }
                    }
                    renderer.pageCount
                }
            }
        }

        override fun render(source: ParcelFileDescriptor, destination: ParcelFileDescriptor, pageIndex: Int, width: Int) {
            guarded {
                source.use { input ->
                    destination.use { output ->
                        PdfRenderer(ParcelFileDescriptor.dup(input.fileDescriptor)).use { renderer ->
                            require(pageIndex in 0 until renderer.pageCount)
                            renderer.openPage(pageIndex).use { page ->
                                require(page.width > 0 && page.height > 0)
                                val factor = minOf(width.coerceIn(64, 2048).toDouble() / page.width, 2048.0 / page.height)
                                val bitmap = createBitmap(
                                    (page.width * factor).roundToInt().coerceIn(1, 2048),
                                    (page.height * factor).roundToInt().coerceIn(1, 2048),
                                    Bitmap.Config.ARGB_8888,
                                )
                                try {
                                    bitmap.eraseColor(Color.WHITE)
                                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                                    ParcelFileDescriptor.AutoCloseOutputStream(ParcelFileDescriptor.dup(output.fileDescriptor)).use {
                                        check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it))
                                    }
                                } finally {
                                    bitmap.recycle()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    internal fun <T> guarded(block: () -> T): T {
        // A native parser stall must not retain the caller indefinitely.
        val timeout = Runnable { Process.killProcess(Process.myPid()) }
        handler.postDelayed(timeout, 20_000)
        return try {
            block()
        } catch (failure: Exception) {
            // Binder can marshal this exception type consistently across supported APIs.
            throw IllegalArgumentException("Unable to read this PDF", failure)
        } finally {
            handler.removeCallbacks(timeout)
        }
    }

    override fun onBind(intent: Intent) = worker

    companion object {
        const val MAX_PAGES = 300
    }
}
