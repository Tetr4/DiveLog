package cloud.mike.divelog.bluetooth.utils

import android.annotation.SuppressLint
import io.reactivex.Observable

@SuppressLint("CheckResult")
internal fun <T> Observable<T>.subscribeForever(onNext: (value: T) -> Unit) {
    this.subscribe(onNext)
}