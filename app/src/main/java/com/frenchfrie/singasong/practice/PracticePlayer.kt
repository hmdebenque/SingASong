package com.frenchfrie.singasong.practice

import android.content.Context
import android.net.Uri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class PracticePlayer(context: Context) {
    val player: ExoPlayer = ExoPlayer.Builder(context)
        .setAudioAttributes(
            AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_MUSIC).build(),
            true,
        )
        .setHandleAudioBecomingNoisy(true)
        .build()

    fun open(file: File, positionMs: Long) {
        player.setMediaItem(MediaItem.fromUri(Uri.fromFile(file)), positionMs)
        player.playWhenReady = false
        player.prepare()
    }

    fun toggle() {
        if (player.isPlaying) player.pause() else {
            if (player.playbackState == Player.STATE_ENDED) player.seekTo(0)
            if (player.playbackState == Player.STATE_IDLE) player.prepare()
            player.play()
        }
    }

    fun close() = player.release()

    companion object {
        /** Validate real decoding/readiness before the old recording is replaced. */
        suspend fun inspect(context: Context, file: File): Long = withContext(Dispatchers.Main) {
            val candidate = ExoPlayer.Builder(context).build()
            try {
                withTimeout(10_000) {
                    suspendCancellableCoroutine { continuation ->
                        candidate.addListener(object : Player.Listener {
                            override fun onPlaybackStateChanged(playbackState: Int) {
                                if (playbackState == Player.STATE_READY && continuation.isActive) {
                                    if (candidate.duration > 0) continuation.resume(candidate.duration)
                                    else continuation.resumeWithException(IllegalArgumentException("Recording has no duration"))
                                }
                            }
                            override fun onPlayerError(error: PlaybackException) {
                                if (continuation.isActive) continuation.resumeWithException(error)
                            }
                        })
                        candidate.setMediaItem(MediaItem.fromUri(Uri.fromFile(file)))
                        candidate.prepare()
                    }
                }
            } finally {
                candidate.release()
            }
        }
    }
}
