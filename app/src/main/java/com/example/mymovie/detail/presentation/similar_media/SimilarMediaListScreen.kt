package com.example.mymovie.detail.presentation.similar_media

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mymovie.R
import com.example.mymovie.detail.presentation.MediaDetailsScreenState
import com.example.mymovie.detail.presentation.detailScreenUiComponents.SimilarMediaItem
import com.example.mymovie.ui.theme.SmallRadius
import com.example.mymovie.ui_shared_components.ListShimmerEffect
import com.example.mymovie.ui_shared_components.header

@Composable
fun SimilarMediaListScreen(
    navController: NavController,
    mediaDetailsScreenState: MediaDetailsScreenState,
    name: String,
) {
    val title = stringResource(R.string.similar_to, name)

    val mediaList = mediaDetailsScreenState.similarMediaList

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {

        if (mediaList.isEmpty()) {
            ListShimmerEffect(
                title = title,
                SmallRadius
            )
        } else {

            val listState = rememberLazyGridState()

            LazyVerticalGrid(
                state = listState,
                contentPadding = PaddingValues(top = SmallRadius.dp),
                columns = GridCells.Adaptive(190.dp),
            ) {

                header {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(
                                    vertical = 16.dp,
                                    horizontal = 22.dp
                                ),
                            textAlign = TextAlign.Center,
                            text = title,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 20.sp
                        )
                    }

                }

                items(mediaList.size) { i ->
                    SimilarMediaItem(
                        media = mediaList[i],
                        navController = navController,
                        mediaDetailsScreenState = mediaDetailsScreenState
                    )
                }

            }
        }
    }
}
