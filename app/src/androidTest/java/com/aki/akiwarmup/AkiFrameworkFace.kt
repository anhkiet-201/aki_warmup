package com.aki.akiwarmup

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aki.akiwarmup.core.dsl.runScene
import com.aki.akiwarmup.core.logger.AkiLog
import com.aki.akiwarmup.core.logger.LogTag
import com.aki.akiwarmup.facebook.GroupPost
import com.aki.akiwarmup.facebook.action.chooseGroup
import com.aki.akiwarmup.facebook.action.selectGroup
import com.aki.akiwarmup.facebook.action.typeCaption
import com.aki.akiwarmup.facebook.scene.FaceBaseBehaviors
import com.aki.akiwarmup.facebook.scene.faceSceneDefine
import com.aki.akiwarmup.facebook.screen.onChooseGroupsView
import com.aki.akiwarmup.facebook.screen.onHomeView
import com.aki.akiwarmup.facebook.screen.onInputCaptionView
import com.aki.akiwarmup.facebook.screen.onSelectGroupsView
import com.aki.akiwarmup.tiktok.action.keyWorlds
import com.aki.akiwarmup.tiktok.action.onChooseVideo
import com.aki.akiwarmup.tiktok.action.onTiktokSharePostAction
import com.aki.akiwarmup.tiktok.action.selectRandomMusic
import com.aki.akiwarmup.tiktok.action.selectUser
import com.aki.akiwarmup.tiktok.action.tapDeleteAndRepost
import com.aki.akiwarmup.tiktok.action.tapDeleteInRepostPopup
import com.aki.akiwarmup.tiktok.action.tapDeleteVideo
import com.aki.akiwarmup.tiktok.action.tapToAddMusic
import com.aki.akiwarmup.tiktok.action.tapToUpload
import com.aki.akiwarmup.tiktok.action.typeCaption
import com.aki.akiwarmup.tiktok.action.typeSearchKeyword
import com.aki.akiwarmup.tiktok.action.watchVideo
import com.aki.akiwarmup.tiktok.model.AutoRate
import com.aki.akiwarmup.tiktok.scene.tiktokSceneDefine
import com.aki.akiwarmup.tiktok.scene.TiktokBaseBehaviors
import com.aki.akiwarmup.tiktok.scene.TiktokCommentBehaviors
import com.aki.akiwarmup.tiktok.scene.TiktokDeleteVideoBehaviors
import com.aki.akiwarmup.tiktok.screen.onAddInfoView
import com.aki.akiwarmup.tiktok.screen.onDeletePopup
import com.aki.akiwarmup.tiktok.screen.onHome
import com.aki.akiwarmup.tiktok.screen.onProfile
import com.aki.akiwarmup.tiktok.screen.onRepostPopup
import com.aki.akiwarmup.tiktok.screen.onSearch
import com.aki.akiwarmup.tiktok.screen.onSearchResult
import com.aki.akiwarmup.tiktok.screen.onSelectMusicSheet
import com.aki.akiwarmup.tiktok.screen.onTiktokSharePost
import com.aki.akiwarmup.tiktok.screen.onVideoPreview
import com.aki.akiwarmup.tiktok.screen.onVideoView
import com.google.gson.Gson
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Lớp chứa các kịch bản kiểm thử tích hợp giao diện người dùng E2E (End-to-End) dành riêng cho Facebook
 * (và có thể mở rộng cho TikTok) dựa trên Aki Framework DSL.
 *
 * Để khởi chạy các kịch bản kiểm thử trong lớp này thông qua ADB (Android Debug Bridge), sử dụng lệnh instrumentation:
 * ```bash
 * adb shell am instrument -w -r -e class com.aki.akiwarmup.AkiFrameworkFace com.aki.akiwarmup.test/androidx.test.runner.AndroidJUnitRunner
 * ```
 */
@RunWith(AndroidJUnit4::class)
class AkiFrameworkFace {

    /**
     * Kịch bản tự động đăng bài lên các nhóm Facebook (Auto Post Groups).
     *
     * Kịch bản này tự động hóa quy trình tìm kiếm, tích chọn các nhóm Facebook theo từ khóa chỉ định,
     * sau đó viết caption và đăng bài viết.
     *
     * **Các đối số truyền vào qua Instrumentation Arguments (`context.args`):**
     * - `keywords`: Chuỗi chứa các từ khóa tìm kiếm nhóm, cách nhau bởi ký tự gạch đứng `|` (Ví dụ: `nhom 1|nhom 2|mua ban`).
     * - `caption`: Nội dung đoạn văn bản (caption) của bài viết cần đăng.
     *
     * **Luồng kịch bản chi tiết:**
     * 1. Khởi tạo cảnh Face với tên "Group Post". Tách tham số `keywords` thành danh sách. Nếu danh sách rỗng, dừng kiểm thử.
     * 2. Xử lý màn hình Chọn nhóm (`onChooseGroupsView`): Quét và nhấn chọn các nhóm phù hợp từ danh sách bằng [chooseGroup].
     * 3. Xử lý màn hình Chia sẻ bài viết (`onSelectGroupsView`): Quét và tích chọn thêm các nhóm để chia sẻ bài viết bằng [selectGroup].
     * 4. Đọc tham số `caption` từ đối số. Nếu caption rỗng, dừng kiểm thử.
     * 5. Xử lý màn hình Nhập caption (`onInputCaptionView`): Soạn thảo văn bản và nhấn Đăng bài bằng [typeCaption].
     * 6. Xử lý màn hình Trang chủ (`onHomeView`): Đợi quá trình tải lên hoàn tất và dừng kịch bản với trạng thái thành công ("OK").
     */
    @Test
    fun autoPostGroups() = runScene {
        scene {
            faceSceneDefine("Group Post", context) {
                include(FaceBaseBehaviors)
                AkiLog.e(LogTag.ENGINE,(context.args.getString("data") ?: ""))
                val data = Gson().fromJson<GroupPost>((context.args.getString("data") ?: "{\n" +
                "  \"keywords\": [\n" +
                "    \"Vsip 3\",\n" +
                "    \"Nam tân uyên\",\n" +
                "    \"KCN Vsip 3\",\n" +
                "    \"Tuyển dụng Bình Dương\",\n" +
                "    \"Việc làm phổ thông\",\n" +
                "    \"Công ty Farina\",\n" +
                "    \"Tuyển công nhân đóng gói\"\n" +
                "  ],\n" +
                "  \"captions\": [\n" +
                "    \"\uD83C\uDFED VIỆC LÀM ỔN ĐỊNH - THU NHẬP HẤP DẪN TẠI KCN VSIP 3, BÌNH DƯƠNG!\\n\\nCông ty Farina đang cần tuyển gấp đồng đội cho các vị trí:\\n\uD83D\uDCE6 Đóng gói bánh\\n⚙\uFE0F Vận hành máy trộn\\n\\n\uD83D\uDCB5 Thu nhập cực tốt:\\n☀\uFE0F Ca ngày: 260.000đ/8 giờ\\n\uD83C\uDF19 Ca đêm: 305.000đ/8 giờ\\n⏰ Tăng ca: 40.000đ/giờ\\n\uD83D\uDCB8 Hỗ trợ ứng lương hằng tuần cực kỳ linh hoạt!\\n\\n\uD83C\uDF81 Quyền lợi siêu hời:\\n\uD83C\uDF5A Bao cơm giữa ca\\n\uD83D\uDC55 Cấp đồng phục sạch sẽ, chuyên nghiệp\\n❄\uFE0F Môi trường làm việc mát mẻ, hiện đại và sạch sẽ.\\n\\n\uD83D\uDCCD Địa điểm: KCN VSIP 3 (Gần khu vực Nam Tân Uyên), Bình Dương.\\n\uD83D\uDC49 Nhanh tay đăng ký ứng tuyển ngay hôm nay để nhận lịch phỏng vấn sớm nhất nhé mọi người ơi!\",\n" +
                "    \"\uD83D\uDD25 TUYỂN DỤNG GẤP: CÔNG NHÂN NAM/NỮ LÀM VIỆC TẠI CÔNG TY FARINA (KCN VSIP 3)\\n\\nBạn đang tìm kiếm một công việc có môi trường mát mẻ, sạch sẽ và thu nhập ổn định? Đăng ký ngay thôi!\\n\\n\uD83D\uDCCB Vị trí tuyển dụng:\\n1\uFE0F⃣ Đóng gói bánh (Nhẹ nhàng, dễ làm)\\n2\uFE0F⃣ Vận hành máy trộn (Được hướng dẫn công việc)\\n\\n\uD83D\uDCB0 Mức lương hấp dẫn:\\n• Ca ngày: 260k/8h\\n• Ca đêm: 305k/8h\\n• Tăng ca: 40k/h\\n✔\uFE0F Đặc biệt: Hỗ trợ ứng lương hàng tuần giúp bạn chủ động chi tiêu.\\n\\n✨ Phúc lợi đi kèm:\\n• Được bao cơm miễn phí.\\n• Được cấp phát đồng phục sạch sẽ.\\n• Làm việc trong phòng máy lạnh, hiện đại.\\n\\n\uD83D\uDCCC Khu vực tuyển dụng chính: KCN VSIP 3, Bình Dương (Rất gần Nam Tân Uyên).\\n\uD83D\uDCAC Nhắn tin ngay cho page hoặc để lại số điện thoại dưới bình luận để nhận việc đi làm ngay nhé!\",\n" +
                "    \"\uD83D\uDCB8 CẦN VIỆC LÀM NGAY - TIỀN RA ĐỀU TAY - CÓ HỖ TRỢ ỨNG LƯƠNG HẰNG TUẦN!\\n\\nNhà máy Farina tại KCN VSIP 3, Bình Dương đang rộng cửa chào đón anh chị em lao động với mức thu nhập siêu tốt:\\n\\n☀\uFE0F Làm ngày: 260.000đ / 8 tiếng\\n\uD83C\uDF19 Làm đêm: 305.000đ / 8 tiếng\\n⏰ Tăng ca: 40.000đ / tiếng\\n\\n\uD83D\uDEE0\uFE0F Công việc cụ thể:\\n- Đóng gói bánh thành phẩm.\\n- Vận hành máy trộn nguyên liệu.\\n\\n\uD83C\uDF1F Đi làm không lo đói vì đã có BAO CƠM. Môi trường làm việc cực sạch sẽ, hiện đại, cấp đồng phục sẵn sàng.\\n\uD83D\uDCCD Vị trí: KCN VSIP 3, gần Nam Tân Uyên, Bình Dương.\\n\\n\uD83D\uDCDE Inbox ngay để nhận vị trí ứng tuyển sớm nhất!\",\n" +
                "    \"\uD83C\uDFED CÔNG TY FARINA (KCN VSIP 3, BÌNH DƯƠNG) TUYỂN DỤNG LAO ĐỘNG PHỔ THÔNG\\n\\nKhông yêu cầu kinh nghiệm cao, chỉ cần chăm chỉ và chịu khó! Có người hướng dẫn công việc từ A-Z.\\n\\n\uD83D\uDCCC Công việc:\\n- Đóng gói bánh\\n- Vận hành máy trộn\\n\\n\uD83D\uDC8E Thu nhập thực tế:\\n- Ca ngày: 260.000đ/8h\\n- Ca đêm: 305.000đ/8h\\n- Tiền tăng ca: 40.000đ/h\\n- Có hỗ trợ ứng lương hàng tuần cho anh em khó khăn.\\n\\n\uD83C\uDF40 Môi trường làm việc sạch sẽ, hiện đại. Được công ty bao cơm và cấp đồng phục đầy đủ.\\n\uD83D\uDCCD Địa điểm làm việc tiện lợi tại KCN VSIP 3 (giáp ranh khu Nam Tân Uyên).\\n\\n\uD83D\uDCE9 Hãy gửi thông tin đăng ký ứng tuyển ngay hôm nay!\",\n" +
                "    \"❄\uFE0F TÌM VIỆC LÀM PHÒNG LẠNH - SẠCH SẼ - LƯƠNG CAO TẠI BÌNH DƯƠNG?\\n\\nHãy về với đội của Công ty Farina tại KCN VSIP 3 ngay nhé!\\n\\n\uD83D\uDC49 Chi tiết công việc:\\n- Đóng gói bánh ngọt\\n- Vận hành máy trộn\\n\\n\uD83D\uDCB5 Thu nhập cực kỳ cạnh tranh:\\n- Ca ngày: 260k/8 giờ\\n- Ca đêm: 305k/8 giờ\\n- Tăng ca: 40k/giờ\\n- Hỗ trợ ứng lương hằng tuần.\\n\\n\uD83C\uDF81 Chế độ cực tốt: Bao cơm, cấp đồng phục sạch sẽ, làm việc trong môi trường hiện đại, mát mẻ.\\n\uD83D\uDCCD Địa chỉ: KCN VSIP 3, Bình Dương (Gần vòng xoay Nam Tân Uyên).\\n\\n\uD83D\uDCAC Liên hệ ngay để được hướng dẫn nhận việc nhanh chóng!\",\n" +
                "    \"\uD83D\uDE80 VIỆC THẬT LƯƠNG THẬT - ĐI LÀM NGAY TẠI KCN VSIP 3 BÌNH DƯƠNG\\n\\nCông ty Farina thông báo tuyển dụng nhân sự cho dây chuyền sản xuất:\\n\uD83D\uDCE6 Vị trí 1: Đóng gói bánh\\n\uD83E\uDD63 Vị trí 2: Vận hành máy trộn\\n\\n\uD83D\uDCB0 Thu nhập chi tiết:\\n- Ca 8 tiếng ngày: 260.000đ\\n- Ca 8 tiếng đêm: 305.000đ\\n- Tăng ca: 40.000đ/giờ\\n- Nhận ứng lương hàng tuần để trang trải cuộc sống.\\n\\n❤\uFE0F Bạn được gì khi làm việc ở đây?\\n- Cơm nước giữa ca hoàn toàn miễn phí.\\n- Đồng phục được cấp phát sạch sẽ.\\n- Môi trường nhà xưởng hiện đại, chuẩn sạch.\\n\\n\uD83D\uDCCD Địa điểm: KCN VSIP 3, Bình Dương (khu vực lân cận Nam Tân Uyên rất tiện đi lại).\\n\uD83D\uDC49 Để lại bình luận [HỌ TÊN + SĐT] để bộ phận tuyển dụng liên hệ lại ngay!\",\n" +
                "    \"\uD83D\uDD14 TUYỂN GẤP NAM/NỮ LAO ĐỘNG PHỔ THÔNG - KHÔNG YÊU CẦU BẰNG CẤP\\n\\nNhà máy bánh Farina (KCN VSIP 3, Bình Dương) tuyển dụng nhân sự làm việc lâu dài hoặc thời vụ:\\n\\n\uD83D\uDC49 Công việc chính: Đóng gói bánh, vận hành máy trộn nguyên liệu.\\n\\n\uD83D\uDCB5 Thu nhập:\\n- Ca ngày: 260.000đ/8h\\n- Ca đêm: 305.000đ/8h\\n- Tăng ca: 40.000đ/giờ\\n- Có chính sách ứng lương mỗi tuần cực tốt cho nhân viên.\\n\\n\uD83C\uDF81 Quyền lợi đi kèm:\\n- Công ty bao cơm ngon miệng.\\n- Môi trường sạch sẽ, hiện đại, máy lạnh mát mẻ.\\n- Cấp đồng phục làm việc chu đáo.\\n\\n\uD83D\uDCCD Làm việc tại: KCN VSIP 3, Bình Dương (Thuộc khu vực Nam Tân Uyên).\\n\uD83D\uDCE9 Inbox ngay để nhận lịch phỏng vấn trực tiếp!\",\n" +
                "    \"\uD83D\uDE4B\u200D♂\uFE0F ANH EM KHU VỰC NAM TÂN UYÊN, KCN VSIP 3 ĐANG TÌM VIỆC LÀM? \\n\\nHãy tham khảo ngay cơ hội việc làm hấp dẫn tại Công ty Farina:\\n\\n\uD83D\uDD39 Vị trí: Đóng gói bánh & Vận hành máy trộn.\\n\uD83D\uDD39 Mức lương:\\n- 260.000đ cho ca ngày 8 tiếng.\\n- 305.000đ cho ca đêm 8 tiếng.\\n- Tăng ca tính thêm 40.000đ/giờ.\\n- Có chế độ hỗ trợ ứng lương hàng tuần cực nhanh gọn.\\n\\n\uD83D\uDD39 Quyền lợi vượt trội:\\n- Môi trường làm việc siêu sạch sẽ, khang trang và hiện đại.\\n- Được cấp đồng phục và bao cơm đầy đủ.\\n\\n\uD83D\uDCDE Liên hệ đăng ký ngay bằng cách nhắn tin trực tiếp cho fanpage để được xếp lịch sớm nhất!\",\n" +
                "    \"\uD83C\uDFED CÔNG TY FARINA TUYỂN DỤNG SỐ LƯỢNG LỚN - ĐI LÀM NGAY TẠI BÌNH DƯƠNG\\n\\nĐịa điểm: KCN VSIP 3, Bình Dương (gần Nam Tân Uyên)\\n\\n\uD83D\uDCE6 Các vị trí đang trống:\\n- Đóng gói bánh\\n- Vận hành máy trộn\\n\\n\uD83D\uDCB5 Chế độ thu nhập cực tốt:\\n- Ca ngày: 260.000đ/8 giờ\\n- Ca đêm: 305.000đ/8 giờ\\n- Tăng ca: 40.000đ/giờ\\n- Hỗ trợ ứng lương hằng tuần.\\n\\n\uD83C\uDF81 Đãi ngộ đặc biệt:\\n- Bao cơm miễn phí tại nhà ăn công ty.\\n- Được cấp đồng phục sạch sẽ.\\n- Làm việc trong môi trường sạch sẽ, hiện đại, đảm bảo sức khỏe.\\n\\n\uD83D\uDC49 Nhấn nút Gửi tin nhắn để nhận tư vấn chi tiết và nộp hồ sơ ngay hôm nay!\",\n" +
                "    \"\uD83C\uDF1F CƠ HỘI VIỆC LÀM TỐT - THU NHẬP CAO TẠI KCN VSIP 3 BÌNH DƯƠNG\\n\\nBạn muốn làm việc trong một môi trường sạch sẽ, hiện đại và có thu nhập ổn định? Công ty Farina chính là điểm dừng chân lý tưởng dành cho bạn!\\n\\n\uD83D\uDCCB Tuyển dụng vị trí:\\n- Đóng gói bánh ngọt\\n- Vận hành máy trộn\\n\\n\uD83D\uDCB0 Thu nhập hấp dẫn:\\n- Ca ngày (8 tiếng): 260.000đ\\n- Ca đêm (8 tiếng): 305.000đ\\n- Tăng ca: 40.000đ/giờ\\n- Hỗ trợ cho ứng lương hàng tuần cực tiện lợi.\\n\\n\uD83C\uDF81 Quyền lợi cực chất: Được cấp đồng phục sạch sẽ, bao cơm giữa ca, môi trường làm việc đạt chuẩn hiện đại.\\n\uD83D\uDCCD Địa điểm: KCN VSIP 3 (giáp ranh Nam Tân Uyên), Bình Dương.\\n\\n\uD83D\uDCE9 Đừng bỏ lỡ cơ hội! Inbox ngay thông tin của bạn để chúng tôi liên hệ phỏng vấn đi làm ngay nhé!\"\n" +
                "  ]\n" +
                "}"),
                    GroupPost::class.java)
                AkiLog.e(LogTag.ENGINE,data.toString())
                val keywords = data.keywords
                if (keywords.isEmpty()) {
                    context.stop("Danh sách từ khóa rỗng")
                }

                onChooseGroupsView(context) {
                    chooseGroup(context, data)
                }

                onSelectGroupsView(context) {
                    selectGroup(context, data)
                }

                val captions = data.captions
                if (captions.isEmpty()) {
                    context.stop("Caption rỗng")
                }
                onInputCaptionView(context) {
                    typeCaption(context, captions.random())
                }

                onHomeView(context) {
                    action("Wait for upload") {
                        stop("OK")
                    }
                }
            }
        }

        loop {  }
    }
}
