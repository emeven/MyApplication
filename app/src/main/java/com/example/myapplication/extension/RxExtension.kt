package com.example.myapplication.extension

import android.view.View
import com.google.common.base.Optional
import com.jakewharton.rxbinding3.view.clicks
import com.uber.autodispose.MaybeSubscribeProxy
import com.uber.autodispose.ObservableSubscribeProxy
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.autoDisposable
import io.reactivex.*
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.OnErrorNotImplementedException
import io.reactivex.observers.DisposableMaybeObserver
import io.reactivex.observers.DisposableObserver
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit
import kotlin.reflect.KFunction0

class EmptyObserver<T> : DisposableObserver<T>() {
    override fun onComplete() {}

    override fun onNext(t: T) {}

    override fun onError(e: Throwable) {}

}

class CrashOnErrorObserver<T>(val next: (t: T) -> Unit) : DisposableObserver<T>() {

    override fun onComplete() {}

    override fun onNext(t: T) {
        next(t)
    }

    override fun onError(e: Throwable) {
        (e as? RuntimeException)?.let {
            throw it
        } ?: run {
            throw OnErrorNotImplementedException(e)
        }
    }
}

class CrashOnErrorMaybeObserver<T>(val success: (t: T) -> Unit) : DisposableMaybeObserver<T>() {

    override fun onComplete() {}

    override fun onError(e: Throwable) {
        (e as? RuntimeException)?.let {
            throw it
        } ?: run {
            throw OnErrorNotImplementedException(e)
        }
    }

    override fun onSuccess(t: T) = success(t)
}

fun <T> Observable<Optional<T>>.filterAndGet(): Observable<T> = this.filter { it.isPresent }.map { it.get() }

fun <T> Single<Optional<T>>.filterAndGet(): Maybe<T> = this.filter { it.isPresent }.map { it.get() }

fun <T> ObservableSubscribeProxy<T>.subscribeWithCrash(next: (t: T) -> Unit) = this.subscribe(CrashOnErrorObserver<T>(next))
// kotlin 中使用无返回值的 java 方法没有办法使用 Callable References 的形式，所以加了对应的方法 KFunction0<Unit>
fun <T> ObservableSubscribeProxy<T>.subscribeWithCrash(next: KFunction0<Unit>) = this.subscribe(CrashOnErrorObserver<T> { next.invoke() })

fun <T> MaybeSubscribeProxy<T>.subscribeWithCrash(next: (t: T) -> Unit) = this.subscribe(CrashOnErrorMaybeObserver<T>(next))
// kotlin 中使用无返回值的 java 方法没有办法使用 Callable References 的形式，所以加了对应的方法 KFunction0<Unit>
fun <T> MaybeSubscribeProxy<T>.subscribeWithCrash(next: KFunction0<Unit>) = this.subscribe(CrashOnErrorMaybeObserver<T> { next.invoke() })

fun <T> Observable<T>.subscribeWithCrash(provider: ScopeProvider, next: (t: T) -> Unit) = this.autoDisposable(provider).subscribeWithCrash(next)
fun <T> Observable<T>.subscribeWithProvider(provider: ScopeProvider, next: (t: T) -> Unit, error: (t: Throwable) -> Unit): Disposable = this.autoDisposable(provider).subscribe(next, error)
fun <T> Maybe<T>.subscribeWithCrash(provider: ScopeProvider, next: (t: T) -> Unit) = this.autoDisposable(provider).subscribeWithCrash(next)
fun <T> Maybe<T>.subscribeWithProvider(provider: ScopeProvider, next: (t: T) -> Unit, error: (t: Throwable) -> Unit): Disposable = this.autoDisposable(provider).subscribe(next, error)
fun <T> Maybe<T>.subscribe(provider: ScopeProvider, next: (t: T) -> Unit, error: (t: Throwable) -> Unit): Disposable = this.autoDisposable(provider).subscribe(next, error)

// kotlin 中使用无返回值的 java 方法没有办法使用 Callable References 的形式，所以加了对应的方法 KFunction0<Unit>
fun <T> Observable<T>.subscribeWithCrash(provider: ScopeProvider, next: KFunction0<Unit>) = this.autoDisposable(provider).subscribeWithCrash(next)
fun <T> Observable<T>.subscribe(provider: ScopeProvider, next: KFunction0<Unit>, error: (t: Throwable) -> Unit): Disposable = this.autoDisposable(provider).subscribe({ next.invoke() }, error)
fun <T> Maybe<T>.subscribeWithCrash(provider: ScopeProvider, next: KFunction0<Unit>) = this.autoDisposable(provider).subscribeWithCrash(next)
fun <T> Maybe<T>.subscribe(provider: ScopeProvider, next: KFunction0<Unit>, error: (t: Throwable) -> Unit): Disposable = this.autoDisposable(provider).subscribe({ next.invoke() }, error)
fun <T> Maybe<T>.subscribeWithProvider(provider: ScopeProvider, next: KFunction0<Unit>, error: (t: Throwable) -> Unit): Disposable = this.autoDisposable(provider).subscribe({ next.invoke() }, error)

fun View?.throttleClicks(duration: Long = 200): Observable<Unit> = Observable.just(Optional.fromNullable(this)).filterAndGet().flatMap { it.clicks().throttleFirst(duration, TimeUnit.MILLISECONDS) }
