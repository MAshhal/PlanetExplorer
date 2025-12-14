package com.mystic.planetexplorer.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.unveilIn
import androidx.compose.animation.veilOut
import androidx.navigation3.scene.Scene

fun <T: Any> slideTransitionSpec():
        AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform = {

    ContentTransform(
        targetContentEnter = slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Left,
            animationSpec = tween(durationMillis = 500)
        ),
        initialContentExit = slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Left,
            animationSpec = tween(durationMillis = 500)
        )
    )
}

fun <T: Any> slidePopTransitionSpec():
        AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform = {

    ContentTransform(
        targetContentEnter = slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Right,
            animationSpec = tween(durationMillis = 500)
        ),
        initialContentExit = slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Right,
            animationSpec = tween(durationMillis = 500)
        )
    )
}

@OptIn(ExperimentalAnimationApi::class)
fun <T: Any> unveilTransitionSpec():
        AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform = {

    ContentTransform(
        targetContentEnter = unveilIn(tween(600)),
        initialContentExit = veilOut(tween(600))
    )
}

@OptIn(ExperimentalAnimationApi::class)
fun <T: Any> unveilPopTransitionSpec():
        AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform = {

    ContentTransform(
        targetContentEnter = unveilIn(tween(600)),
        initialContentExit = veilOut(tween(600))
    )
}
