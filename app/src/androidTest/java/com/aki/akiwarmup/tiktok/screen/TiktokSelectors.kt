package com.aki.akiwarmup.tiktok.screen

object TiktokText {
    /** Nhãn hoặc nút chuyển hướng đến trang chủ (Home) */
    const val HOME   = "Trang chủ"

    /** Nhãn hoặc nút thực hiện chức năng Tìm kiếm (Search) */
    const val SEARCH = "Tìm kiếm"

    /** Nhãn hoặc văn bản thông báo quảng bá đề xuất */
    const val RECOMMENDED_PROMOTION = "Quảng bá đề xuất"

    /** Nút Đăng lại bài viết */
    const val REPOST = "Đăng lại"

    /** Nút Sao chép Liên kết của video */
    const val COPY_LINK = "Sao chép Liên kết"

    /** Tab Video trong kết quả tìm kiếm hoặc share */
    const val VIDEO_TAB = "Video"

    /** Nút xác nhận Đã hiểu trên các dialog thông báo */
    const val UNDERSTOOD = "Đã hiểu"

    /** Nút từ chối quyền hoặc từ chối thao tác Không cho phép */
    const val NOT_ALLOWED = "Không cho phép"

    /** Nút hoặc văn bản Thêm âm thanh khi đăng video */
    const val ADD_SOUND = "Thêm âm thanh"

    /** Nút Đăng video/bài viết */
    const val POST = "Đăng"

    /** Tab Người dùng trong kết quả tìm kiếm */
    const val USER_TAB = "Người dùng"

    /** Nút hoặc nhãn Xóa video/bài viết */
    const val DELETE = "Xóa"

    /** Nút ĐĂNG lớn trên màn hình quay (Record) */
    const val RECORD_POST = "ĐĂNG"

    /** Tùy chọn Chọn nhiều ảnh/video */
    const val SELECT_MULTIPLE = "Chọn nhiều"

    /** Tab Tất cả trong album */
    const val ALL = "Tất cả"

    /** Tab Ảnh trong album */
    const val IMAGE = "Ảnh"

    /** Tab Văn bản để tạo ảnh AI */
    const val TEXT_TAB = "Văn bản"

    /** Tiêu đề màn hình tạo ảnh từ văn bản AI */
    const val CREATE_IMAGE_FROM_TEXT_TITLE = "TẠO HÌNH ẢNH TỪ VĂN BẢN CỦA BẠN"

    /** Tiêu đề chọn phong cách ảnh AI */
    const val SELECT_STYLE_TITLE = "Chọn một phong cách"

    /** Nút Tiếp tục */
    const val NEXT = "Tiếp"
}

object TiktokId {
    /** Nút mở Album/Thư viện tải lên trên màn hình quay */
    const val UPLOAD_BUTTON = "com.ss.android.ugc.trill:id/ckt"

    /** ID của ảnh đại diện người dùng */
    const val USER_AVATAR  = "com.ss.android.ugc.trill:id/user_avatar"

    /** ID của thanh nhập từ khóa tìm kiếm */
    const val SEARCH_BAR   = "com.ss.android.ugc.trill:id/gz8"

    /**
     * ViewPager của trang **Search Result**
     */
    const val SEARCH_PAGER = "com.ss.android.ugc.trill:id/viewpager_search"

    /** Vùng hiển thị mô tả (caption/hashtags) của video */
    const val VIDEO_DESC = "com.ss.android.ugc.trill:id/desc"

    /** Nút Thích (Like) video */
    const val LIKE_BUTTON = "com.ss.android.ugc.trill:id/fhc"

    /** Nút Yêu thích (Favorite) video */
    const val FAVORITE_BUTTON = "com.ss.android.ugc.trill:id/h_9"

    /** Nút mở khung bình luận */
    const val COMMENT_BUTTON = "com.ss.android.ugc.trill:id/e0m"

    /** List chứa các bình luận */
    const val COMMENT_LIST = "com.ss.android.ugc.trill:id/sai"

    /** View hiển thị Avatar khi comment*/
    const val COMMENT_AVATAR = "com.ss.android.ugc.trill:id/dz3"

    /** Nút trả lời bình luận*/
    const val REPPLY_COMMENT_BUTTON = "com.ss.android.ugc.trill:id/e2i"

    /** Danh sách các icon đề xuất*/
    const val ICON_LIST = "com.ss.android.ugc.trill:id/h3d"

    /** Popup nhập comment*/
    const val COMMENT_INPUT_POPUP = "com.ss.android.ugc.trill:id/h3h"

    /** Ô nhập nội dung bình luận */
    const val COMMENT_INPUT = "com.ss.android.ugc.trill:id/e02"

    /** Nút Chia sẻ/Xem thêm tùy chọn (Share/More) */
    const val SHARE_BUTTON = "com.ss.android.ugc.trill:id/ubv"

    /** Nút thực hiện Tìm kiếm cạnh thanh nhập từ khóa */
    const val SEARCH_BUTTON = "com.ss.android.ugc.trill:id/trq"

    /** Grid/List chứa các video kết quả tìm kiếm */
    const val SEARCH_RESULT_LIST = "com.ss.android.ugc.trill:id/m_7"

    /** View hiển thị nút/văn bản Thêm âm thanh trong màn hình edit video */
    const val ADD_SOUND_TEXT = "com.ss.android.ugc.trill:id/zo6"

    /** Nút Tiếp tục trong quy trình đăng video/chọn nhạc */
    const val NEXT_BUTTON = "com.ss.android.ugc.trill:id/ond"

    /** Danh sách các bài nhạc gợi ý */
    const val MUSIC_LIST = "com.ss.android.ugc.trill:id/t96"

    /** Ô nhập caption cho video chuẩn bị đăng */
    const val CAPTION_INPUT = "com.ss.android.ugc.trill:id/gfw"

    /** View hiển thị tên người dùng (username) trong danh sách tìm kiếm */
    const val SEARCH_USERNAME = "com.ss.android.ugc.trill:id/yi8"

    /** Lưới danh sách video trên trang cá nhân (Profile) */
    const val PROFILE_VIDEO_GRID = "com.ss.android.ugc.trill:id/hdm"

    /** Item video đơn lẻ trong lưới danh sách video cá nhân */
    const val PROFILE_VIDEO_ITEM = "com.ss.android.ugc.trill:id/z9y"

    /** Danh sách các tùy chọn khi nhấn nút Chia sẻ (gồm nút Xóa, v.v.) */
    const val SHARE_OPTIONS_LIST = "com.ss.android.ugc.trill:id/vv"

    /** Nút Xóa và Đăng lại trong dialog/bottom sheet */
    const val DELETE_AND_REPOST_BUTTON = "com.ss.android.ugc.trill:id/sbo"

    /** Nút Xóa trong popup hỏi ý kiến trước khi xóa/đăng lại */
    const val DELETE_IN_REPOST_POPUP = "com.ss.android.ugc.trill:id/f9z"

    /** Nút xác nhận Xóa cuối cùng trong hộp thoại xóa video */
    const val CONFIRM_DELETE_BUTTON = "com.ss.android.ugc.trill:id/wk"
}

object TiktokDesc {
    /** Nút gửi bình luận (Post comment button) */
    const val POST_COMMENT_BUTTON = "@2131953937"
}
