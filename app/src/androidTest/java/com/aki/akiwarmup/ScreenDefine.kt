package com.aki.akiwarmup

import com.aki.akiwarmup.core.dsl.ActionDef
import com.aki.akiwarmup.core.dsl.SceneExecutionContext
import com.aki.akiwarmup.core.dsl.ScreenBuilder
import com.aki.akiwarmup.core.dsl.defineScreen
import com.aki.akiwarmup.core.dsl.id
import com.aki.akiwarmup.core.dsl.text

/**
 * Trang **HOME**
 * */
fun onHome(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) = defineScreen("Home", context) {
    detect {
        all(
            text(TEXT.HOME),
            id(ID.USER_AVATAR)
        )
    }
    apply(block)
}

/**
 * Trang **SEARCH**
 * */
fun onSearch(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) = defineScreen("Search", context) {
    detect {
        has( id(ID.SEARCH_BAR) and text(TEXT.SEARCH))
    }
    apply(block)
}

/**
 * Trang **SEARCH RESULT**
 * */
fun onSearchResult(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) = defineScreen("Search", context) {
    detect {
        has( id(ID.SEARCH_PAGER))
    }
    apply(block)
}

/**
 * Trang **SEARCH RESULT**
 * */
fun onVideoView(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) = defineScreen("Video View", context) {
    detect { has(text(TEXT.SEARCH) and id(ID.USER_AVATAR)) }
    apply(block)
}

