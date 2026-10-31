package com.frenchfrie.singasong.reader

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.IBinder
import android.os.ParcelFileDescriptor
import java.io.File
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

interface ScoreReader {
    suspend fun inspect(file: File): Int
    suspend fun render(file: File, page: Int, width: Int): Bitmap
}

class PdfReader(private val context: Context) : ScoreReader {
    private val mutex = Mutex()

    override suspend fun inspect(file: File): Int = call { worker ->
        ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).use(worker::inspect)
    }

    override suspend fun render(file: File, page: Int, width: Int): Bitmap = call { worker ->
        val result = File.createTempFile("page-", ".png", context.cacheDir)
        try {
            ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).use { input ->
                ParcelFileDescriptor.open(result, ParcelFileDescriptor.MODE_READ_WRITE).use { output ->
                    worker.render(input, output, page, width)
                }
            }
            checkNotNull(BitmapFactory.decodeFile(result.path)) { "Unable to render page" }
        } finally {
            result.delete()
        }
    }

    private suspend fun <T> call(block: (IPdfWorker) -> T): T = mutex.withLock {
        val connected = CompletableDeferred<IPdfWorker>()
        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                connected.complete(IPdfWorker.Stub.asInterface(binder))
            }
            override fun onServiceDisconnected(name: ComponentName) {
                connected.completeExceptionally(IllegalStateException("PDF worker stopped"))
            }
            override fun onNullBinding(name: ComponentName) {
                connected.completeExceptionally(IllegalStateException("PDF worker unavailable"))
            }
        }
        check(context.bindService(Intent(context, PdfWorkerService::class.java), connection, Context.BIND_AUTO_CREATE))
        try {
            val worker = withTimeout(10_000) { connected.await() }
            withContext(Dispatchers.IO) { block(worker) }
        } finally {
            context.unbindService(connection)
        }
    }
}
