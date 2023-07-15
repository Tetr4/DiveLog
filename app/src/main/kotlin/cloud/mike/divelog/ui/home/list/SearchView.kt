package cloud.mike.divelog.ui.home.list

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.themeadapter.material3.Mdc3Theme

@Composable
fun SearchView(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "",
            )
        },
        trailingIcon = {
            if (value.isNotBlank()) {
                IconButton(
                    onClick = { onValueChange("") },
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "",
                    )
                }
            }
        },
        singleLine = true,
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    Mdc3Theme {
        SearchView(
            value = "",
            onValueChange = {},
            placeholder = "lorem ipsum",
        )
    }
}
