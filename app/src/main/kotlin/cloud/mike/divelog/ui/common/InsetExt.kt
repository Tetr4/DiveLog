package cloud.mike.divelog.ui.common

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.union
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

// The default insets don't include the display cutout for some reason.

val ScaffoldDefaults.contentWindowInsetsWithCutout: WindowInsets
    @Composable
    get() = contentWindowInsets.union(WindowInsets.displayCutout)

@OptIn(ExperimentalMaterial3Api::class)
val TopAppBarDefaults.windowInsetsWithCutout: WindowInsets
    @Composable
    get() = windowInsets.union(WindowInsets.displayCutout)
        .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)

val BottomAppBarDefaults.windowInsetsWithCutout
    @Composable
    get() = windowInsets.union(WindowInsets.displayCutout)
        .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
