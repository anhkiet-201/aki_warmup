package com.aki.akiwarmup.tiktok.scene

import com.aki.akiwarmup.core.dsl.SceneBuilder
import com.aki.akiwarmup.core.logger.AkiLog
import com.aki.akiwarmup.core.logger.LogTag
import com.aki.akiwarmup.tiktok.action.*
import com.aki.akiwarmup.tiktok.screen.*

/**
 * Behavior bắt lỗi và xử lý các màn hình pop-up không xác định (Unknown Screens).
 */
val TiktokBaseBehaviors: SceneBuilder.() -> Unit = {
    handleUnknowScreen {
        AkiLog.w(LogTag.ENGINE, "unknown screen #${context.consecutiveUnknownScreens}")
        if (this.context.consecutiveUnknownScreens > 8) {
            context.device.pressHome()
            this.context.stop("LỖI APP (Quá nhiều màn hình không xác định)")
        }
    }

    onUnknowView(context) {
        action {
            onUnknowViewAction(context)
        }
    }
}

/**
 * Behavior xử lý việc bình luận (Xem bình luận và Thêm bình luận).
 */
val TiktokCommentBehaviors: SceneBuilder.() -> Unit = {
    onCommentView(context) {
        action {
            viewComment(context)
        }
    }

    onAddComment(context) {
        action {
            addComment(context)
        }
    }
}

/**
 * Behavior xử lý các luồng chia sẻ (Share), popup xóa video.
 * Màn hình Video View sẽ được add tự động kèm action mở Menu.
 */
val TiktokDeleteVideoBehaviors: SceneBuilder.() -> Unit = {
    onVideoView(context) {
        action {
            openVideoMenu(context)
        }
    }

    onShare(context) {
        action {
            swipeToChooseDelete(context)
        }
    }
}
