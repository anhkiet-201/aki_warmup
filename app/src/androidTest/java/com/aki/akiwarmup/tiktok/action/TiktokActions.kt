package com.aki.akiwarmup.tiktok.action

import android.util.Log
import com.aki.akiwarmup.core.logger.AkiLog
import com.aki.akiwarmup.core.logger.LogTag
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiObject2
import com.aki.akiwarmup.core.dsl.ActionBuilder
import com.aki.akiwarmup.core.dsl.SceneExecutionContext
import com.aki.akiwarmup.core.dsl.SimpleSelector
import com.aki.akiwarmup.core.dsl.activeScreenBuilder
import com.aki.akiwarmup.core.dsl.and
import com.aki.akiwarmup.core.dsl.clazz
import com.aki.akiwarmup.core.dsl.defineAction
import com.aki.akiwarmup.core.dsl.desc
import com.aki.akiwarmup.core.dsl.id
import com.aki.akiwarmup.core.dsl.or
import com.aki.akiwarmup.core.dsl.text
import com.aki.akiwarmup.random.generateComment
import com.aki.akiwarmup.tiktok.model.AutoRate
import com.aki.akiwarmup.tiktok.model.RateType
import com.aki.akiwarmup.tiktok.screen.TiktokDesc
import com.aki.akiwarmup.tiktok.screen.TiktokId
import com.aki.akiwarmup.tiktok.screen.TiktokText
import java.util.Collections
import java.util.WeakHashMap

typealias Action = suspend ActionBuilder.() -> Unit

// ============================================================================
// BỘ TỪ KHÓA TIKTOK WARMUP
// ============================================================================

/**
 * 1. Từ khóa việc làm & tuyển dụng chung (không phụ thuộc địa phương).
 */
val commonEmploymentKeywords = listOf(
    "việc làm",
    "viec lam",
    "vieclam",
    "tìm việc",
    "tim viec",
    "timviec",
    "tuyển dụng",
    "tuyen dung",
    "tuyendung",
    "thời vụ",
    "thoi vu",
    "thoivu",
    "chính thức",
    "chinh thuc",
    "chinhthuc",
    "công nhân",
    "cong nhan",
    "congnhan",
    "lao động",
    "lao dong",
    "laodong",
    "lao dong pho thong",
    "ldpt",
    "công ty",
    "cong ty",
    "congty",
    "công việc",
    "cong viec",
    "congviec",
    "kcn",
    "khu công nghiệp",
    "khu cong nghiep",
    "khucongnghiep"
)

/**
 * 2. Từ khóa chế độ đãi ngộ & thu hút người lao động.
 */
val commonBenefitKeywords = listOf(
    "lương 3 ngày",
    "luong 3 ngay",
    "luong3ngay",
    "lương tuần",
    "luong tuan",
    "luongtuan",
    "nhận lương",
    "nhan luong",
    "nhanluong",
    "bao ăn ở",
    "bao an o",
    "baoano",
    "tăng ca",
    "tang ca",
    "tangca",
    "thu nhập",
    "thu nhap",
    "thunhap"
)

/**
 * 3. Từ khóa ngành nghề sản xuất chủ lực.
 */
val commonIndustryKeywords = listOf(
    "điện tử",
    "dien tu",
    "dientu",
    "gỗ",
    "nội thất",
    "noi that",
    "noithat",
    "may mặc",
    "may mac",
    "maymac",
    "giày da",
    "giay da",
    "giayda",
    "bao bì",
    "bao bi",
    "baobi",
    "cơ khí",
    "co khi",
    "cokhi",
    "nhựa",
    "nhua"
)

/**
 * 4. Địa bàn & Khu công nghiệp Bình Dương (có dấu, không dấu, viết liền).
 */
val binhDuongLocationKeywords = listOf(
    // Tỉnh & Thành phố / Huyện
    "bình dương",
    "binh duong",
    "binhduong",
    "vieclambinhduong",
    "thủ dầu một",
    "thu dau mot",
    "thudaumot",
    "tdm",
    "thuận an",
    "thuan an",
    "thuanan",
    "dĩ an",
    "di an",
    "dian",
    "tân uyên",
    "tan uyen",
    "tanuyen",
    "bến cát",
    "ben cat",
    "bencat",
    "bàu bàng",
    "bau bang",
    "baubang",
    "bắc tân uyên",
    "bac tan uyen",
    "bactanuyen",
    "phú giáo",
    "phu giao",
    "phugiao",
    "dầu tiếng",
    "dau tieng",
    "dautieng",

    // KCN VSIP (1, 2, 2A, 3)
    "vsip",
    "kcn vsip",
    "vsip 1",
    "vsip 2",
    "vsip 2a",
    "vsip 3",
    "vsip1",
    "vsip2",
    "vsip3",
    "việt nam singapore",
    "viet nam singapore",

    // KCN Sóng Thần (1, 2, 3)
    "sóng thần",
    "song than",
    "songthan",
    "sóng thần 1",
    "song than 1",
    "songthan1",
    "sóng thần 2",
    "song than 2",
    "songthan2",

    // KCN Mỹ Phước (1, 2, 3, 4)
    "mỹ phước",
    "my phuoc",
    "myphuoc",
    "mỹ phước 1",
    "my phuoc 1",
    "myphuoc1",
    "mỹ phước 2",
    "my phuoc 2",
    "myphuoc2",
    "mỹ phước 3",
    "my phuoc 3",
    "myphuoc3",

    // KCN Nam Tân Uyên & Đất Cuốc
    "nam tân uyên",
    "nam tan uyen",
    "namtanuyen",
    "đất cuốc",
    "dat cuoc",
    "datcuoc",

    // KCN Tân Đông Hiệp, Đồng An, Rạch Bắp, Việt Hương, Kim Huy, Đại Đăng, Mapletree...
    "tân đông hiệp",
    "tan dong hiep",
    "tandonghiep",
    "đồng an",
    "dong an",
    "dongan",
    "rạch bắp",
    "rach bap",
    "rachbap",
    "việt hương",
    "viet huong",
    "viethuong",
    "kim huy",
    "kimhuy",
    "đại đăng",
    "dai dang",
    "daidang",
    "mapletree",
    "tân bình bình dương",
    "kcn tân bình",
    "bình chuẩn",
    "binh chuan",
    "binhchuan",
    "an tây",
    "an tay",
    "antay"
)

/**
 * 5. Địa bàn & Khu công nghiệp Đồng Nai (Bảo lưu dự phòng).
 */
val dongNaiLocationKeywords = listOf(
    "đồng nai",
    "dong nai",
    "dongnai",
    "vieclamdongnai",
    "biên hòa",
    "bien hoa",
    "bienhoa",
    "trảng bom",
    "trang bom",
    "trangbom",
    "long thành",
    "long thanh",
    "longthanh",
    "nhơn trạch",
    "nhon trach",
    "nhontrach",
    "vĩnh cửu",
    "vinh cuu",
    "vinhcuu",
    "sông mây",
    "song may",
    "songmay",
    "hố nai",
    "ho nai",
    "honai",
    "hố nai 3",
    "ho nai 3",
    "honai3",
    "tam phước",
    "tam phuoc",
    "tamphuoc",
    "amata",
    "lộc an",
    "loc an",
    "locan",
    "bình sơn",
    "binh son",
    "binhson",
    "loc an binh son",
    "locanbinhson",
    "giang điền",
    "giang dien",
    "giangdien"
)

/**
 * Bộ câu tìm kiếm tự nhiên TikTok cho khu vực Bình Dương.
 */
val binhDuongSearchKeywords = listOf(
    // 1. Nhóm lương 3 ngày 1 lần / lương tuần / thời vụ tại Bình Dương
    "những công ty lương 3 ngày 1 lần tại bình dương",
    "công ty lương 3 ngày bình dương",
    "việc làm lương 3 ngày 1 lần bình dương",
    "việc làm lương 3 ngày bình dương",
    "tuyển dụng công ty lương 3 ngày bình dương",
    "công ty nhận lương 3 ngày tại bình dương",
    "việc làm thời vụ lương 3 ngày bình dương",
    "việc làm lương tuần bến cát bình dương",
    "việc làm lương 3 ngày thuận an",

    // 2. KCN VSIP 1, 2, 3 (Thuận An, Tân Uyên, Bến Cát)
    "việc làm kcn vsip 1 thuận an",
    "tuyển dụng kcn vsip 1 bình dương",
    "công ty điện tử kcn vsip 1",
    "việc làm kcn vsip 2 tân uyên",
    "công ty kcn vsip 2 mở rộng",
    "tuyển dụng kcn vsip bình dương",
    "việc làm thời vụ kcn vsip",
    "công ty điện tử kcn vsip 2",

    // 3. KCN Sóng Thần & Dĩ An
    "việc làm kcn sóng thần dĩ an",
    "tuyển dụng kcn sóng thần 1 2",
    "công ty giày da kcn sóng thần",
    "việc làm thời vụ kcn sóng thần",
    "công ty kcn sóng thần bình dương",
    "tuyển dụng việc làm dĩ an bình dương",

    // 4. KCN Mỹ Phước 1, 2, 3 & Bến Cát
    "việc làm kcn mỹ phước 1 2 3",
    "tuyển dụng kcn mỹ phước bến cát",
    "công ty điện tử mỹ phước 3",
    "công ty gỗ mỹ phước bến cát",
    "việc làm thời vụ bến cát bình dương",
    "tuyển dụng lao động phổ thông bến cát",

    // 5. KCN Bàu Bàng
    "việc làm kcn bàu bàng bình dương",
    "tuyển dụng kcn bàu bàng",
    "công ty may mặc kcn bàu bàng",
    "việc làm thời vụ bàu bàng bình dương",
    "công nhân kcn bàu bàng",

    // 6. KCN Nam Tân Uyên & Đất Cuốc
    "việc làm kcn nam tân uyên",
    "tuyển dụng kcn nam tân uyên bình dương",
    "công ty gỗ kcn nam tân uyên",
    "việc làm kcn đất cuốc bắc tân uyên",
    "tuyển dụng tân uyên bình dương",

    // 7. KCN Đồng An, Việt Hương, Đại Đăng
    "việc làm kcn đồng an thuận an",
    "tuyển dụng kcn việt hương bình dương",
    "công ty kcn đại đăng thủ dầu một",

    // 8. Từ khóa tổng hợp Bình Dương
    "việc làm bình dương",
    "việc làm thời vụ bình dương",
    "tìm việc làm ở bình dương",
    "tuyển dụng công nhân bình dương",
    "làm công ty ở bình dương",
    "việc làm thủ dầu một bình dương",
    "việc làm thuận an bình dương",
    "việc làm dĩ an",
    "việc làm tân uyên"
)

/**
 * Bộ câu tìm kiếm tự nhiên TikTok cho khu vực Đồng Nai (Bảo lưu dự phòng).
 */
val dongNaiSearchKeywords = listOf(
    // 7 từ khóa gốc
    "những công ty lương 3 ngày 1 lần tại đồng nai",
    "công ty điện tử kcn sông mây",
    "công ty gỗ nội thất kcn hố nai 3",
    "công ty điện tử kcn long thành",
    "công ty gỗ kcn tam phước",
    "công ty điện tử kcn amata long thành",
    "công ty điện tử kcn lộc an bình sơn",
    // Nhóm lương 3 ngày 1 lần / lương tuần tại Đồng Nai
    "công ty lương 3 ngày đồng nai",
    "việc làm lương 3 ngày 1 lần",
    "việc làm lương 3 ngày đồng nai",
    "tuyển dụng công ty lương 3 ngày",
    "công ty nhận lương 3 ngày tại đồng nai",
    "việc làm thời vụ lương 3 ngày đồng nai",
    // KCN Sông Mây
    "việc làm kcn sông mây",
    "tuyển dụng kcn sông mây",
    "công nhân điện tử kcn sông mây",
    "việc làm thời vụ kcn sông mây",
    // KCN Hố Nai 3 & Trảng Bom
    "việc làm kcn hố nai 3",
    "công ty gỗ kcn hố nai",
    "tuyển dụng kcn hố nai trảng bom",
    "việc làm gỗ nội thất hố nai",
    // KCN Long Thành & Lộc An Bình Sơn
    "việc làm kcn long thành",
    "tuyển dụng kcn long thành",
    "công ty điện tử long thành",
    "việc làm kcn lộc an bình sơn",
    "công ty kcn lộc an bình sơn",
    "tuyển dụng lộc an bình sơn đồng nai",
    // KCN Tam Phước & KCN Amata
    "việc làm kcn tam phước biên hòa",
    "tuyển dụng kcn tam phước",
    "công ty gỗ tam phước đồng nai",
    "việc làm kcn amata",
    "công ty điện tử kcn amata",
    "tuyển dụng kcn amata biên hòa",
    // Từ khóa tổng hợp Đồng Nai
    "việc làm đồng nai",
    "việc làm thời vụ đồng nai",
    "tìm việc làm ở đồng nai",
    "tuyển dụng công nhân đồng nai",
    "làm công ty ở đồng nai"
)

// ============================================================================
// PUBLIC KEYWORD INTERFACES (Cấu hình kích hoạt hiện tại: BÌNH DƯƠNG)
// ============================================================================

/**
 * Danh sách từ khóa quét caption mô tả video để nhận diện video tuyển dụng mục tiêu.
 * Kết hợp từ khóa chung (việc làm, chế độ, ngành nghề) và địa bàn Bình Dương.
 */
val captionKeyword: List<String> = commonEmploymentKeywords +
    commonBenefitKeywords +
    commonIndustryKeywords +
    binhDuongLocationKeywords

/**
 * Danh sách cụm từ tìm kiếm tự nhiên mồi thuật toán TikTok.
 * Mặc định sử dụng bộ tìm kiếm khu vực Bình Dương.
 */
val keyWorlds: List<String> = binhDuongSearchKeywords

private val targetHashtags = listOf("#ttnhr", "#vieclamttn")

/**
 * Trạng thái phiên xem video xuyên suốt nhiều lần lặp hoặc re-detect của ActionLoop.
 * Giữ số lượng video đã xem, số lần fingerprint trùng liên tiếp, cờ quay lại từ Comment và danh sách video đã bình luận.
 */
class WatchVideoSession {
    var videoCount: Int = 0
    var sameFingerprintCount: Int = 0
    var lastFingerprint: String? = null
    var current: VideoState? = null
    var pendingCommentReturn: Boolean = false
    var commentSheetHandled: Boolean = false
    val commentedFingerprints: MutableSet<String> = mutableSetOf()
}

private val watchVideoSessions = Collections.synchronizedMap(
    WeakHashMap<SceneExecutionContext, MutableMap<String, WatchVideoSession>>()
)

@Volatile
private var activeWatchSession: WatchVideoSession? = null

/**
 * Lấy hoặc khởi tạo [WatchVideoSession] gắn với [SceneExecutionContext] và khóa màn hình hiện tại,
 * đảm bảo không bị tạo lại session mới khi `ScreenBuilder` đánh giá lại `testBlock` sau khi đóng bảng Comment.
 */
fun SceneExecutionContext.getWatchVideoSession(key: String): WatchVideoSession {
    val map = watchVideoSessions.getOrPut(this) { mutableMapOf() }
    return map.getOrPut(key) { WatchVideoSession() }
}

/**
 * Trạng thái tương tác cụ thể của từng video đơn lẻ đang phát trên màn hình.
 * Tránh lặp lại follow, dwell hoặc tương tác trùng lặp (ví dụ tap Re-post 2 lần gây hủy repost).
 */
data class VideoState(
    val fingerprint: String?,
    val isTarget: Boolean,
    val isKeywordHit: Boolean,
    val performed: MutableSet<RateType> = mutableSetOf(),
    var hasFollowed: Boolean = false,
    var hasDwelled: Boolean = false
)

/**
 * Kiểm tra xem màn hình hiện tại có còn đang ở giao diện xem video hay không.
 * Hỗ trợ kiểm tra cả `LIKE_BUTTON` (`#fhc`) và `LIKE_BUTTON_CONTAINER` (`#fhu`) khi cây Accessibility bị nén lúc video đang phát.
 */
private fun ActionBuilder.isOnVideoFeed(): Boolean {
    return (has(id(TiktokId.LIKE_BUTTON)) || has(id(TiktokId.LIKE_BUTTON_CONTAINER))) &&
        has(id(TiktokId.USER_AVATAR))
}

/**
 * Đọc định danh duy nhất của video hiện tại (fingerprint).
 * Sử dụng existingDesc đã tìm thấy từ đầu vòng lặp để tránh scan lại view hierarchy.
 * Nếu video không có mô tả (caption trống) trong chế độ seeding, kết hợp ngày đăng và thông tin nút bình luận làm định danh dự phòng.
 */
private fun ActionBuilder.readVideoFingerprint(existingDesc: String?, isSeeding: Boolean = false): String? {
    val trimmedExisting = existingDesc?.trim()
    if (!trimmedExisting.isNullOrEmpty()) return trimmedExisting
    if (isSeeding) {
        val authorOrDate = find(id("com.ss.android.ugc.trill:id/z_x"))?.text?.trim()
            ?: find(id("com.ss.android.ugc.trill:id/title"))?.text?.trim()
        if (!authorOrDate.isNullOrEmpty()) {
            return "no_desc_$authorOrDate"
        }
    }

    // Fallback 1: Thử lấy contentDescription của nút Like hoặc Like container
    val likeDesc = find(id(TiktokId.LIKE_BUTTON))?.contentDescription?.toString()?.trim()
        ?: find(id(TiktokId.LIKE_BUTTON_CONTAINER))?.contentDescription?.toString()?.trim()
    if (!likeDesc.isNullOrEmpty()) {
        return "like_$likeDesc"
    }

    // Fallback 2: Thử lấy contentDescription của nút Comment hoặc Share
    val commentOrShareDesc = find(id(TiktokId.COMMENT_BUTTON_CONTAINER))?.contentDescription?.toString()?.trim()
        ?: find(id(TiktokId.COMMENT_BUTTON))?.contentDescription?.toString()?.trim()
        ?: find(id(TiktokId.SHARE_BUTTON))?.contentDescription?.toString()?.trim()
    if (!commentOrShareDesc.isNullOrEmpty()) {
        return "btn_$commentOrShareDesc"
    }

    // Fallback 3: Ghép text của các TextView hiển thị trên màn hình
    val anyText = findAll(clazz("android.widget.TextView"))
        .mapNotNull { it.text?.trim() }
        .filter { it.isNotEmpty() }
        .take(5)
        .joinToString("|")
    if (anyText.isNotEmpty()) {
        return "text_$anyText"
    }

    return null
}

/**
 * Kiểm tra xem video có thuộc diện mục tiêu seeding hay tuyển dụng cần tương tác không.
 */
private fun isTargetVideo(descText: String?, isSeeding: Boolean): Boolean {
    if (isSeeding) return true
    if (descText.isNullOrEmpty()) return false
    return targetHashtags.any { descText.contains(it, ignoreCase = true) }
}

/**
 * Kiểm tra mô tả video có chứa từ khóa quan tâm trong [captionKeyword] hay không.
 */
private fun isKeywordHit(descText: String?): Boolean {
    if (descText.isNullOrEmpty()) return false
    val lower = descText.lowercase()
    return captionKeyword.any { kw -> lower.contains(kw) }
}

/**
 * Chờ xem video (dwell time) theo hành vi người dùng tự nhiên.
 * - Video thuộc diện mục tiêu (seeding, hashtag) hoặc đúng từ khóa chủ đề: xem ngẫu nhiên theo [durationRangeSeconds].
 * - Video không liên quan: lướt qua nhanh (fast skip) từ 1.0s đến 2.5s.
 */
private suspend fun ActionBuilder.dwellVideo(
    isEngaged: Boolean,
    durationRangeSeconds: IntRange
) {
    if (isEngaged) {
        val minMs = maxOf(3000, durationRangeSeconds.first * 1000)
        val maxMs = maxOf(minMs + 2000, durationRangeSeconds.last * 1000)
        val waitMs = random(minMs, maxMs)
        AkiLog.d(LogTag.CONTENT, "Engaged video (target/topic matched) -> dwelling ${waitMs}ms (${waitMs / 1000}s)")
        wait(waitMs)
    } else {
        val waitMs = random(1000, 2500)
        AkiLog.d(LogTag.CONTENT, "Irrelevant video -> fast skip ${waitMs}ms")
        wait(waitMs)
    }
}

/**
 * Thực hiện bấm nút Follow nếu video thuộc mục tiêu và chưa từng follow trước đó.
 * Ngăn chặn việc bấm lặp lại gây toggle hủy Follow.
 */
private fun ActionBuilder.followOnce(state: VideoState) {
    if (!state.hasFollowed) {
        find(id(TiktokId.FOLLOW_BUTTON))?.let { followButton ->
            AkiLog.d(LogTag.ACTION, "Tapping follow button for target video")
            tap(followButton)
            state.hasFollowed = true
        }
    }
}

/**
 * Mở sheet chia sẻ và nhấn chọn chức năng mong muốn (ví dụ Đăng lại hoặc Sao chép link).
 * Dùng waitUntil để chờ popup hiện ra an toàn thay vì delay cố định.
 */
private suspend fun ActionBuilder.shareAndPick(targetText: String): Boolean {
    val shareButton = find(id(TiktokId.SHARE_BUTTON)) ?: return false
    tap(shareButton)
    val item = waitUntil(text(targetText), maxMs = 3000L)
    return if (item != null) {
        tap(item)
        wait(random(1000, 1500))
        true
    } else {
        pressBack()
        wait(random(500, 1000))
        false
    }
}

/**
 * Cuộn lên xem video kế tiếp trong feed.
 * Không thực hiện scan UI ở đây để tránh đọc lặp lại mô tả video (được xử lý ở đầu vòng lặp kế tiếp).
 */
private suspend fun ActionBuilder.swipeToNext(session: WatchVideoSession) {
    swipeUp()
    wait(random(1000, 1500))
    session.current = null
}

/**
 * Lướt xem video TikTok và tự động thực hiện các hành động tương tác (like, comment, favorite, repost, copy link) 
 * dựa trên tỉ lệ ngẫu nhiên được cấu hình bởi [AutoRate].
 *
 * Quy trình xử lý trên mỗi video:
 * 1. Guard feed & bỏ qua quảng cáo: Kiểm tra feed video và lướt qua video quảng cáo đề xuất ngay lập tức.
 * 2. Dwell time: Xem video theo thời gian quy định (lâu nếu trúng từ khóa, nhanh nếu không trúng).
 * 3. Follow: Tự động Follow 1 lần duy nhất nếu video thuộc diện mục tiêu seeding/tuyển dụng.
 * 4. Đa tương tác độc lập: Một video có thể có nhiều tương tác (Like, Favorite, Repost, CopyLink, Comment)
 *    hoặc không có tương tác nào, dựa theo tỉ lệ phần trăm xác suất độc lập cấu hình trong [rate].
 * 5. Chuyển tiếp video:
 *    - Nếu đạt giới hạn [maxVideos] (khi `maxVideos > 0`) hoặc trúng xác suất [RateType.EXIT]: kết thúc phiên xem.
 *    - Nếu không EXIT: Luôn luôn thực hiện vuốt lên (`swipeUp`) sang video tiếp theo.
 *    - Nếu phát hiện video trùng lặp sau khi đã swipe (đã chạm đáy danh sách): dừng ngay lập tức và gọi [onEndOfVideos] (fallback [onExit]).
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`), chứa thông tin phiên chạy.
 * @param rate Đối tượng cấu hình xác suất/tần suất các hành động tự động (`AutoRate`), mặc định là một instance mới.
 * @param id Định danh hành động, mặc định là "Watch Video".
 * @param isSeeding Nếu true, coi mọi video đều là video mục tiêu cần quan tâm follow/comment.
 * @param maxVideos Số lượng video tối đa cần xem trong phiên (<= 0 là không giới hạn).
 * @param watchDurationSeconds Khoảng thời gian (giây) dừng lại xem đối với video mục tiêu/đúng chủ đề.
 * @param onEndOfVideos Callback hành động được gọi khi đã lướt hết video. Nếu null, fallback gọi [onExit].
 * @param session Trạng thái phiên xem video xuyên suốt các vòng lặp hoặc re-detect của ActionLoop.
 * @param onExit Callback hành động dạng suspend được gọi khi quyết định thoát xem video.
 */
fun watchVideo(
    context: SceneExecutionContext,
    rate: AutoRate = AutoRate(),
    id: String = "Watch Video",
    isSeeding: Boolean = false,
    maxVideos: Int = 0,
    watchDurationSeconds: IntRange = context.sceneConfig.watchVideoSeconds,
    onEndOfVideos: Action? = null,
    session: WatchVideoSession = context.getWatchVideoSession("${activeScreenBuilder.get()?.screenID ?: id}_$isSeeding"),
    onExit: Action
) = defineAction(id, context) {
    activeWatchSession = session
    loop {
        // 1. Kiểm tra màn hình còn ở feed video không
        if (!isOnVideoFeed()) {
            AkiLog.w(LogTag.ACTION, "watchVideo: Not on video feed, ending action for re-detection")
            if (isSeeding && session.videoCount > 0 && !session.pendingCommentReturn) {
                AkiLog.i(LogTag.ACTION, "watchVideo (seeding): feed ended or exited after ${session.videoCount} videos -> finishing")
                if (onEndOfVideos != null) onEndOfVideos() else onExit()
            }
            endAction()
        }

        // 2. Kiểm tra quảng cáo: nếu là video quảng bá đề xuất, lướt qua ngay
        on(text(TiktokText.RECOMMENDED_PROMOTION)) { promo ->
            if (promo != null) {
                AkiLog.d(LogTag.CONTENT, "Ad detected (${TiktokText.RECOMMENDED_PROMOTION}) -> swipeUp")
                swipeUp()
                wait(random(500, 1000))
                session.current = null
                session.pendingCommentReturn = false
                endAction()
            }
        }

        // 3. Khởi tạo trạng thái cho video hiện tại & kiểm tra trùng lặp (hết danh sách video)
        val desc = find(id(TiktokId.VIDEO_DESC))?.text?.trim()
        val fp = readVideoFingerprint(desc, isSeeding)

        // Nếu vừa quay lại Video View sau khi mở CommentSheet (pendingCommentReturn) hoặc session.current có cùng fingerprint:
        // kiểm tra giới hạn maxVideos / EXIT rồi vuốt ngay sang video kế tiếp, tuyệt đối không tương tác lại trên cùng video
        val existingState = session.current
        if (session.pendingCommentReturn || (existingState != null && fp != null && existingState.fingerprint == fp)) {
            session.pendingCommentReturn = false
            AkiLog.d(LogTag.CONTENT, "Resumed on same video after comment interaction -> checking exit/maxVideos and swiping next")
            if (maxVideos > 0 && session.videoCount >= maxVideos) {
                AkiLog.i(LogTag.ACTION, "Reached maxVideos ($maxVideos) after comment -> ending watchVideo")
                this.onExit()
                rate.reset()
                endAction()
            }
            sometimes(rate[RateType.EXIT].coerceIn(0, 100)) {
                AkiLog.d(LogTag.ACTION, "Rate EXIT triggered -> ending watchVideo")
                this.onExit()
                rate.reset()
                endAction()
            }
            swipeToNext(session)
            return@loop
        }

        // Đối với video không có desc mà không phải isSeeding: đồng nghĩa với không đúng chủ đề
        if (desc.isNullOrEmpty() && !isSeeding) {
            session.videoCount++
            AkiLog.d(LogTag.CONTENT, "Video #${session.videoCount} has no desc (not seeding) -> Off-topic, fast skip")
            val skipMs = random(1000, 2000)
            wait(skipMs)
            swipeToNext(session)
            return@loop
        }

        // Nếu trùng lặp với video trước đó (khi session.current == null tức là đã thực hiện swipeUp) -> Chắc chắn đã chạm đáy danh sách video
        if (fp != null && session.lastFingerprint != null && fp == session.lastFingerprint) {
            AkiLog.i(LogTag.CONTENT, "Duplicate video confirmed ($fp) -> Ending watchVideo immediately")
            if (onEndOfVideos != null) onEndOfVideos() else onExit()
            endAction()
        }
        session.lastFingerprint = fp

        val isTarget = isTargetVideo(desc, isSeeding)
        val isHit = isKeywordHit(desc)
        session.videoCount++
        val state = VideoState(
            fingerprint = fp,
            isTarget = isTarget,
            isKeywordHit = isHit
        )
        session.current = state
        AkiLog.d(LogTag.CONTENT, "Video #${session.videoCount} desc: ${desc?.take(80) ?: "none"} (target=$isTarget, hit=$isHit)")

        // 4. Follow 1 lần duy nhất nếu là video mục tiêu
        if (state.isTarget) {
            followOnce(state)
        }

        // 5. Chờ xem video (Dwell time)
        val isEngaged = state.isTarget || state.isKeywordHit
        dwellVideo(isEngaged, watchDurationSeconds)
        state.hasDwelled = true

        // 6. Thực hiện tương tác (Một video có thể có nhiều hành động tương tác hoặc không)
        if (state.isKeywordHit || state.isTarget) {
            // Like (hỗ trợ cả ImageView #fhc lẫn Button container #fhu khi #fhc bị ẩn trong lúc phát video)
            sometimes(rate[RateType.LIKE].coerceIn(0, 100)) {
                val likeImg = find(id(TiktokId.LIKE_BUTTON))
                if (likeImg != null) {
                    if (!likeImg.isSelected) {
                        AkiLog.d(LogTag.ACTION, "Tapping LIKE (#fhc)")
                        tap(likeImg)
                        state.performed.add(RateType.LIKE)
                        wait(random(1000, 2500))
                    }
                } else {
                    val likeContainer = findAll(id(TiktokId.LIKE_BUTTON_CONTAINER)).firstOrNull {
                        it.contentDescription?.toString()?.startsWith(TiktokDesc.LIKE_UNSELECTED_PREFIX) == true
                    }
                    if (likeContainer != null) {
                        AkiLog.d(LogTag.ACTION, "Tapping LIKE container (#fhu)")
                        tap(likeContainer)
                        state.performed.add(RateType.LIKE)
                        wait(random(1000, 2500))
                    }
                }
            }

            // Favorite (Lấy phần tử cuối cùng để tránh trùng ID h_9 trên thanh top bar của Home)
            sometimes(rate[RateType.FAVORITE].coerceIn(0, 100)) {
                findAll(id(TiktokId.FAVORITE_BUTTON)).lastOrNull()?.let {
                    if (!it.isSelected) {
                        AkiLog.d(LogTag.ACTION, "Tapping FAVORITE")
                        tap(it)
                        state.performed.add(RateType.FAVORITE)
                        wait(random(1000, 3000))
                    }
                }
            }

            // Repost (Bỏ qua nếu trên màn hình đã hiện nhãn "Bạn đã đăng lại" để tránh mở Share Sheet vô ích)
            if (!has(id(TiktokId.REPOSTED_BADGE))) {
                sometimes(rate[RateType.RE_POST].coerceIn(0, 100)) {
                    AkiLog.d(LogTag.ACTION, "Sharing RE_POST")
                    if (shareAndPick(TiktokText.REPOST)) {
                        state.performed.add(RateType.RE_POST)
                    }
                }
            }

            // Copy Link
            sometimes(rate[RateType.COPY_LINK].coerceIn(0, 100)) {
                AkiLog.d(LogTag.ACTION, "Sharing COPY_LINK")
                if (shareAndPick(TiktokText.COPY_LINK)) {
                    state.performed.add(RateType.COPY_LINK)
                }
            }

            // Comment (nếu là video mục tiêu và chưa từng comment trên video này trong phiên)
            val canComment = state.isTarget &&
                !state.performed.contains(RateType.COMMENT) &&
                (fp == null || !session.commentedFingerprints.contains(fp))
            if (canComment) {
                sometimes(rate[RateType.COMMENT].coerceIn(0, 100)) {
                    val commentButton = find(id(TiktokId.COMMENT_BUTTON_CONTAINER))
                        ?: find(id(TiktokId.COMMENT_BUTTON))
                    commentButton?.let {
                        AkiLog.d(LogTag.ACTION, "Opening COMMENT")
                        session.commentSheetHandled = false
                        session.pendingCommentReturn = true
                        if (fp != null) session.commentedFingerprints.add(fp)
                        state.performed.add(RateType.COMMENT)
                        tap(it)
                        wait(random(600, 1500))
                        endAction() // Chuyển quyền xử lý cho CommentView (session.pendingCommentReturn & session.current được giữ)
                    }
                }
            }
        }

        // 7. Kiểm tra nếu đã xem đủ số lượng video giới hạn (maxVideos > 0)
        if (maxVideos > 0 && session.videoCount >= maxVideos) {
            AkiLog.i(LogTag.ACTION, "Reached maxVideos ($maxVideos) -> ending watchVideo")
            this.onExit()
            rate.reset()
            endAction()
        }

        // 8. Quyết định: Thoát hay Vuốt sang video tiếp theo
        sometimes(rate[RateType.EXIT].coerceIn(0, 100)) {
            AkiLog.d(LogTag.ACTION, "Rate EXIT triggered -> ending watchVideo")
            this.onExit()
            rate.reset()
            endAction()
        }

        // Nếu không EXIT -> BẮT BUỘC SWIPE SANG VIDEO TIẾP THEO
        AkiLog.d(LogTag.ACTION, "Swiping to next video")
        swipeToNext(session)
    }
}

/**
 * Xem các bình luận của video hiện tại và thực hiện tương tác ngẫu nhiên.
 *
 * Quy trình thực hiện:
 * 1. Nếu phiên hiện tại đã gửi comment (`commentSheetHandled == true`), đóng ngay Comment Sheet (`#be3` / `pressBack()`) để tránh lặp.
 * 2. Kiểm tra nếu ô nhập liệu (`TiktokId.COMMENT_INPUT`) đã có sẵn text nháp thì bấm nút gửi (`#cj9` / `@2131953937`) và đóng Comment Sheet.
 * 3. Nếu chưa có text nháp, cuộn ngẫu nhiên danh sách bình luận (`TiktokId.COMMENT_LIST`) và bấm vào ô nhập bình luận (hoặc nút trả lời nếu có) để mở popup nhập.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 */
fun viewComment(
    context: SceneExecutionContext
) = defineAction("View Comment", context) {
    suspend fun closeCommentSheet() {
        val closeBtn = find(id(TiktokId.CLOSE_COMMENT_BUTTON) and desc(TiktokDesc.CLOSE_SHEET))
            ?: find(id(TiktokId.CLOSE_COMMENT_BUTTON))
        if (closeBtn != null) {
            tap(closeBtn)
        } else {
            pressBack()
        }
        wait(random(600, 1000))
    }

    if (activeWatchSession?.commentSheetHandled == true) {
        AkiLog.d(LogTag.ACTION, "viewComment: commentSheetHandled=true -> closing comment sheet")
        closeCommentSheet()
        endAction()
    }

    on(id(TiktokId.COMMENT_INPUT)) { textField ->
        val draftText = textField?.text?.removePrefix("Thêm bình luận...")?.trim()
        if (!draftText.isNullOrEmpty()) {
            val sendBtn = find(id(TiktokId.POST_COMMENT_BTN_ID))
                ?: findAll(desc(TiktokDesc.POST_COMMENT_BUTTON)).lastOrNull { it.isClickable }
            if (sendBtn != null) {
                AkiLog.d(LogTag.ACTION, "viewComment: Submitting draft comment")
                tap(sendBtn)
                activeWatchSession?.commentSheetHandled = true
                wait(random(1500, 2500))
            }
            closeCommentSheet()
            endAction()
        }

        val comments = find(id(TiktokId.COMMENT_LIST))
        val replyButtons = comments?.findObjects(id(TiktokId.REPPLY_COMMENT_BUTTON).toBySelector())
        if (!replyButtons.isNullOrEmpty()) {
            sometimes(50) {
                comments.swipe(Direction.UP, 0.6f)
                wait(random(400, 1000))
            }
            choose(
                5 to {
                    tap(textField)
                },
                5 to {
                    tap(replyButtons.randomOrNull() ?: textField)
                }
            )
        } else {
            tap(textField)
        }
        wait(random(800, 1400))
    }
    endAction()
}

/**
 * Thêm một bình luận mới vào video, bấm gửi ngay lập tức và đóng Comment Sheet.
 *
 * Quy trình thực hiện:
 * 1. Tìm ô nhập liệu (`TiktokId.COMMENT_INPUT`) đang có thể click và gõ nội dung bình luận.
 * 2. Bấm trực tiếp nút Gửi bình luận (`TiktokId.POST_COMMENT_BTN_ID` / `TiktokDesc.POST_COMMENT_BUTTON`).
 * 3. Đánh dấu `commentSheetHandled = true`, chờ gửi hoàn tất và đóng luôn Comment Sheet để quay về Video View.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 */
fun addComment(
    context: SceneExecutionContext
) = defineAction("Add Comment", context) {
    val inputField = findAll(id(TiktokId.COMMENT_INPUT)).lastOrNull { it.isClickable }
    if (inputField != null) {
        humanType(inputField, generateComment(enableTypos = true))
        wait(random(500, 1200))

        val sendBtn = find(id(TiktokId.POST_COMMENT_BTN_ID))
            ?: findAll(desc(TiktokDesc.POST_COMMENT_BUTTON)).lastOrNull { it.isClickable }
        if (sendBtn != null) {
            AkiLog.d(LogTag.ACTION, "addComment: Tapping Send button (#cj9)")
            tap(sendBtn)
            activeWatchSession?.commentSheetHandled = true
            wait(random(1800, 2800))
        } else {
            pressBack()
            wait(random(600, 1000))
        }
    }

    // Nếu bàn phím / popup nhập liệu (#ck2) vẫn còn hiển thị thì ẩn đi trước
    if (has(id(TiktokId.COMMENT_INPUT_POPUP))) {
        pressBack()
        wait(random(600, 1000))
    }

    // Đóng luôn Comment Sheet (#be3 / desc="Đóng") để quay thẳng về Video View, tránh kẹt lại ở View Comment
    if (has(id(TiktokId.COMMENT_AVATAR))) {
        val closeBtn = find(id(TiktokId.CLOSE_COMMENT_BUTTON) and desc(TiktokDesc.CLOSE_SHEET))
            ?: find(id(TiktokId.CLOSE_COMMENT_BUTTON))
        if (closeBtn != null) {
            tap(closeBtn)
        } else {
            pressBack()
        }
        wait(random(600, 1000))
    }
    endAction()
}

/**
 * Nhập từ khóa tìm kiếm và kích hoạt hành động tìm kiếm trên ứng dụng TikTok.
 *
 * Quy trình thực hiện:
 * 1. Tìm ô nhập liệu tìm kiếm thông qua ID `TiktokId.SEARCH_BAR`.
 * 2. Thực hiện tap vào ô nhập liệu, đợi 500ms, sau đó mô phỏng gõ phím kiểu người dùng ([humanType]) để điền từ khóa [keyword].
 * 3. Tìm nút Tìm kiếm thông qua ID `TiktokId.SEARCH_BUTTON` và tap vào để thực thi truy vấn tìm kiếm.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 * @param keyword Chuỗi từ khóa cần tìm kiếm trên TikTok.
 */
fun typeSearchKeyword(context: SceneExecutionContext, keyword: String) =
    defineAction("Typing keyword", context) {
        find(id(TiktokId.SEARCH_BAR))?.let {
            tap(it)
            wait(500)
            humanType(it, keyword)
            wait(200)
        }
        find(id(TiktokId.SEARCH_BUTTON))?.let {
            tap(it)
            wait(1000)
        }
        endAction()
    }

/**
 * Chuyển sang tab Video trong kết quả tìm kiếm và chọn ngẫu nhiên một video để phát.
 *
 * Quy trình thực hiện:
 * 1. Tìm và tap vào tab "Video" (`TiktokText.VIDEO_TAB`). Nếu tab này không được chọn (isSelected = false), kết thúc hành động.
 * 2. Có xác suất 20% thực hiện cuộn màn hình lên (swipeUp) để xem thêm các video phía dưới.
 * 3. Tìm vùng danh sách kết quả (`TiktokId.SEARCH_RESULT_LIST`), tìm các view con thuộc lớp `android.view.View`.
 * 4. Lấy tối đa 4 video đầu tiên, chọn ngẫu nhiên một video và tap vào để phát, sau đó đợi 3000ms.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 */
fun selectVideoAfterSearch(context: SceneExecutionContext) = defineAction("Select Video", context) {
    find(text(TiktokText.VIDEO_TAB))?.let {
        tap(it)
        if (!it.isSelected) {
            endAction()
        }
        waitUntil(id(TiktokId.SEARCH_RESULT_LIST))
    }
    loop {
        choose(5 to {
            swipeUp()
            wait(random(430, 1230))
        }, 5 to {
            find(id(TiktokId.SEARCH_RESULT_LIST))?.findObjects(clazz("android.view.View").toBySelector())
                .run {
                    this?.let {
                        tap(it.random())
                        waitUntil(id(TiktokId.USER_AVATAR), maxMs = 3000L)
                    }
                }
            endAction()
        })

    }
}

/**
 * Xử lý tự động đóng (dismiss) các hộp thoại hoặc màn hình hướng dẫn/pop-up không xác định xuất hiện đột xuất.
 *
 * Hàm sẽ kiểm tra sự hiện diện của hai nút:
 * - Nút "Đã hiểu" (`TiktokText.UNDERSTOOD`): Nếu tìm thấy, thực hiện tap để đóng.
 * - Nút "Không cho phép" (`TiktokText.NOT_ALLOWED`): Nếu tìm thấy, thực hiện tap để đóng/từ chối.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 */
fun onUnknowViewAction(context: SceneExecutionContext) =
    defineAction("Unknown View Action", context) {
        find(text(TiktokText.UNDERSTOOD))?.let {
            tap(it)
        }
        find(text(TiktokText.NOT_ALLOWED))?.let {
            tap(it)
        }
        endAction()
    }

/**
 * Xử lý hành động chia sẻ bài viết TikTok bằng cách chuyển sang tab Video trong màn hình chia sẻ.
 *
 * Quy trình thực hiện:
 * 1. Tìm nút tab "Video" (`TiktokText.VIDEO_TAB`) trên giao diện chia sẻ.
 * 2. Thực hiện tap vào tab đó, đợi một khoảng thời gian ngẫu nhiên từ 3000ms đến 5000ms rồi kết thúc hành động.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 */
fun onTiktokSharePostAction(context: SceneExecutionContext) =
    defineAction("Tiktok Share Post Action", context) {
        find(text(TiktokText.VIDEO_TAB) or text("Ảnh"))?.let {
            tap(it)
            waitUntil(selector =  (desc("Mẫu") and desc("Văn bản")) or (id(TiktokId.ADD_SOUND_TEXT)),maxMs = 120000L)
            endAction()
        }
    }

/**
 * Nhấn chọn thêm âm thanh cho video trong quy trình đăng tải bài viết TikTok.
 *
 * Quy trình thực hiện:
 * 1. Tìm nút/văn bản Thêm âm thanh (`TiktokId.ADD_SOUND_TEXT`). Nếu text hiển thị của nó chính xác là "Thêm âm thanh" (`TiktokText.ADD_SOUND`), 
 *    tiến hành tap vào nút này để mở màn hình chọn nhạc và đợi 5000ms.
 * 2. Nếu nút trên không khả dụng hoặc đã có nhạc, tìm nút Tiếp (`TiktokId.NEXT_BUTTON`), tap vào đó và đợi ngẫu nhiên từ 3000ms đến 5000ms.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 */
fun tapToAddMusic(context: SceneExecutionContext) = defineAction("Tap To Add Music", context) {
    find(id(TiktokId.ADD_SOUND_TEXT) or id("com.ss.android.ugc.trill:id/zct"))?.let {
        if (it.text == TiktokText.ADD_SOUND) {
            tap(it)
            wait(5000)
            endAction()
        }
    }
    find(text("Tiếp"))?.let {
        tap(it)
        wait(random(3000, 5000))
    }
    endAction()
}

/**
 * Nhấn chọn công cụ AI trên màn hình xem trước video (nếu có) và nhấn nút "Tiếp" để chuyển sang bước kế tiếp.
 *
 * Quy trình thực hiện:
 * 1. Tìm nút công cụ AI ([TiktokId.AI_BUTTON]). Nếu tìm thấy, thực hiện chạm (tap) và chờ ngẫu nhiên từ 1000ms đến 1500ms.
 * 2. Tìm nút "Tiếp" ([TiktokText.NEXT]). Nếu tìm thấy, thực hiện chạm (tap) và chờ ngẫu nhiên từ 1000ms đến 1500ms.
 *
 * @param context Ngữ cảnh thực thi hành động ([SceneExecutionContext]).
 */
fun tapAIToolAndProceed(context: SceneExecutionContext) = defineAction("Tap AI Tool And Proceed", context) {
    find(id(TiktokId.AI_BUTTON))?.let {
        tap(it)
        wait(random(1000, 1500))
    }
    find(text(TiktokText.NEXT))?.let {
        tap(it)
        wait(random(1000, 1500))
    }
}

fun tapAutoCut(context: SceneExecutionContext, action: Action) = defineAction("Tap AutoCut", context) {
    on(desc("Mẫu")) {
        if (it != null) {
            tap(it)
            waitUntil(text("Chọn mẫu"), maxMs = 120000).let { result ->
                if (result == null) {
                    pressBack()
                    endAction()
                }
            }
        }
        action()
    }
}

fun tapText(context: SceneExecutionContext, text: String, action: Action) = defineAction("Tap Text", context) {
    find(desc("Văn bản"))?.let {
        context.device.executeShellCommand("ime set com.google.android.inputmethod.latin/com.android.inputmethod.latin.LatinIME")
        tap(it)
        wait(random(1500, 3000))
        on(clazz("android.widget.EditText")) { editText ->
            humanType(editText, text)
            action()
            ///
            on(clazz("androidx.recyclerview.widget.RecyclerView")) { recyclerview ->
                val options = recyclerview?.children
                val fontButton = options?.getOrNull(0)
                val colorButton = options?.getOrNull(1)
                val styleButton = options?.getOrNull(2)
                val alignButton = options?.getOrNull(3)

                fontButton?.let {  button ->
                    tap(button)
                    wait(random(300, 900))
                    find(clazz("android.widget.HorizontalScrollView"))?.let { scrollview ->
                        val fontList = scrollview.children.first()
                        sometimes(60) {
                            fontList.scroll(Direction.RIGHT, 0.6f)
                        }
                        fontList.children.random().let { font ->
                            tap(font)
                        }
                    }
                }
                wait(random(300, 900))

                colorButton?.let { button ->
                    tap(button)
                    wait(random(300, 900))
                    find(clazz("android.widget.HorizontalScrollView"))?.let { scrollview ->
                        val colorList = scrollview.children.first()
                        repeat(random(0, 5)) {
                            colorList.scroll(Direction.RIGHT, 0.6f)
                            wait(random(200, 500))
                        }
                        colorList.children.random().let { color ->
                            tap(color)
                        }
                    }
                }
                wait(random(300, 900))

                styleButton?.let { button ->
                    repeat(random(0, 4)) {
                        tap(button)
                        wait(random(200, 500))
                    }
                }
                wait(random(300, 900))

                alignButton?.let { button ->
                    repeat(random(0, 4)) {
                        tap(button)
                        wait(random(200, 500))
                    }
                }
            }
            ///
            wait(random(1500, 3000))
            on(desc("Text")) { text -> tap(text)}
            wait(random(1500, 3000))
            pressBack()
        }
    }
}

/**
 * Nhấn nút Đăng để hoàn tất việc đăng tải video, đồng thời thực thi callback đi kèm.
 *
 * Quy trình thực hiện:
 * 1. Tìm nút có nhãn "Đăng" (`TiktokText.POST`) và thực hiện tap vào nút đó.
 * 2. Gọi hàm callback [action] được truyền vào để thực hiện các thao tác hậu kỳ hoặc xác minh tiếp theo.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 * @param action Callback hành động suspend sẽ được gọi ngay sau khi nhấn nút Đăng.
 */
fun tapToUpload(context: SceneExecutionContext, action: Action) =
    defineAction("Tap To Upload", context) {
        tap(find(text(TiktokText.POST)))
        action()
        endAction()
    }

/**
 * Chọn ngẫu nhiên một bài hát từ danh sách nhạc đề xuất của TikTok.
 *
 * Quy trình thực hiện:
 * 1. Tìm danh sách bài hát thông qua ID `TiktokId.MUSIC_LIST`.
 * 2. Lấy các item con đại diện cho các dòng chứa bài hát (`android.widget.LinearLayout`).
 * 3. Chọn ngẫu nhiên một item bài hát và thực hiện tap vào đó.
 * 4. Đợi một khoảng thời gian ngẫu nhiên từ 3000ms đến 5000ms để áp dụng nhạc, sau đó nhấn phím Back để quay lại.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 */
fun selectRandomMusic(context: SceneExecutionContext) =
    defineAction("Select random music", context) {
        find(id(TiktokId.MUSIC_LIST))?.findObjects(clazz("android.widget.LinearLayout").toBySelector())
            .run {
                tap(this?.random())
                wait(random(3000, 5000))
                pressBack()
            }
        endAction()
    }

/**
 * Nhập mô tả (caption) của video và nhấn nút Đăng để bắt đầu tải bài viết lên TikTok.
 *
 * Quy trình thực hiện:
 * 1. Lấy thông tin caption từ đối số (`context.args.getString("caption")`).
 * 2. Tìm ô nhập caption thông qua ID `TiktokId.CAPTION_INPUT`.
 * 3. Gõ nội dung caption kèm dấu cách phía sau bằng phương thức [humanType], sau đó đợi ngẫu nhiên từ 3000ms đến 5000ms.
 * 4. Tìm nút Đăng (`TiktokText.POST`) và tap vào.
 * 5. Chờ một khoảng thời gian dài ngẫu nhiên từ 20000ms đến 40000ms cho quá trình tải lên hoàn tất.
 * 6. Cuối cùng, thực hiện nhấn phím Home (`pressHome`) và dừng tiến trình kiểm thử (`stop`).
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 */
fun typeCaption(context: SceneExecutionContext) = defineAction("Type Caption", context) {
    val caption = context.args.getString("caption") ?: ""
    if (caption.isEmpty()) {
        stop("Caption trống")
    }
    val location = context.args.getString("location") ?: ""
    findAll(clazz("android.widget.EditText")).lastOrNull()?.let {
        humanType(it, "$caption ")
        wait(random(1000, 1500))
        pressBack()
        wait(random(1000, 1500))
        if (location.isNotEmpty()) {
            find(text("Vị trí"))?.let { locationButton ->
                tap(locationButton)
                wait(random(3000, 5000))
                find(id("com.android.permissioncontroller:id/permission_message"))?.let {
                    find(id("com.android.permissioncontroller:id/permission_allow_foreground_only_button"))?.click()
                    wait(random(3000, 5000))
                }
                find(clazz("android.widget.EditText"))?.let { inputText ->
                    inputText.text = location
                    wait(random(1000, 1500))
                    waitUntil(clazz("androidx.recyclerview.widget.RecyclerView"), maxMs=120000L)?.let { rcv ->
                        rcv.children.firstOrNull()?.let { target ->
                            tap(target)
                        }
                    }
                }
            }
            wait(random(1000, 1500))
        }
        find(text(TiktokText.POST))?.let { post ->
            tap(post)
            wait(random(20000, 40000))
            pressHome()
            stop()
        }
    }
    endAction()
}

/**
 * Chọn tài khoản trùng khớp với tên đăng nhập (username) tại trang kết quả tìm kiếm.
 *
 * Quy trình thực hiện:
 * 1. Kiểm tra nếu trên tab hiện tại (ví dụ tab "Top") đã xuất hiện ngay phần tử khớp với [username] (`TiktokId.SEARCH_USERNAME`),
 *    tiến hành Follow (nếu chưa follow) và tap vào tài khoản để mở trang cá nhân ngay mà không cần chuyển tab.
 * 2. Nếu chưa thấy trên tab hiện tại, tìm tab "Người dùng" (`TiktokText.USER_TAB`) và tap để chuyển tab, đợi danh sách tải.
 * 3. Tìm phần tử khớp với [username] (hỗ trợ cuộn tối đa 3 lần nếu danh sách dài).
 * 4. Nếu không tìm thấy người dùng phù hợp, gọi callback [onNoUser].
 * 5. Nếu tìm thấy, thực hiện bấm nút "Follow" (nếu đang ở trạng thái chưa follow) rồi tap mở trang cá nhân và chờ lưới video hiển thị.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 * @param username Tên tài khoản TikTok cần tìm kiếm và bấm vào.
 * @param onNoUser Callback hành động suspend được gọi khi không tìm thấy bất kỳ người dùng nào khớp với [username].
 */
fun selectUser(
    context: SceneExecutionContext, username: String, onNoUser: Action
) = defineAction("Select User", context) {
    val cleanUsername = username.trim().removePrefix("@")
    val userSelector = id(TiktokId.SEARCH_USERNAME) and text(cleanUsername)

    // 1. Thử tìm ngay trên tab hiện tại (thường tab Top đã hiển thị thẻ User chính xác ở đầu trang)
    var targetUser = find(userSelector)

    // 2. Nếu chưa thấy ở tab hiện tại, chuyển sang tab "Người dùng"
    if (targetUser == null) {
        val userTab = find(desc(TiktokText.USER_TAB)) ?: find(text(TiktokText.USER_TAB))
        if (userTab != null && !userTab.isSelected) {
            tap(userTab)
            wait(random(1200, 2000))
        }
        targetUser = waitUntil(userSelector, maxMs = 6000L)
    }

    // 3. Nếu vẫn chưa thấy trên màn hình đầu của tab Người dùng, thử cuộn xuống tối đa 2 lần
    var scrollAttempts = 0
    while (targetUser == null && scrollAttempts < 2) {
        scrollAttempts++
        swipeUp()
        wait(random(1000, 1500))
        targetUser = find(userSelector)
    }

    if (targetUser == null) {
        onNoUser()
    } else {
        // Bấm nút Follow trên dòng chứa user nếu chưa follow (text == "Follow")
        runCatching {
            val rowContainer = targetUser.parent?.parent
            val followBtn = rowContainer?.findObject(id(TiktokId.FOLLOW_USER_BUTTON).toBySelector())
                ?.takeIf { it.text == "Follow" }
                ?: rowContainer?.findObject(text("Follow").toBySelector())
            if (followBtn != null) {
                wait(random(800, 1800))
                tap(followBtn)
                wait(random(1000, 2000))
            }
        }
        tap(targetUser)
        waitUntil(desc("Bài đăng") or id(TiktokId.PROFILE_VIDEO_GRID), maxMs = 10000L)
    }
    endAction()
}

/**
 * Lấy danh sách video từ lưới bài viết trên trang hồ sơ (Profile) của người dùng hiện tại và thực hiện hành động callback chỉ định.
 *
 * Quy trình thực hiện:
 * 1. Tìm lưới video trên Profile thông qua ID `TiktokId.PROFILE_VIDEO_GRID`.
 * 2. Tìm tất cả các phần tử video con thông qua ID `TiktokId.PROFILE_VIDEO_ITEM`.
 * 3. Nếu danh sách video trống hoặc null, gọi callback [onNoVideos] để xử lý tình huống không có video nào.
 * 4. Nếu có video, thực hiện callback [action] bằng cách truyền vào danh sách video tìm được để xử lý tiếp.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 * @param onNoVideos Callback hành động suspend được gọi khi trang cá nhân không có video nào.
 * @param action Callback hành động suspend nhận danh sách các đối tượng video (`List<UiObject2>`) để tiếp tục xử lý.
 */
fun onChooseVideo(
    context: SceneExecutionContext,
    onNoVideos: Action,
    action: suspend ActionBuilder.(List<UiObject2>) -> Unit
) = defineAction("Choose Video", context) {
    find(clazz("android.widget.GridView")).let { grid ->
        val videoItems = grid?.children?.mapNotNull {
            it.findObjects(clazz("android.widget.TextView").toBySelector()).lastOrNull()
        } ?: emptyList()
//        val videoItems = grid?.children?.mapNotNull {
//            it.children.lastOrNull()?.children?.lastOrNull()
//        } ?: emptyList()
        if (videoItems.isEmpty()) {
            onNoVideos()
        } else {
            action(videoItems)
        }
    }
}

/**
 * Mở menu tùy chọn hoặc chia sẻ của video đang phát hiện tại.
 *
 * Quy trình thực hiện:
 * 1. Tìm nút Chia sẻ/Xem thêm (`TiktokId.SHARE_BUTTON`) trên màn hình xem video.
 * 2. Tap vào nút đó để mở menu tùy chọn/chia sẻ của video.
 * 3. Đợi một khoảng thời gian ngẫu nhiên từ 1000ms đến 1500ms để menu hiển thị đầy đủ.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 */
fun openVideoMenu(
    context: SceneExecutionContext
) = defineAction("Open Video Menu", context) {
    find(id(TiktokId.SHARE_BUTTON))?.let {
        tap(it)
        wait(random(1000, 1500))
    }
    endAction()
}

/**
 * Cuộn ngang bảng chọn chia sẻ để tìm và nhấn nút Xóa video.
 *
 * Quy trình thực hiện:
 * 1. Tìm vùng chứa danh sách các tùy chọn chia sẻ thông qua ID `TiktokId.SHARE_OPTIONS_LIST`.
 * 2. Thực hiện cuộn ngang sang phía bên phải (Direction.RIGHT) với tỷ lệ cuộn 0.8f để hiển thị các nút chức năng ẩn phía sau (như nút Xóa).
 * 3. Đợi ngẫu nhiên từ 1000ms đến 1500ms.
 * 4. Tìm nút có nhãn "Xóa" (`TiktokText.DELETE`) và tap vào, sau đó tiếp tục đợi ngẫu nhiên từ 1000ms đến 1500ms.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 */
fun swipeToChooseDelete(
    context: SceneExecutionContext
) = defineAction("Swipe to choose delete", context) {
    find(id(TiktokId.SHARE_OPTIONS_LIST))?.scroll(Direction.RIGHT, 0.8f)
    wait(random(1000, 1500))
    find(text(TiktokText.DELETE))?.let {
        tap(it)
        wait(random(1000, 1500))
    }
    endAction()
}

/**
 * Nhấn nút xác nhận Xóa và Đăng lại trên popup/bottom sheet của TikTok.
 *
 * Quy trình thực hiện:
 * 1. Tìm nút "Xóa và Đăng lại" thông qua ID `TiktokId.DELETE_AND_REPOST_BUTTON`.
 * 2. Nếu tìm thấy nút này, thực hiện tap vào nó.
 * 3. Đợi ngẫu nhiên từ 2000ms đến 5000ms để quá trình xử lý diễn ra hoàn tất trước khi kết thúc hành động.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 */
fun tapDeleteAndRepost(
    context: SceneExecutionContext
) = defineAction("Delete and repost", context) {
    find(id(TiktokId.DELETE_AND_REPOST_BUTTON)).let {
        if (it != null) {
            tap(it)
        }
        wait(random(2000, 3000))
        endAction()
    }
}

/**
 * Xác nhận hành động Xóa (chỉ xóa, không đăng lại) trên popup hỏi ý kiến đăng lại.
 *
 * Quy trình thực hiện:
 * 1. Tìm nút Xóa trong popup thông qua ID `TiktokId.DELETE_IN_REPOST_POPUP`.
 * 2. Nếu tìm thấy nút này, thực hiện tap vào để xác nhận xóa.
 * 3. Gọi hàm callback [action] truyền vào để tiếp tục quy trình xử lý sau khi xóa.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 * @param action Callback hành động suspend được gọi sau khi bấm nút Xóa.
 */
fun tapDeleteInRepostPopup(
    context: SceneExecutionContext,
    action: Action
) = defineAction("Delete in repost popup", context) {
    find(text(TiktokText.DELETE))?.let {
        tap(it)
        wait(random(1500, 2000))
        find(text(TiktokText.DELETE))?.click()
        action()
    }
    endAction()
}

/**
 * Xác nhận xóa bài viết vĩnh viễn trên popup xác nhận xóa của ứng dụng TikTok.
 *
 * Quy trình thực hiện:
 * 1. Tìm nút xác nhận Xóa thông qua ID `TiktokId.CONFIRM_DELETE_BUTTON`.
 * 2. Nếu tìm thấy, thực hiện tap vào nút đó để xóa vĩnh viễn video.
 * 3. Đợi ngẫu nhiên từ 2000ms đến 5000ms để hệ thống thực thi xóa và cập nhật giao diện.
 * 4. Gọi callback [action] truyền vào để tiếp tục luồng xử lý tiếp theo.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 * @param action Callback hành động suspend được gọi sau khi hoàn thành các bước bấm nút và chờ đợi.
 */
fun tapDeleteVideo(
    context: SceneExecutionContext,
    action: Action
) = defineAction("Delete post", context) {
    find(id(TiktokId.CONFIRM_DELETE_BUTTON))?.let { deleteButton ->
        tap(deleteButton)
        wait(random(2000, 5000))
        action()
    }
    endAction()
}

/**
 * Hành động nhấn nút "Quay" từ màn hình Trang chủ để mở giao diện quay/tải lên.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 */
fun openRecordFromHome(
    context: SceneExecutionContext
) = defineAction("Open Record From Home", context) {
    find(desc("Quay"))?.let {
        tap(it)
        waitUntil(text(TiktokText.RECORD_POST) and text(TiktokText.ADD_SOUND))
    }
}

/**
 * Hành động nhấn nút mở thư viện/album tải lên từ màn hình quay.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 */
fun openUploadLibrary(
    context: SceneExecutionContext
) = defineAction("Open Upload Library", context) {
    find(id(TiktokId.UPLOAD_BUTTON))?.let {
        tap(it)
        waitUntil(text(TiktokText.ALL) and text(TiktokText.VIDEO_TAB) and text(TiktokText.IMAGE))
    }
}

/**
 * Hành động chuyển sang tab "Văn bản" để tạo ảnh AI từ văn bản mô tả.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 */
fun chooseTextToImageTab(
    context: SceneExecutionContext
) = defineAction("Choose Text Tab", context) {
    find(text(TiktokText.TEXT_TAB))?.let {
        tap(it)
        waitUntil(text(TiktokText.CREATE_IMAGE_FROM_TEXT_TITLE))
    }
}

/**
 * Hành động nhập prompt văn bản và nhấn "Tiếp" để chuyển sang bước chọn phong cách ảnh.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 * @param promptText Nội dung văn bản mô tả hình ảnh AI cần tạo.
 */
fun inputPromptAndProceed(
    context: SceneExecutionContext,
    promptText: String
) = defineAction("Input Prompt And Proceed", context) {
    if (promptText.isEmpty()) {
        stop("Chưa nhập text mô tả")
    }
    find(clazz("android.widget.EditText"))?.let {
        humanType(it, promptText)
        wait(1500)
        find(text(TiktokText.NEXT))?.let { continueButton ->
            tap(continueButton)
            waitUntil(text(TiktokText.SELECT_STYLE_TITLE) and text(TiktokText.NEXT))
        }
    }
}

/**
 * Hành động cuộn và chọn ngẫu nhiên một phong cách ảnh AI, sau đó bấm "Tiếp".
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 */
fun selectRandomImageStyle(
    context: SceneExecutionContext
) = defineAction("Select Random Image Style", context) {
    on(clazz("androidx.recyclerview.widget.RecyclerView")) { recyclerView ->
        while (true) {
            if (random(100) < 60) {
                recyclerView?.scroll(Direction.RIGHT, 0.6f)
                wait(random(500, 1000))
            } else {
                val templates = recyclerView?.children
                if (!templates.isNullOrEmpty()) {
                    tap(templates.random())
                    wait(random(900, 1500))
                    find(text(TiktokText.NEXT))?.let { continueButton -> tap(continueButton) }
                    wait(random(900, 1500))
                    endAction()
                }
            }
        }
    }
}

/**
 * Hành động chuyển hướng từ màn hình Trang chủ (Home) sang màn hình Hồ sơ cá nhân (Profile).
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 */
fun openProfileFromHome(
    context: SceneExecutionContext
) = defineAction("Open Profile From Home", context) {
    (find(desc(TiktokText.PROFILE_TAB)) ?: find(text(TiktokText.PROFILE_TAB)))?.let { profileTab ->
        tap(profileTab)
    }
}

/**
 * Hành động nhấn nút "Sửa hồ sơ" từ màn hình Trang cá nhân (Profile).
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 */
fun openEditProfile(
    context: SceneExecutionContext
) = defineAction("Open Edit Profile", context) {
    find(id("com.ss.android.ugc.trill:id/rh3"))?.let { editButton ->
        tap(editButton)
        waitUntil(text(TiktokText.CHANGE_PHOTO))
    }
}

/**
 * Hành động chọn mục "Tiểu sử" trên màn hình Chỉnh sửa hồ sơ.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 */
fun openEditBio(
    context: SceneExecutionContext
) = defineAction("Open Edit Bio", context) {
    find(text(TiktokText.BIO))?.let { bioItem ->
        tap(bioItem)
        waitUntil(text(TiktokText.BIO) and (text(TiktokText.EDIT_BIO_HINT) or clazz("android.widget.EditText")))
    }
}

/**
 * Hành động cập nhật nội dung tiểu sử mới:
 * 1. Kiểm tra nếu nội dung tiểu sử hiện tại đã giống [bioText] thì thoát sớm (không ghi đè).
 * 2. Điền [bioText] vào ô EditText.
 * 3. Kiểm tra nút "Lưu" (Save), nếu khả dụng (`isEnabled == true`) thì bấm Lưu.
 * 4. Chờ giao diện quay lại màn hình "Sửa hồ sơ" rồi bấm Home và kết thúc kịch bản thành công.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 * @param bioText Nội dung tiểu sử cần cập nhật.
 */
fun updateBioAndSave(
    context: SceneExecutionContext,
    bioText: String
) = defineAction("Update Bio And Save", context) {
    find(clazz("android.widget.EditText"))?.let { inputField ->
        if (inputField.text == bioText) {
            AkiLog.i(LogTag.ACTION, "Tiểu sử hiện tại đã trùng khớp với '$bioText', bỏ qua cập nhật")
            pressBack()
            wait(random(500, 1000))
            pressHome()
            stop("Tiểu sử đã trùng khớp, không cần cập nhật")
            return@defineAction
        }

        inputField.text = bioText
        wait(random(600, 1200))

        val saveButton = find(desc(TiktokText.SAVE)) ?: find(text(TiktokText.SAVE))
        if (saveButton != null && saveButton.isEnabled) {
            tap(saveButton)
            val returned = waitUntil(text(TiktokText.EDIT_PROFILE))
            if (returned != null) {
                wait(random(500, 1000))
                pressHome()
                stop("Cập nhật tiểu sử thành công")
            } else {
                pressBack()
                pressHome()
                stop("Đã bấm Lưu tiểu sử")
            }
        } else {
            AkiLog.w(LogTag.ACTION, "Nút Lưu tiểu sử không khả dụng hoặc không tìm thấy")
            pressBack()
            pressHome()
            stop("Nút Lưu không khả dụng")
        }
    }
}

