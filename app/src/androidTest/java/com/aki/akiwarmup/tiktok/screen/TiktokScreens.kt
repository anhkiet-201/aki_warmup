package com.aki.akiwarmup.tiktok.screen

import com.aki.akiwarmup.core.dsl.SceneExecutionContext
import com.aki.akiwarmup.core.dsl.ScreenBuilder
import com.aki.akiwarmup.core.dsl.defineScreen
import com.aki.akiwarmup.core.dsl.id
import com.aki.akiwarmup.core.dsl.text

/**
 * Trang **HOME**
 */
fun onHome(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Home", context) {
        detect {
            all(
                text(TiktokText.HOME), id(TiktokId.USER_AVATAR)
            )
        }
        apply(block)
    }

/**
 * Trang **SEARCH**
 */
fun onSearch(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Search", context) {
        detect {
            has(id(TiktokId.SEARCH_BAR) and text(TiktokText.SEARCH))
        }
        apply(block)
    }

/**
 * Trang **SEARCH RESULT**
 */
fun onSearchResult(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Search Result", context) {
        detect {
            has(id(TiktokId.SEARCH_PAGER))
        }
        apply(block)
    }

/**
 * Trang **VIDEO VIEW** — đang xem video
 */
fun onVideoView(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Video View", context) {
        detect { has(text(TiktokText.SEARCH) and id(TiktokId.USER_AVATAR)) }
        apply(block)
    }

/**
 * Trang **UNKNOWN** — màn hình không xác định, có dialog cần dismiss
 */
fun onUnknowView(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Unknown", context) {
        detect { any(text("Đã hiểu"), text("Không cho phép")) }
        apply(block)
    }

/**
 * Trang **TIKTOK SHARE POST**
 * Xuất hiện khi chia sẻ video lên TikTok từ ngoài app
 */
fun onTiktokSharePost(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Tiktok Share Post", context) {
        detect { has(text("Chia sẻ lên TikTok")) }
        apply(block)
    }

/**
 * Trang **VIDEO PREVIEW** — xem lại video trước khi đăng
 */
fun onVideoPreview(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Video Preview", context) {
        detect { has(id("com.ss.android.ugc.trill:id/zo6")) }
        apply(block)
    }

/**
 * Sheet **SELECT MUSIC** — chọn nhạc nền
 */
fun onSelectMusicSheet(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Select Music", context) {
        detect { has(id("com.ss.android.ugc.trill:id/t96")) }
        apply(block)
    }

/**
 * Trang **ADD INFO** — thêm caption, hashtag, vị trí trước khi đăng
 */
fun onAddInfoView(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Add Info", context) {
        detect { has(id("com.ss.android.ugc.trill:id/gfw")) }
        apply(block)
    }

/**
 * Trang **PROFILE** — trang cá nhân
 */
fun onProfile(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Profile", context) {
        detect { has(id("com.ss.android.ugc.trill:id/hdm")) }
        apply(block)
    }

/**
 * Sheet **SHARE** — menu chia sẻ video
 */
fun onShare(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Share", context) {
        detect { has(id("com.ss.android.ugc.trill:id/znd")) }
        apply(block)
    }

/**
 * Sheet **REPOST** — popup xác nhận đăng lại
 */
fun onRepostPopup(
    context: SceneExecutionContext, block: ScreenBuilder.() -> Unit
) = defineScreen("Repost Popup", context) {
    detect { has(id("com.ss.android.ugc.trill:id/ofw")) }
    apply(block)
}

/**
 * Sheet **DELETE** — popup xác nhận xóa video
 */
fun onDeletePopup(
    context: SceneExecutionContext, block: ScreenBuilder.() -> Unit
) = defineScreen("Delete Popup", context) {
    detect { has(id("com.ss.android.ugc.trill:id/xd")) }
    apply(block)
}
