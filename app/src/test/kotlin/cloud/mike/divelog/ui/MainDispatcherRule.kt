package cloud.mike.divelog.ui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Replaces [Dispatchers.Main] before each test with a [StandardTestDispatcher] that can skip delays.
 *
 * See [Testing Coroutines](https://developer.android.com/kotlin/coroutines/test#setting-main-dispatcher).
 *
 * Usage:
 * ```kotlin
 * @get:Rule
 * var mainDispatcherRule = MainDispatcherRule()
 * ```
 *
 * @see [TestScope.runTest]
 * @see [TestScope.runCurrent]
 * @see [TestScope.advanceTimeBy]
 * @see [TestScope.advanceUntilIdle]
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = StandardTestDispatcher(),
) : TestWatcher() {

    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}
