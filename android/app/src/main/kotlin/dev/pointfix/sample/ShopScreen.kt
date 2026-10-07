package dev.pointfix.sample

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.layout
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val BannerInk = Color(0xFF16181A)
private val Heart = Color(0xFFE5484D)

@Composable
fun ShopScreen() {
    val cs = MaterialTheme.colorScheme
    var query by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("All") }
    var saleOnly by rememberSaveable { mutableStateOf(false) }
    var bag by rememberSaveable { mutableIntStateOf(2) }
    var tab by rememberSaveable { mutableIntStateOf(0) }
    val favorites = remember { mutableStateListOf("cloud-fleece-hoodie") }
    val visible = products.filter {
        (category == "All" || it.category == category) && (!saleOnly || it.oldPrice != null) &&
            it.name.contains(query.trim(), ignoreCase = true)
    }
    val navBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(Modifier.fillMaxSize().background(cs.background)) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = navBottom + 108.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            fullWidth { Header(bag) }
            fullWidth { SearchRow(query, { query = it }, saleOnly) { saleOnly = !saleOnly } }
            fullWidth { PromoPager() }
            fullWidth { CategoryRow(category) { category = it } }
            fullWidth {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Special For You", Modifier.weight(1f), fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = cs.onBackground)
                    Text("See All", Modifier.clip(RoundedCornerShape(8.dp)).clickable { category = "All"; query = ""; saleOnly = false }
                        .padding(4.dp).testTag("shop.seeAll"), fontSize = 14.sp, color = cs.onSurfaceVariant)
                }
            }
            items(visible, key = { it.slug }) { p ->
                ProductCard(p, p.slug in favorites,
                    onFavorite = { if (!favorites.remove(p.slug)) favorites.add(p.slug) },
                    onAdd = { bag++ })
            }
            if (visible.isEmpty()) fullWidth {
                Text("No pieces match \"${query.trim()}\"", Modifier.fillMaxWidth().padding(vertical = 32.dp),
                    color = cs.onSurfaceVariant, fontSize = 14.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        }
        BottomBar(tab, { tab = it }, Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp))
    }
}

private fun LazyGridScope.fullWidth(content: @Composable () -> Unit) = item(span = { GridItemSpan(maxLineSpan) }) { content() }

@Composable
private fun Header(bag: Int) {
    val cs = MaterialTheme.colorScheme
    Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(46.dp).clip(CircleShape).background(Lime).testTag("profile.avatar"), contentAlignment = Alignment.Center) {
            Text("AM", color = Ink, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f).testTag("profile.greeting")) {
            Text("Welcome back", fontSize = 13.sp, color = cs.onSurfaceVariant)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Hey, Alex", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = cs.onBackground)
                Icon(Icons.Rounded.KeyboardArrowDown, "Switch account", Modifier.size(22.dp), tint = cs.onBackground)
            }
        }
        BagButton(bag)
    }
}

@Composable
private fun BagButton(count: Int) {
    val cs = MaterialTheme.colorScheme
    val bump = remember { Animatable(1f) }
    var shown by remember { mutableIntStateOf(count) }
    LaunchedEffect(count) {
        if (count != shown) { shown = count; bump.snapTo(1.35f); bump.animateTo(1f, spring(0.45f, Spring.StiffnessMediumLow)) }
    }
    Box(Modifier.size(50.dp)) {
        Box(Modifier.size(48.dp).align(Alignment.BottomStart).clip(CircleShape).background(cs.surface).clickable {}.testTag("shop.bag"),
            contentAlignment = Alignment.Center) {
            Icon(Icons.Rounded.ShoppingCart, "Bag", Modifier.size(22.dp), tint = cs.onSurface)
        }
        Box(Modifier.align(Alignment.TopEnd).scale(bump.value).size(20.dp).clip(CircleShape).background(Lime)
            .border(2.dp, cs.background, CircleShape).testTag("bag.count").semantics { contentDescription = "$count items in bag" },
            contentAlignment = Alignment.Center) {
            Text("$count", color = Ink, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SearchRow(query: String, onQuery: (String) -> Unit, saleOnly: Boolean, onFilter: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Row(Modifier.fillMaxWidth().height(54.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(18.dp)).background(cs.surface).padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Search, null, Modifier.size(22.dp), tint = cs.onSurfaceVariant)
            Spacer(Modifier.width(10.dp))
            Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                if (query.isEmpty()) Text("Explore Fashion", color = cs.onSurfaceVariant, fontSize = 15.sp)
                BasicTextField(query, onQuery, Modifier.fillMaxWidth().testTag("shop.search").semantics { contentDescription = "Search products" },
                    singleLine = true, textStyle = TextStyle(color = cs.onSurface, fontSize = 15.sp),
                    cursorBrush = SolidColor(cs.onSurface), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search))
            }
            if (query.isNotEmpty()) Icon(Icons.Rounded.Close, "Clear search",
                Modifier.size(20.dp).clip(CircleShape).clickable { onQuery("") }, tint = cs.onSurfaceVariant)
        }
        val bg by animateColorAsState(if (saleOnly) cs.onSurface else Lime, label = "filter")
        Box(Modifier.size(54.dp).clip(RoundedCornerShape(18.dp)).background(bg).clickable(onClick = onFilter).testTag("shop.filter")
            .semantics { contentDescription = "Show sale items only"; stateDescription = if (saleOnly) "On" else "Off" },
            contentAlignment = Alignment.Center) {
            val glyph = if (saleOnly) Lime else Ink
            Canvas(Modifier.size(20.dp)) {
                val w = size.width; val stroke = w * 0.1f
                listOf(0.2f to 0.68f, 0.5f to 0.3f, 0.8f to 0.6f).forEach { (y, x) ->
                    drawLine(glyph, Offset(0f, y * w), Offset(w, y * w), stroke, androidx.compose.ui.graphics.StrokeCap.Round)
                    drawCircle(bg, w * 0.15f, Offset(x * w, y * w)); drawCircle(glyph, w * 0.15f, Offset(x * w, y * w), style = Stroke(stroke))
                }
            }
        }
    }
}

private data class Promo(val eyebrow: String, val title: String, val highlight: String)
private val promos = listOf(
    Promo("Super Sale Discount", "Up to ", "50%"),
    Promo("New Season Drop", "Denim from ", "\$58"),
    Promo("Members Weekend", "Free ", "shipping"),
)

@Composable
private fun PromoPager() {
    val pager = rememberPagerState { promos.size }
    LaunchedEffect(pager) {
        while (true) {
            delay(4_000)
            runCatching { pager.animateScrollToPage((pager.currentPage + 1) % promos.size) } // a user swipe cancels this hop, not the loop
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        HorizontalPager(pager, Modifier.fillMaxWidth().height(172.dp), pageSpacing = 12.dp) { PromoCard(promos[it]) }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            repeat(promos.size) { i ->
                val active = pager.currentPage == i
                val w by animateDpAsState(if (active) 20.dp else 6.dp, label = "dot")
                Box(Modifier.padding(horizontal = 3.dp).size(w, 6.dp).clip(CircleShape)
                    .background(if (active) Lime else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)))
            }
        }
    }
}

@Composable
private fun PromoCard(promo: Promo) {
    Box(Modifier.fillMaxSize().clip(RoundedCornerShape(24.dp)).background(BannerInk)) {
        Canvas(Modifier.matchParentSize()) {
            val c = Offset(size.width * 0.86f, size.height * 0.62f)
            for (i in 0 until 6) {
                val r = size.height * (0.16f + i * 0.13f)
                drawArc(Lime.copy(alpha = 0.85f - i * 0.13f), 120f, 260f, false, Offset(c.x - r, c.y - r), Size(r * 2, r * 2),
                    style = Stroke(width = size.height * 0.018f))
            }
            drawCircle(Lime, size.height * 0.09f, c)
            drawCircle(Lime.copy(alpha = 0.6f), size.height * 0.025f, Offset(size.width * 0.6f, size.height * 0.18f))
        }
        Column(Modifier.fillMaxHeight().padding(20.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(promo.eyebrow, color = Color.White.copy(alpha = 0.72f), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(4.dp))
                Text(buildAnnotatedString { append(promo.title); withStyle(SpanStyle(color = Lime)) { append(promo.highlight) } },
                    color = Color.White, fontSize = 30.sp, lineHeight = 34.sp, fontWeight = FontWeight.Bold)
            }
            Row(Modifier.clip(CircleShape).background(Lime).clickable {}.testTag("promo.shopNow").padding(start = 16.dp, end = 12.dp, top = 9.dp, bottom = 9.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text("Shop Now", color = Ink, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(6.dp))
                Icon(Icons.AutoMirrored.Rounded.ArrowForward, null, Modifier.size(16.dp), tint = Ink)
            }
        }
    }
}

@Composable
private fun CategoryRow(selected: String, onSelect: (String) -> Unit) {
    val cs = MaterialTheme.colorScheme
    // Bleed the chip row past the grid's 20dp side padding so chips scroll edge to edge.
    LazyRow(Modifier.layout { m, c ->
        val bleed = 20.dp.roundToPx()
        val p = m.measure(c.copy(minWidth = c.maxWidth + 2 * bleed, maxWidth = c.maxWidth + 2 * bleed))
        layout(c.maxWidth, p.height) { p.place(-bleed, 0) }
    }, contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(categories) { name ->
            val on = name == selected
            Row(Modifier.clip(CircleShape).background(if (on) cs.onSurface else cs.surface).clickable { onSelect(name) }
                .testTag("category.${name.lowercase()}").semantics { this.selected = on }
                .padding(horizontal = 18.dp, vertical = 11.dp), verticalAlignment = Alignment.CenterVertically) {
                if (on) Text("✦ ", color = Lime, fontSize = 13.sp)
                Text(name, color = if (on) cs.surface else cs.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun ProductCard(p: Product, favorite: Boolean, onFavorite: () -> Unit, onAdd: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val dark = cs.surface.luminance() < 0.5f
    Column(Modifier.clip(RoundedCornerShape(22.dp)).background(cs.surface).testTag("product.${p.slug}").padding(8.dp)) {
        Box(Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(16.dp)).background(if (dark) lerp(p.tile, Color.Black, 0.62f) else p.tile)) {
            Garment(p.kind, p.color, Modifier.matchParentSize().padding(24.dp))
            p.oldPrice?.let { old ->
                Text("-${(old - p.price) * 100 / old}%", Modifier.padding(10.dp).clip(CircleShape).background(Ink).padding(horizontal = 8.dp, vertical = 3.dp),
                    color = Lime, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Box(Modifier.align(Alignment.TopEnd).padding(8.dp).size(32.dp).clip(CircleShape).background(cs.surface.copy(alpha = 0.92f))
                .clickable(onClick = onFavorite).testTag("product.${p.slug}.favorite")
                .semantics { contentDescription = "Favorite ${p.name}"; stateDescription = if (favorite) "Saved" else "Not saved" },
                contentAlignment = Alignment.Center) {
                Icon(if (favorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder, null, Modifier.size(18.dp),
                    tint = if (favorite) Heart else cs.onSurface)
            }
        }
        Column(Modifier.padding(start = 6.dp, end = 2.dp, top = 10.dp, bottom = 2.dp)) {
            Text(p.category, fontSize = 12.sp, color = cs.onSurfaceVariant)
            // Intentional demo flaw: long product names are hard-clipped mid-word (no ellipsis, no second line),
            // so "Oversized Corduroy Bomber" renders as a truncated fragment. Fix: maxLines = 2 or TextOverflow.Ellipsis.
            Text(p.name, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = cs.onSurface, maxLines = 1, softWrap = false, overflow = TextOverflow.Clip)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("\$${p.price}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = cs.onSurface)
                p.oldPrice?.let {
                    Text("\$$it", Modifier.padding(start = 6.dp), fontSize = 12.sp, color = cs.onSurfaceVariant, textDecoration = TextDecoration.LineThrough)
                }
                Spacer(Modifier.weight(1f))
                Box(Modifier.size(34.dp).clip(CircleShape).background(cs.onSurface).clickable(onClick = onAdd)
                    .testTag("product.${p.slug}.add").semantics { contentDescription = "Add ${p.name} to bag" }, contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Add, null, Modifier.size(18.dp), tint = cs.surface)
                }
            }
        }
    }
}

private data class Tab(val label: String, val icon: ImageVector, val tag: String)
private val tabs = listOf(
    Tab("Home", Icons.Rounded.Home, "nav.home"), Tab("Bag", Icons.Rounded.ShoppingCart, "nav.bag"),
    Tab("Favorites", Icons.Rounded.FavoriteBorder, "nav.favorites"), Tab("Profile", Icons.Rounded.Person, "nav.profile"),
)

@Composable
private fun BottomBar(selected: Int, onSelect: (Int) -> Unit, modifier: Modifier) {
    val bar = if (MaterialTheme.colorScheme.surface.luminance() < 0.5f) Color(0xFF242726) else Ink
    Row(modifier.fillMaxWidth().height(68.dp).shadow(18.dp, CircleShape).clip(CircleShape).background(bar).padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        tabs.forEachIndexed { i, t ->
            val on = i == selected
            Row(Modifier.fillMaxHeight().clip(CircleShape).background(if (on) Lime else Color.Transparent).clickable { onSelect(i) }
                .testTag(t.tag).semantics(mergeDescendants = true) { contentDescription = t.label; this.selected = on }
                .padding(horizontal = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(t.icon, null, Modifier.size(22.dp), tint = if (on) Ink else Color.White.copy(alpha = 0.6f))
                AnimatedVisibility(on) { Text(t.label, Modifier.padding(start = 8.dp), color = Ink, fontSize = 14.sp, fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}
