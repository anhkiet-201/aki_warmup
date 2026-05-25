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
fun onHome(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Home", context) {
        detect {
            all(
                text(TEXT.HOME), id(ID.USER_AVATAR)
            )
        }
        apply(block)
    }

/**
 * Trang **SEARCH**
 * */
fun onSearch(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Search", context) {
        detect {
            has(id(ID.SEARCH_BAR) and text(TEXT.SEARCH))
        }
        apply(block)
    }

/**
 * Trang **SEARCH RESULT**
 * */
fun onSearchResult(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Search", context) {
        detect {
            has(id(ID.SEARCH_PAGER))
        }
        apply(block)
    }

/**
 * Trang **SEARCH RESULT**
 * */
fun onVideoView(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Video View", context) {
        detect { has(text(TEXT.SEARCH) and id(ID.USER_AVATAR)) }
        apply(block)
    }

/**
 * Trang **UNKOWN**
 * */
fun onUnknowView(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Unknow", context) {
        detect { any(text("Đã hiểu"), text("Không cho phép")) }
        apply(block)
    }

/**
 * Trang **TIKTOK SHARE POST**
 * Xuất hiện sau khi chia sẻ video lên tiktok
 * */
fun onTiktokSharePost(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Tiktok share post", context) {
        detect { has(text("Chia sẻ lên TikTok")) }
        apply(block)
    }

/**
 * Trang **VIDEO PREVIEW**
 * Review video trước khi post
 * */
fun onVideoPreview(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Video Preview", context) {
        detect { has(id("com.ss.android.ugc.trill:id/zo6")) }
        apply(block)
    }

/**
 * Trang **SELECT MUSIC**
 * Chọn nhạc
 * */
fun onSelectMusicSheet(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Select Music", context) {
        detect { has(id("com.ss.android.ugc.trill:id/t96")) }
        apply(block)
    }

/**
 * Trang **ADD INFO**
 * Thêm thông tin như: Caption, hastag, vị trí
 * */
fun onAddInfoView(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Add Info", context) {
        detect { has(id("com.ss.android.ugc.trill:id/gfw")) }
        apply(block)
    }

/**
 * Trang **Profile**
 * */
fun onProfile(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Profile", context) {
        detect { has(id("com.ss.android.ugc.trill:id/hdm")) }
        apply(block)
    }

/**
 * Trang **Share**
 * */
fun onShare(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Share", context) {
        detect { has(id("com.ss.android.ugc.trill:id/znd")) }
        apply(block)
    }

/**
 * Sheet **Repost**
 * */
fun onRepostPopup(
    context: SceneExecutionContext, block: ScreenBuilder.() -> Unit
) = defineScreen("Repost Popup", context) {
    detect { has(id("com.ss.android.ugc.trill:id/ofw")) }
    apply(block)
}

/**
 * Sheet **Delete**
 * */
fun onDeletePopup(
    context: SceneExecutionContext, block: ScreenBuilder.() -> Unit
) = defineScreen("Delete Popup", context) {
    detect { has(id("com.ss.android.ugc.trill:id/xd")) }
    apply(block)
}

