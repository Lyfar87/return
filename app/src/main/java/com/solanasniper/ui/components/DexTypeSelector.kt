package com.solanasniper.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.solanasniper.R
import com.solanasniper.domain.model.DexType

@Composable
fun DexTypeSelector(
    selected: DexType,
    onSelect: (DexType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DexType.values().forEach { dexType ->
            DexTypeChip(
                type = dexType,
                isSelected = dexType == selected,
                onSelected = { onSelect(dexType) }
            )
        }
    }
}

@Composable
private fun DexTypeChip(
    type: DexType,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    val (iconRes, label) = when (type) {
        DexType.RAYDIUM -> Pair(R.drawable.ic_raydium, "Raydium")
        DexType.JUPITER -> Pair(R.drawable.ic_jupiter, "Jupiter")
    }

    FilterChip(
        selected = isSelected,
        onClick = onSelected,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = ImageVector.vectorResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

// Предпросмотр компонента
@Preview(showBackground = true)
@Composable
private fun DexTypeSelectorPreview() {
    MaterialTheme {
        DexTypeSelector(
            selected = DexType.RAYDIUM,
            onSelect = {}
        )
    }
}