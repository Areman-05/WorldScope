package com.example.worldscope.ui.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.worldscope.R
import com.example.worldscope.data.local.entity.FavoriteCountryEntity
import com.example.worldscope.ui.theme.WsGreen
import com.example.worldscope.ui.theme.WsGreenDark
import com.example.worldscope.ui.theme.WsSurfaceSoft

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onCountryClick: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    viewModel: FavoritesViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val groupedCodes = state.groups.flatMap { it.countryCodes }.toSet()
    val freeFavorites = state.favorites.filterNot { groupedCodes.contains(it.alpha2Code) }
    var selectedGroupId by remember { mutableStateOf<Long?>(null) }
    var addSearchQuery by remember { mutableStateOf("") }
    var showAddPanel by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = WsSurfaceSoft,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(WsGreen, WsGreenDark)
                        )
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("favorites_topbar")
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        val planetAnim = rememberInfiniteTransition(label = "favorites_planet_anim")
                        val rotation by planetAnim.animateFloat(
                            initialValue = 0f,
                            targetValue = 360f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMillis = 9000),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "favorites_planet_rotation"
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .rotate(rotation)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                Color(0xFFFFF59D),
                                                Color(0xFFFBC02D)
                                            )
                                        )
                                    )
                                    .padding(7.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Public,
                                    contentDescription = null,
                                    tint = WsGreenDark
                                )
                            }
                            Text(
                                text = stringResource(R.string.favorites),
                                color = Color.White,
                                style = androidx.compose.material3.MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    },
                    colors = androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    ),
                    navigationIcon = {},
                    actions = {}
                )
            }
        }
    ) { paddingValues ->
        AnimatedVisibility(
            visible = !state.hasLoaded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .testTag("favorites_loading"),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    tonalElevation = 2.dp
                ) {
                    Text(
                        stringResource(R.string.loading),
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                        color = WsGreenDark
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = state.hasLoaded && state.favorites.isEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .testTag("favorites_empty_container"),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    tonalElevation = 2.dp,
                    modifier = Modifier.padding(horizontal = 18.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 22.dp, vertical = 20.dp)
                            .testTag("favorites_empty_content"),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "💚",
                            style = androidx.compose.material3.MaterialTheme.typography.headlineLarge
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            stringResource(R.string.no_favorites),
                            modifier = Modifier.testTag("favorites_empty"),
                            color = WsGreenDark,
                            fontWeight = FontWeight.SemiBold
                        )
                        Button(
                            onClick = onBackClick,
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .testTag("favorites_go_countries"),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = WsGreenDark,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                stringResource(R.string.countries),
                                modifier = Modifier.testTag("favorites_go_countries_text")
                            )
                        }
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = state.hasLoaded && state.favorites.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .testTag("favorites_list"),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(14.dp),
                        tonalElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Listas personalizadas",
                                color = WsGreenDark,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = state.newGroupName,
                                    onValueChange = viewModel::updateNewGroupName,
                                    modifier = Modifier.weight(1f),
                                    placeholder = { Text("Nombre de la lista") },
                                    singleLine = true
                                )
                                Button(
                                    onClick = viewModel::createGroup,
                                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                        containerColor = WsGreenDark,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Icon(Icons.Filled.Add, contentDescription = null)
                                }
                            }
                        }
                    }
                }
                items(state.groups, key = { it.id }) { group ->
                    GroupSummaryCard(
                        group = group,
                        itemCount = group.countryCodes.size,
                        onOpen = {
                            selectedGroupId = group.id
                            addSearchQuery = ""
                            showAddPanel = false
                        }
                    )
                }
                item {
                    Text(
                        text = "Favoritos libres",
                        color = WsGreenDark,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier
                            .padding(top = 6.dp, start = 4.dp)
                            .testTag("favorites_free_title")
                    )
                }
                items(freeFavorites, key = { it.alpha2Code }) { favorite ->
                    FavoriteItem(
                        favorite = favorite,
                        onClick = {
                            val code = favorite.alpha2Code
                            if (code.isNotBlank()) onCountryClick(code)
                        },
                        onRemoveClick = { viewModel.removeFavorite(favorite.alpha2Code) }
                    )
                }
            }
        }
    }

    val selectedGroup = state.groups.firstOrNull { it.id == selectedGroupId }
    if (selectedGroup != null) {
        val favoritesInGroup = state.favorites.filter { selectedGroup.countryCodes.contains(it.alpha2Code) }
        val candidatesToAdd = state.favorites.filterNot { selectedGroup.countryCodes.contains(it.alpha2Code) }
            .filter { it.name.contains(addSearchQuery, ignoreCase = true) }
        GroupDetailDialog(
            groupName = selectedGroup.name,
            favoritesInGroup = favoritesInGroup,
            candidatesToAdd = candidatesToAdd,
            addSearchQuery = addSearchQuery,
            showAddPanel = showAddPanel,
            onDismiss = { selectedGroupId = null },
            onDeleteGroup = {
                viewModel.removeGroup(selectedGroup.id)
                selectedGroupId = null
            },
            onToggleAddPanel = { showAddPanel = !showAddPanel },
            onAddSearchQueryChange = { addSearchQuery = it },
            onAddCountry = { country ->
                viewModel.addCountryToGroup(selectedGroup.id, country.alpha2Code)
            },
            onRemoveCountry = { country ->
                viewModel.removeCountryFromGroup(selectedGroup.id, country.alpha2Code)
            },
            onCountryClick = onCountryClick
        )
    }
}

@Composable
private fun GroupSummaryCard(
    group: FavoriteGroup,
    itemCount: Int,
    onOpen: () -> Unit
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = group.name,
                    color = WsGreenDark,
                    fontWeight = FontWeight.Bold,
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "$itemCount paises en la lista",
                    color = Color(0xFF5C6B5C)
                )
            }
            Button(
                onClick = onOpen,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = WsGreenDark,
                    contentColor = Color.White
                )
            ) {
                Text("Abrir")
            }
        }
    }
}

@Composable
private fun GroupDetailDialog(
    groupName: String,
    favoritesInGroup: List<FavoriteCountryEntity>,
    candidatesToAdd: List<FavoriteCountryEntity>,
    addSearchQuery: String,
    showAddPanel: Boolean,
    onDismiss: () -> Unit,
    onDeleteGroup: () -> Unit,
    onToggleAddPanel: () -> Unit,
    onAddSearchQueryChange: (String) -> Unit,
    onAddCountry: (FavoriteCountryEntity) -> Unit,
    onRemoveCountry: (FavoriteCountryEntity) -> Unit,
    onCountryClick: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = groupName,
                        color = WsGreenDark,
                        fontWeight = FontWeight.ExtraBold,
                        style = androidx.compose.material3.MaterialTheme.typography.titleLarge
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onToggleAddPanel) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Añadir paises",
                                tint = WsGreenDark
                            )
                        }
                        IconButton(onClick = onDeleteGroup) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Eliminar grupo",
                                tint = Color(0xFFC62828)
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Cerrar",
                                tint = Color(0xFF666666)
                            )
                        }
                    }
                }

                AnimatedVisibility(visible = showAddPanel, enter = fadeIn(), exit = fadeOut()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = addSearchQuery,
                            onValueChange = onAddSearchQueryChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Buscar pais para anadir") },
                            singleLine = true
                        )
                        if (candidatesToAdd.isEmpty()) {
                            Text("No hay paises disponibles con esa busqueda.")
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 180.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(candidatesToAdd, key = { it.alpha2Code }) { country ->
                                    Surface(
                                        color = Color(0xFFF7FBF7),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 10.dp, vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(country.name, color = WsGreenDark)
                                            IconButton(onClick = { onAddCountry(country) }) {
                                                Icon(
                                                    imageVector = Icons.Filled.Add,
                                                    contentDescription = "Anadir a la lista",
                                                    tint = WsGreen
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Text(
                    text = "Paises dentro de la lista",
                    color = WsGreenDark,
                    fontWeight = FontWeight.SemiBold
                )
                if (favoritesInGroup.isEmpty()) {
                    Text("Esta lista esta vacia. Pulsa + para anadir paises.")
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(favoritesInGroup, key = { it.alpha2Code }) { country ->
                            Surface(
                                color = Color(0xFFF7FBF7),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = country.name,
                                        color = WsGreenDark,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { onCountryClick(country.alpha2Code) }
                                    )
                                    IconButton(onClick = { onRemoveCountry(country) }) {
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = "Quitar de la lista",
                                            tint = Color(0xFFC62828)
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
}
