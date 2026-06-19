package com.aki.akiwarmup.facebook.scene

import com.aki.akiwarmup.core.dsl.SceneBuilder
import com.aki.akiwarmup.core.logger.AkiLog
import com.aki.akiwarmup.core.logger.LogTag
import com.aki.akiwarmup.tiktok.action.*
import com.aki.akiwarmup.tiktok.screen.*

/**
 * Hành vi (Behavior) cơ bản của Facebook: bắt lỗi và tự động xử lý các màn hình không xác định (Unknown Screens).
 * Nếu số lượng màn hình không xác định liên tiếp vượt quá 8, hệ thống sẽ tự động nhấn phím Home và dừng kiểm thử.
 */
val FaceBaseBehaviors: SceneBuilder.() -> Unit = {
    handleUnknowScreen {
        AkiLog.w(LogTag.ENGINE, "unknown screen #${context.consecutiveUnknownScreens}")
        if (this.context.consecutiveUnknownScreens > 8) {
            context.device.pressHome()
            this.context.stop("LỖI APP (Quá nhiều màn hình không xác định)")
        }
    }
}

/**
 * Hành vi (Behavior) xử lý luồng bình luận trên Facebook (bao gồm xem danh sách bình luận và gửi bình luận mới).
 */
val FaceCommentBehaviors: SceneBuilder.() -> Unit = {
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
 * Hành vi (Behavior) xử lý luồng chia sẻ và xóa video trên Facebook.
 * Tự động đăng ký màn hình xem video để thực hiện mở menu, và màn hình chia sẻ để thực hiện xóa video.
 */
val FaceDeleteVideoBehaviors: SceneBuilder.() -> Unit = {
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
