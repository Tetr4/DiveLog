package cloud.mike.divelog.ui.home.list

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ScubaDiving
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cloud.mike.divelog.R
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.ui.DiveTheme

@Composable
fun DiveList(
    dives: List<Dive>,
    onDiveClicked: (Dive) -> Unit,
    modifier: Modifier = Modifier,
    query: String,
    onQueryChanged: (String) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SearchView(
            modifier = Modifier.padding(horizontal = 16.dp),
            value = query,
            onValueChange = onQueryChanged,
            placeholder = stringResource(R.string.home_search_placeholder),
        )
        TagFilters(
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        if (dives.isEmpty()) {
            EmptyState()
        } else {
            ListView(
                dives = dives,
                onDiveClicked = onDiveClicked,
            )
        }
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.home_label_empty),
            style = MaterialTheme.typography.headlineLarge,
        )
        Spacer(Modifier.height(32.dp))
        Icon(
            modifier = Modifier
                .fillMaxSize(.5f)
                .padding(32.dp),
            imageVector = Icons.Default.ScubaDiving,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun ListView(
    dives: List<Dive>,
    onDiveClicked: (Dive) -> Unit,
) {
    LazyColumn {
        // TODO sticky header for dates?
        itemsIndexed(
            items = dives,
            key = { _, dive -> dive.id },
        ) { index, dive ->
            DiveItem(
                modifier = Modifier.padding(horizontal = 16.dp),
                dive = dive,
                onClick = { onDiveClicked(dive) },
            )
            if (index != dives.lastIndex) {
                Divider()
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    DiveTheme {
        DiveList(
            dives = Dive.samples,
            onDiveClicked = {},
            query = "",
            onQueryChanged = {},
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewEmpty() {
    DiveTheme {
        DiveList(
            dives = emptyList(),
            onDiveClicked = {},
            query = "",
            onQueryChanged = {},
        )
    }
}
