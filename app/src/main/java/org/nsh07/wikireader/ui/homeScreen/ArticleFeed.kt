package org.nsh07.wikireader.ui.homeScreen

import android.icu.text.CompactDecimalFormat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.text.parseAsHtml
import coil3.ImageLoader
import okio.utf8Size
import org.nsh07.wikireader.ui.image.FeedImage
import org.nsh07.wikireader.ui.viewModel.FeedState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ArticleFeed(
    feedState: FeedState,
    imageLoader: ImageLoader,
    insets: PaddingValues,
    listState: LazyListState,
    performSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val df = remember {
        CompactDecimalFormat.getInstance(
            context.resources.configuration.getLocales().get(0),
            CompactDecimalFormat.CompactStyle.SHORT
        )
    }
    val dtf = remember {
        DateTimeFormatter
            .ofLocalizedDate(FormatStyle.LONG)
            .withLocale(context.resources.configuration.getLocales().get(0))
    }
    val uriHandler = LocalUriHandler.current
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize()
    ) {
        item {
            if (feedState.tfa != null) {
                Text(
                    "Featured Article",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp)
                )
                Text(
                    text = remember { LocalDate.now().format(dtf) },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
        item {
            if (feedState.tfa != null) {
                ElevatedCard(
                    onClick = { performSearch(feedState.tfa.titles.canonical) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    FeedImage(
                        source = feedState.tfa.originalImage.source,
                        description = feedState.tfa.titles.normalized,
                        width = feedState.tfa.originalImage.width,
                        height = feedState.tfa.originalImage.height,
                        imageLoader = imageLoader,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        feedState.tfa.titles.normalized,
                        style = MaterialTheme.typography.headlineMedium,
                        fontFamily = FontFamily.Serif,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp)
                    )
                    Text(
                        feedState.tfa.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                    Text(
                        feedState.tfa.extract,
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 8.dp, bottom = 16.dp)
                    )
                }
            }
        }
        item {
            if (feedState.mostReadArticles != null) {
                Text(
                    "Most Read",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 32.dp)
                )
                Text(
                    "Top articles of the day",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    for (i in 0..4) {
                        key(i) {
                            Row(
                                modifier = Modifier
                                    .clickable(
                                        onClick = {
                                            performSearch(feedState.mostReadArticles[i].titles.normalized)
                                        }
                                    )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 16.dp)
                                ) {
                                    Text(
                                        feedState.mostReadArticles[i].titles.normalized,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(top = 16.dp)
                                    )
                                    Text(
                                        feedState.mostReadArticles[i].description,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    ) {
                                        ArticleViewsGraph(
                                            feedState.mostReadArticles[i].viewHistory.map { it.views },
                                            modifier = Modifier
                                                .size(width = 96.dp, height = 32.dp)
                                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                        )
                                        Text(
                                            df.format(feedState.mostReadArticles[i].views),
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                if (feedState.mostReadArticles[i].thumbnail != null)
                                    FeedImage(
                                        source = feedState.mostReadArticles[i].thumbnail!!.source,
                                        description = feedState.mostReadArticles[i].titles.normalized,
                                        imageLoader = imageLoader,
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .clip(MaterialTheme.shapes.large)
                                            .size(80.dp, 80.dp)
                                    )
                            }
                            if (i != 4) HorizontalDivider()
                        }
                    }
                }
            }
        }
        item {
            if (feedState.image != null) {
                Text(
                    "Picture of the Day",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 32.dp)
                )
                ElevatedCard(
                    onClick = { uriHandler.openUri(feedState.image.filePage) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    FeedImage(
                        source = feedState.image.image.source,
                        description = feedState.image.description.text,
                        width = feedState.image.image.width,
                        height = feedState.image.image.height,
                        imageLoader = imageLoader,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        feedState.image.description.text,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp)
                    )
                    Text(
                        "By " + feedState.image.artist.text +
                                " (" + feedState.image.credit.text.substringBefore(';') + ")",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 8.dp, bottom = 16.dp)
                    )
                }
            }
        }
        item {
            if (feedState.news != null) {
                val carouselState = rememberCarouselState(0) { feedState.news.size }
                Text(
                    "In the News",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 32.dp)
                )
                HorizontalMultiBrowseCarousel(
                    state = carouselState,
                    itemSpacing = 16.dp,
                    modifier = Modifier
                        .padding(16.dp)
                        .aspectRatio(1f),
                    preferredItemWidth = LocalConfiguration.current.screenWidthDp.dp - 32.dp
                ) { i ->
                    Box {
                        FeedImage(
                            source = feedState.news[i].links
                                .find { it.originalImage != null }
                            !!.originalImage!!.source,
                            description = null,
                            imageLoader = imageLoader,
                            modifier = Modifier
                                .fillMaxWidth()
                                .maskClip(MaterialTheme.shapes.extraLarge)
                        )
                        Box(
                            modifier = Modifier
                                .maskClip(MaterialTheme.shapes.extraLarge)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black
                                        )
                                    )
                                )
                                .fillMaxSize()
                        ) {}
                        Column(modifier = Modifier.align(Alignment.BottomStart)) {
                            Text(
                                feedState.news[i].story.parseAsHtml().toString(),
                                color = Color.White,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .fillMaxWidth()
                            )
                            FlowRow(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        top = 8.dp,
                                        bottom = 16.dp,
                                        start = 16.dp,
                                        end = 16.dp
                                    )
                                    .wrapContentHeight(align = Alignment.Top),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                feedState.news[i].links
                                    // Sort the list to optimize the arrangement of elements
                                    .sortedBy { it.titles.normalized.utf8Size() }
                                    .subList(0, min(3, feedState.news[i].links.size))
                                    .forEach {
                                        OutlinedButton(
                                            border = BorderStroke(
                                                width = ButtonDefaults.outlinedButtonBorder().width,
                                                color = Color.LightGray
                                            ),
                                            onClick = { performSearch(it.titles.canonical) }
                                        ) {
                                            Text(
                                                it.titles.normalized,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                color = Color.White
                                            )
                                        }
                                    }
                            }
                        }
                    }
                }
            }
        }
        item {
            if (feedState.onThisDay != null) {
                val carouselState = rememberCarouselState(0) { feedState.onThisDay.size }
                Text(
                    "On This Day",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 32.dp)
                )
                HorizontalMultiBrowseCarousel(
                    state = carouselState,
                    itemSpacing = 16.dp,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .aspectRatio(0.9f),
                    preferredItemWidth = LocalConfiguration.current.screenWidthDp.dp - 32.dp
                ) { i ->
                    Column {
                        Text(
                            feedState.onThisDay[i].year.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Box {
                            FeedImage(
                                source = feedState.onThisDay[i].pages
                                    .find { it.originalImage != null }
                                !!.originalImage!!.source,
                                description = null,
                                imageLoader = imageLoader,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .maskClip(MaterialTheme.shapes.extraLarge)
                            )
                            Box(
                                modifier = Modifier
                                    .maskClip(MaterialTheme.shapes.extraLarge)
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black
                                            )
                                        )
                                    )
                                    .fillMaxSize()
                            ) {}
                            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                                Text(
                                    feedState.onThisDay[i].text,
                                    color = Color.White,
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .fillMaxWidth()
                                )
                                FlowRow(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            top = 8.dp,
                                            bottom = 16.dp,
                                            start = 16.dp,
                                            end = 16.dp
                                        )
                                        .wrapContentHeight(align = Alignment.Top),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    feedState.onThisDay[i].pages
                                        // Sort the list to optimize the arrangement of elements
                                        .sortedBy { it.titles.normalized.utf8Size() }
                                        .subList(0, min(3, feedState.onThisDay[i].pages.size))
                                        .forEach {
                                            OutlinedButton(
                                                border = BorderStroke(
                                                    width = ButtonDefaults.outlinedButtonBorder().width,
                                                    color = Color.LightGray
                                                ),
                                                onClick = { performSearch(it.titles.canonical) }
                                            ) {
                                                Text(
                                                    it.titles.normalized,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                }
                            }
                        }
                    }
                }
            }
        }
        item {
            Spacer(Modifier.height(insets.calculateBottomPadding() + 152.dp))
        }
    }
}
