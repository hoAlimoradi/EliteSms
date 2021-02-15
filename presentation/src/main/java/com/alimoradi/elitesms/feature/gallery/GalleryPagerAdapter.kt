package com.alimoradi.elitesms.feature.gallery

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.alimoradi.data.extensions.isImage
import com.alimoradi.data.extensions.isVideo
import com.alimoradi.data.util.GlideApp
import com.alimoradi.domain.model.MmsPart
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.alimoradi.elitesms.common.base.QkRealmAdapter
import com.alimoradi.elitesms.common.base.QkViewHolder
import com.alimoradi.elitesms.databinding.GalleryImagePageBinding
import com.alimoradi.elitesms.databinding.GalleryInvalidPageBinding
import com.alimoradi.elitesms.databinding.GalleryVideoPageBinding
import com.alimoradi.smsmms.com.google.android.mms.ContentType
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.*
import javax.inject.Inject

class GalleryPagerAdapter @Inject constructor(
    private val context: Context
) : QkRealmAdapter<MmsPart, ViewBinding>() {

    companion object {
        private const val VIEW_TYPE_INVALID = 0
        private const val VIEW_TYPE_IMAGE = 1
        private const val VIEW_TYPE_VIDEO = 2
    }

    val clicks: Subject<View> = PublishSubject.create()

    private val contentResolver = context.contentResolver
    private val exoPlayers = Collections.newSetFromMap(WeakHashMap<ExoPlayer?, Boolean>())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QkViewHolder<ViewBinding> {
        val holder: QkViewHolder<ViewBinding> = when (viewType) {
            VIEW_TYPE_IMAGE -> QkViewHolder(parent, GalleryImagePageBinding::inflate)
            VIEW_TYPE_VIDEO -> QkViewHolder(parent, GalleryVideoPageBinding::inflate)
            else -> QkViewHolder(parent, GalleryInvalidPageBinding::inflate)
        }

        return holder.apply {
            if (binding is GalleryImagePageBinding) {
                // When calling the public setter, it doesn't allow the midscale to be the same as the
                // maxscale or the minscale. We don't want 3 levels and we don't want to modify the library
                // so let's celebrate the invention of reflection!
                binding.image.attacher.run {
                    javaClass.getDeclaredField("mMinScale").run {
                        isAccessible = true
                        setFloat(binding.image.attacher, 1f)
                    }
                    javaClass.getDeclaredField("mMidScale").run {
                        isAccessible = true
                        setFloat(binding.image.attacher, 1f)
                    }
                    javaClass.getDeclaredField("mMaxScale").run {
                        isAccessible = true
                        setFloat(binding.image.attacher, 3f)
                    }
                }
            }

            binding.root.setOnClickListener(clicks::onNext)
        }
    }

    override fun onBindViewHolder(holder: QkViewHolder<ViewBinding>, position: Int) {
        val part = getItem(position) ?: return
        when {
            getItemViewType(position) == VIEW_TYPE_IMAGE && holder.binding is GalleryImagePageBinding -> {
                // We need to explicitly request a gif from glide for animations to work
                when (part.getUri().let(contentResolver::getType)) {
                    ContentType.IMAGE_GIF -> GlideApp.with(context)
                            .asGif()
                            .load(part.getUri())
                            .into(holder.binding.image)

                    else -> GlideApp.with(context)
                            .asBitmap()
                            .load(part.getUri())
                            .into(holder.binding.image)
                }
            }

            getItemViewType(position) == VIEW_TYPE_VIDEO && holder.binding is GalleryVideoPageBinding -> {
                val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(null)
                val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
                val exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector)
                holder.binding.video.player = exoPlayer
                exoPlayers.add(exoPlayer)

                val dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context, "QKSMS"))
                val videoSource = ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(part.getUri())
                exoPlayer?.prepare(videoSource)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val part = getItem(position)
        return when {
            part?.isImage() == true -> VIEW_TYPE_IMAGE
            part?.isVideo() == true -> VIEW_TYPE_VIDEO
            else -> VIEW_TYPE_INVALID
        }
    }

    fun destroy() {
        exoPlayers.forEach { exoPlayer -> exoPlayer?.release() }
    }

}
