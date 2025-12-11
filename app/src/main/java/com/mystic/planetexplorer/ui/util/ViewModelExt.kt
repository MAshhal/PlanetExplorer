package com.mystic.planetexplorer.ui.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

context(vm: ViewModel)
fun <T> Flow<T>.asState(
    initialValue: T,
    scope: CoroutineScope = vm.viewModelScope,
    started: SharingStarted = SharingStarted.WhileSubscribed(5_000)
) = stateIn(scope, started, initialValue)