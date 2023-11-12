package cloud.mike.divelog.ui.common.form

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cloud.mike.divelog.ui.DiveTheme

@Composable
fun FormTextButton(
    onClick: () -> Unit,
    value: String,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.semantics { role = Role.Button },
    ) {
        Box(
            modifier = Modifier.defaultMinSize(
                minWidth = ButtonDefaults.MinWidth,
                minHeight = 48.dp,
            ),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                modifier = Modifier.padding(vertical = 16.dp),
                text = value.ifBlank { placeholder },
                color = if (value.isBlank()) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewEmpty() {
    DiveTheme {
        FormTextButton(
            value = "",
            placeholder = "Placeholder",
            onClick = {},
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    DiveTheme {
        FormTextButton(
            value = "Lorem Ipsum",
            placeholder = "Placeholder",
            onClick = {},
        )
    }
}
