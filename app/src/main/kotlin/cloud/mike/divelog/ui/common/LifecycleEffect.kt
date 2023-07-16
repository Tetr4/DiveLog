package cloud.mike.divelog.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
fun LifecycleEffect(event: Lifecycle.Event, block: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val eventObserver = LifecycleEventObserver { _, currentEvent ->
        if (currentEvent == event) block()
    }
    DisposableEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(eventObserver)
        onDispose { lifecycleOwner.lifecycle.removeObserver(eventObserver) }
    }
}
