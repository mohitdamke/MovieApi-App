package com.example.mymovie.detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymovie.detail.domain.repository.DetailRepository
import com.example.mymovie.detail.domain.repository.ExtraDetailRepository
import com.example.mymovie.detail.domain.usecase.MinutesToReadableTime
import com.example.mymovie.main.data.remote.api.MediaApi.Companion.API_KEY
import com.example.mymovie.main.domain.repository.MediaRepository
import com.example.mymovie.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaDetailsViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val detailsRepository: DetailRepository,
    private val extraDetailsRepository: ExtraDetailRepository
) : ViewModel() {

    private val _mediaDetailsScreenState = MutableStateFlow(MediaDetailsScreenState())
    val mediaDetailsScreenState = _mediaDetailsScreenState.asStateFlow()

    fun onEvent(event: MediaDetailsScreenEvents) {
        when (event) {
            is MediaDetailsScreenEvents.NavigateToWatchVideo -> {
                _mediaDetailsScreenState.update {
                    it.copy(
                        videoId = mediaDetailsScreenState.value.videosList.shuffled()[0]
                    )
                }
            }

            is MediaDetailsScreenEvents.Refresh -> {
                _mediaDetailsScreenState.update {
                    it.copy(
                        isLoading = true
                    )
                }

                startLoad(true)
            }

            is MediaDetailsScreenEvents.SetDataAndLoad -> {
                _mediaDetailsScreenState.update {
                    it.copy(
                        moviesGenresList = event.moviesGenresList,
                        tvGenresList = event.tvGenresList,
                    )
                }

                startLoad(
                    isRefresh = false,
                    id = event.id,
                    type = event.type,
                    category = event.category
                )
            }
        }
    }

    private fun startLoad(
        isRefresh: Boolean,
        id: Int = mediaDetailsScreenState.value.media?.id ?: 0,
        type: String = mediaDetailsScreenState.value.media?.mediaType ?: "",
        category: String = mediaDetailsScreenState.value.media?.category ?: "",
    ) {

        loadMediaItem(
            id = id,
            type = type,
            category = category
        ) {
            loadDetails(
                isRefresh = isRefresh
            )

            loadSimilarMedialList(
                isRefresh = isRefresh
            )

            loadVideosList(
                isRefresh = isRefresh
            )
        }
    }

    private fun loadMediaItem(
        id: Int,
        type: String,
        category: String,
        onFinished: () -> Unit
    ) {
        viewModelScope.launch {
            _mediaDetailsScreenState.update {
                it.copy(
                    media = mediaRepository.getItem(
                        type = type,
                        category = category,
                        id = id,
                    )
                )
            }
            onFinished()
        }
    }

    private fun loadDetails(isRefresh: Boolean) {

        viewModelScope.launch {

            detailsRepository
                .getDetails(
                    id = mediaDetailsScreenState.value.media?.id ?: 0,
                    type = mediaDetailsScreenState.value.media?.mediaType ?: "",
                    isRefresh = isRefresh,
                    apiKey = API_KEY
                )
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { media ->
                                _mediaDetailsScreenState.update {
                                    it.copy(
                                        media = mediaDetailsScreenState.value.media?.copy(
                                            runtime = media.runtime,
                                            status = media.status,
                                            tagline = media.tagline,
                                        ),
                                        readableTime = MinutesToReadableTime(
                                            media.runtime ?: 0
                                        ).invoke()
                                    )
                                }
                            }
                        }

                        is Resource.Error -> Unit

                        is Resource.Loading -> {
                            _mediaDetailsScreenState.update {
                                it.copy(
                                    isLoading = result.isLoading
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun loadSimilarMedialList(isRefresh: Boolean) {

        viewModelScope.launch {

            extraDetailsRepository
                .getSimilarMediaList(
                    isRefresh = isRefresh,
                    id = mediaDetailsScreenState.value.media?.id ?: 0,
                    type = mediaDetailsScreenState.value.media?.mediaType ?: "",
                    page = 1,
                    apiKey = API_KEY
                )
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { similarMediaList ->
                                _mediaDetailsScreenState.update {
                                    it.copy(
                                        similarMediaList = similarMediaList,
                                        smallSimilarMediaList = similarMediaList.take(10)
                                    )
                                }
                            }
                        }

                        is Resource.Error -> {}

                        is Resource.Loading -> {
                            _mediaDetailsScreenState.update {
                                it.copy(
                                    isLoading = result.isLoading
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun loadVideosList(isRefresh: Boolean) {

        viewModelScope.launch {

            extraDetailsRepository
                .getVideosList(
                    id = mediaDetailsScreenState.value.media?.id ?: 0,
                    isRefresh = isRefresh,
                    apiKey = API_KEY
                )
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { videosList ->
                                _mediaDetailsScreenState.update {
                                    it.copy(
                                        videosList = videosList
                                    )
                                }
                            }
                        }

                        is Resource.Error -> {}

                        is Resource.Loading -> {
                            _mediaDetailsScreenState.update {
                                it.copy(
                                    isLoading = result.isLoading
                                )
                            }
                        }
                    }
                }
        }
    }

}






