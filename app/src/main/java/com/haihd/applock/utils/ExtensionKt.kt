package com.haihd.applock.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

fun <R> CoroutineScope.executeAsyncTask(
    onPreExecute: () -> Unit,
    doInBackground: () -> R,
    onPostExecute: (R) -> Unit
): Job {
    return launch(context = EmptyCoroutineContext) {
        onPreExecute.invoke()
        val result = doInBackground.invoke()
        onPostExecute.invoke(result)
    }
}
