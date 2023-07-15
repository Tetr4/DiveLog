package cloud.mike.divelog.ui.home.list

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.ui.home.list.item.DiveItem
import com.google.accompanist.themeadapter.material3.Mdc3Theme

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
            placeholder = "Tauchgänge suchen",
        )
        TagFilters(
            // we don't add this to contentPadding, because it should be scrollable to the edge of the screen
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        LazyColumn(
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // TODO sticky header for dates?
            itemsIndexed(
                items = dives,
                key = { _, dive -> dive.id },
            ) { _, dive ->
                DiveItem(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    dive = dive,
                    onClick = { onDiveClicked(dive) },
                )
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    Mdc3Theme {
        DiveList(
            dives = Dive.samples,
            onDiveClicked = {},
            query = "",
            onQueryChanged = {},
        )
    }
}
