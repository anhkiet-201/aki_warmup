package com.aki.akiwarmup.random

import java.util.Locale
import kotlin.random.Random

// Dữ liệu Keyword Pool sạch (được làm mới toàn bộ hướng tới vai trò Nữ và không chứa vị trí cụ thể)
val keywordPool = mapOf(
    "greetings" to listOf(
        "Anh ơi", "Chị ơi", "Ad ơi", "Shop ơi", "Sếp ơi", "Cả nhà ơi",
        "Mọi người ơi", "Bên mình ơi", "Bác ơi", "Chủ thớt ơi", "Chủ kênh ơi",
        "Chị admin ơi", "Anh quản lý ơi", "Dạ anh chị ơi", "Cho em hỏi tí ạ", "Anh tuyển dụng ơi",
        "Chị tuyển dụng ơi", "Dạ sếp ơi", "Dạ cả nhà mình ơi", "Mọi người cho em hỏi", "Admin thân mến",
        "Công ty mình ơi", "Anh chị tuyển dụng ơi", "Bên mình cho em hỏi", "Ad cho em hỏi chút", "Chị ơi cho em hỏi",
        "Anh ơi cho em hỏi", "Sếp tuyển dụng ơi", "Dạ ad ơi", "Dạ chị ơi", "Dạ anh ơi",
        "Mọi người cho em hỏi chút", "Cả nhà cho em hỏi", "Bên mình còn ai trực không ạ", "Cho em hỏi thăm tí ạ", "Anh chị ơi",
        "Admin ơi", "Dạ mọi người ơi", "Chủ kênh cho em hỏi", "Sếp ơi cho em hỏi", "Anh chị phòng nhân sự ơi"
    ),
    "job_target" to listOf(
        "công việc này", "việc thời vụ", "việc y như video", "việc bên mình",
        "công việc", "việc này",
        "việc làm chính thức", "việc lắp ráp linh kiện", "việc đóng gói bánh kẹo", "công việc dán nhãn", "việc kiểm hàng",
        "công việc đứng máy", "công việc hành chính", "việc làm chuyên ca ngày", "việc làm chuyên ca đêm",
        "công việc phụ kho", "việc phân loại hàng hóa", "việc may mặc thời vụ", "công việc đóng hộp", "việc làm bưu cục",
        "công việc giao nhận", "việc làm nhẹ nhàng này", "công việc trong phòng máy lạnh", "việc ngồi làm ghế tựa", "việc dán tem nhãn",
        "công việc xếp hộp giấy", "việc phụ xưởng đóng gói", "công việc tại xưởng may", "việc lắp ráp tai nghe", "việc sản xuất nhựa",
        "công việc đóng gói bao bì", "việc làm thời vụ cuối năm", "công việc theo ca", "việc làm phổ thông", "công việc tại nhà máy"
    ),
    "candidate_status_young" to listOf(
        "sinh năm 2k4", "mới ra trường", "muốn làm thêm dịp hè", "chưa có kinh nghiệm gì",
        "sinh năm 2005", "18 tuổi", "mới nghỉ công ty cũ", "đang rảnh",
        "sinh năm 2k6", "sinh năm 2k7", "vừa thi tốt nghiệp xong", "muốn tìm việc làm trước tết", "đang chờ lấy bằng tốt nghiệp",
        "muốn đi làm kiếm tiền đóng học phí", "sinh năm 2004 mới lên Bình Dương", "chưa có kinh nghiệm đứng xưởng", "mới 19 tuổi", "sinh năm 2006 muốn đi làm ngay",
        "mong muốn học hỏi việc nhanh", "sức trẻ năng động chịu khó", "mới xuất ngũ về quê muốn đi làm", "vừa nghỉ bên xưởng linh kiện", "chưa từng đi làm công ty bao giờ",
        "muốn làm thời vụ 2 3 tháng", "mới tốt nghiệp cấp 3", "sinh năm 2005 khoẻ mạnh chịu khó", "muốn tìm việc quanh khu Thuận An", "mới từ quê lên muốn tìm việc ngay",
        "học sinh muốn đi làm thêm", "sinh năm 2k5 chưa vướng bận gia đình", "nhanh nhẹn tiếp thu nhanh", "muốn đi làm để tự lập tài chính", "sinh năm 2k6 có căn cước công dân",
        "muốn xin làm ca đêm lâu dài", "đang rảnh toàn thời gian", "muốn tìm việc làm xoay ca", "mới nghỉ bên cty cũ được 1 tuần", "mong muốn có công việc ổn định"
    ),
    "candidate_status_adult" to listOf(
        "ngoài 30 tuổi", "ngoài 40 tuổi", "đã có gia đình", "có con nhỏ",
        "mẹ bỉm sữa", "mới ở quê lên", "từng làm công nhân may", "từng làm giày da",
        "sức khoẻ tốt", "muốn tìm việc làm thêm",
        "sinh năm 1990 có kinh nghiệm", "sinh năm 1988 sức khoẻ dẻo dai", "đã có kinh nghiệm làm tổ trưởng", "từng làm đóng gói 5 năm", "đã có gia đình ở trọ gần đây",
        "muốn tìm công việc ổn định lâu dài", "hai vợ chồng cùng muốn xin việc", "từng làm kiểm hàng điện tử", "đã có kinh nghiệm đứng máy dập", "sinh năm 1992 chịu khó tăng ca",
        "đã quen làm việc ca đêm", "từng làm ở KCN Sóng Thần", "muốn tìm việc không yêu cầu bằng cấp cao", "ngoài 35 tuổi chăm chỉ chịu khó", "từng làm công nhân may mặc 3 năm",
        "có thể đi làm ngay cả ngày lễ", "từng làm công việc đóng gói kho bãi", "muốn làm chuyên ca ngày để đưa đón con", "chịu được áp lực công việc cao", "đã làm qua nhiều xưởng cơ khí",
        "ở trọ gần khu công nghiệp Vsip", "muốn gắn bó lâu dài với công ty", "đã quen việc đứng làm việc liên tục", "ngoài 45 tuổi nhưng còn rất khoẻ", "có kinh nghiệm vận hành máy đóng gói",
        "từng làm tại xưởng giày da Bình Dương", "vợ chồng trẻ muốn làm chung ca", "từng làm công việc phân loại hàng hoá", "tác phong nhanh nhẹn ngăn nắp", "mong muốn tìm nơi có bảo hiểm đầy đủ"
    ),
    "ask_vacancy" to listOf(
        "còn tuyển không", "còn nhận người không", "còn slot không", "còn nhận hồ sơ không",
        "còn tuyển thời vụ không", "còn nhận nữ không", "có nhận nữ không", "còn slot cho nữ không",
        "nữ còn nhận không",
        "công ty còn nhận đăng ký làm việc không", "còn chỉ tiêu cho lao động phổ thông không", "bên mình còn tuyển ca ngày không ad", "còn nhận hồ sơ trực tiếp tại cổng không sếp", "còn slot làm thời vụ đóng gói không",
        "còn nhận công nhân nữ lắp ráp không", "bên mình còn nhận người đi làm ngay không", "còn tuyển vị trí đóng gói sản phẩm không shop", "còn nhận hồ sơ phỏng vấn ngày mai không", "còn tuyển nam nữ làm thời vụ không",
        "bên mình còn chỉ tiêu tuyển dụng không sếp", "còn tuyển công việc đứng máy không ạ", "đợt này còn nhận công nhân mới không", "còn tuyển thời vụ xoay ca không ad", "còn slot cho nữ làm ca hành chính không",
        "công ty còn nhận người chưa có kinh nghiệm không", "bên mình còn nhận đăng ký làm ca đêm không", "còn tuyển người làm khu vực Thuận An không", "còn tuyển thời vụ 3 tháng không sếp", "còn nhận hồ sơ photo không ad",
        "bên mình còn tuyển công nhân dán nhãn không", "còn tuyển lao động nữ trên 30 tuổi không shop", "còn nhận đăng ký phỏng vấn tuần này không", "còn slot làm việc trong phòng máy lạnh không", "còn tuyển phụ kho dán tem không ad",
        "còn nhận hồ sơ thời vụ không sếp", "còn tuyển công nhân chính thức không ạ", "còn tuyển đóng gói bánh kẹo không cả nhà", "công ty còn tuyển lao động phổ thông số lượng lớn không", "còn nhận đăng ký đi làm tuần tới không shop"
    ),
    "ask_salary" to listOf(
        "lương cơ bản bao nhiêu", "tổng thu nhập tháng thế nào", "tính lương theo ngày hay theo tháng",
        "có được tăng ca nhiều không", "có bao ăn ở không", "mùng mấy thì được lãnh lương",
        "có cho ứng lương không",
        "làm ca đêm lương nhân hệ số bao nhiêu vậy", "tiền phụ cấp chuyên cần là bao nhiêu ạ", "tăng ca chủ nhật tính lương thế nào sếp", "phụ cấp đi lại với nhà trọ bao nhiêu tiền", "có được thưởng năng suất hàng tháng không",
        "lương thời vụ nhận theo tuần hay theo tháng", "làm ca đêm có được phụ cấp tiền cơm không", "lương cơ bản có đóng bảo hiểm xã hội không", "phụ cấp độc hại hoặc nặng nhọc tính thế nào", "có thưởng lương tháng 13 không ạ",
        "lương thử việc được tính bao nhiêu phần trăm", "tiền phụ cấp xăng xe hàng tháng không shop", "tổng thu nhập cả tăng ca được khoảng bao nhiêu", "có hỗ trợ ứng lương giữa tháng không ad", "tiền thưởng chuyên cần có tính vào lương cơ bản không",
        "tăng ca ngày thường được tính nhân mấy ạ", "lương ca ngày với ca đêm chênh lệch thế nào sếp", "có phụ cấp tiền nhà trọ cho người ở xa không", "tiền cơm trưa công ty hỗ trợ bao nhiêu một suất", "có hỗ trợ tiền xe cho công nhân về quê ăn tết không",
        "làm thời vụ có được hưởng các khoản phụ cấp không", "lãnh lương qua thẻ của ngân hàng nào vậy ad", "thử việc trong bao lâu thì được nhận đủ lương", "có được thưởng chuyên cần khi làm đủ ngày công không", "có phụ cấp nuôi con nhỏ dưới 6 tuổi không ạ",
        "tăng ca có bắt buộc không hay tự nguyện vậy shop", "có được nhận tiền mặt hay chuyển khoản ngân hàng", "lương thời vụ có bị trừ khoản phí nào không", "ngày lễ tết đi làm lương nhân hệ số mấy sếp", "có trợ cấp tiền gửi trẻ cho công nhân không"
    ),
    "ask_requirements" to listOf(
        "không có bằng cấp có làm được không", "tuổi cao có nhận không",
        "cận thị có làm được không", "hồ sơ thiếu bổ sung sau được không", "chưa kinh nghiệm có đào tạo không",
        "nhuộm tóc có nhận không", "thử việc có lương không",
        "chỉ có căn cước công dân gốc có nhận việc trước không", "chưa có giấy khám sức khoẻ có đi phỏng vấn được không", "hồ sơ thời vụ cần những giấy tờ gì ạ", "nữ trên 35 tuổi bên mình có nhận không sếp", "chưa có thẻ ngân hàng công ty có hỗ trợ làm giúp không",
        "nhuộm tóc màu trầm có được chấp nhận không ad", "hồ sơ photo chưa công chứng có nhận việc được không", "có yêu cầu chiều cao hay cân nặng gì không shop", "bên mình có nhận lao động ngoài tỉnh không ạ", "chưa có bằng tốt nghiệp cấp 2 có làm được không sếp",
        "đeo kính cận có làm bên khâu lắp ráp được không", "có cần giấy xác nhận cư trú không cả nhà", "chỉ có căn cước photo công chứng có được nhận không", "có nhận người có hình xăm nhỏ trên tay không ad", "không biết chữ có làm việc dán nhãn được không",
        "có bắt buộc phải nộp bằng cấp gốc không shop", "chưa có kinh nghiệm dán tem có được hướng dẫn không", "có nhận người làm thời vụ 1 tháng không sếp", "cần mang theo những gì khi đi phỏng vấn vậy ad", "hồ sơ xin việc có cần giấy hạnh kiểm không",
        "phỏng vấn có yêu cầu mặc trang phục lịch sự thế nào", "chưa có tài khoản ngân hàng có nhận lương tiền mặt không", "giấy khám sức khoẻ của bệnh viện huyện có được chấp nhận không", "có nhận công nhân nam xăm mình ở cánh tay không shop", "có giới hạn độ tuổi tối đa khi tuyển dụng không ad",
        "hồ sơ công chứng quá hạn 6 tháng có dùng được không", "có nhận người làm ca gãy hoặc ca hành chính không sếp", "chưa có kinh nghiệm đứng máy có được nhận việc không", "có yêu cầu giấy tờ gốc để đối chiếu khi nhận hồ sơ không", "có cần giấy khám sức khoẻ theo thông tư 32 không ạ"
    ),
    "actions_apply" to listOf(
        "xin địa chỉ qua làm với", "cho xin số điện thoại liên hệ", "tư vấn giúp em",
        "inbox em nha", "rep tin nhắn em với", "mai em qua nộp hồ sơ luôn nhé",
        "cho em đăng ký với", "gọi cho em số này nhé",
        "inbox hướng dẫn em ứng tuyển với ạ", "cho em xin zalo của nhân sự liên hệ cho tiện", "gửi em xin định vị địa chỉ công ty mình với", "cho em xin lịch phỏng vấn ngày mai nha sếp", "inbox chỉ đường cho em đi phỏng vấn với ad",
        "rep tin nhắn tư vấn công việc giúp em nha", "cho em đăng ký làm ca đêm với ạ", "cho em xin số zalo để nộp hồ sơ trước", "inbox tư vấn mức lương và thời gian làm việc giúp em", "cho em xin thông tin liên hệ trực tiếp của nhân sự",
        "đăng ký phỏng vấn thì liên hệ qua đâu vậy shop", "rep tin nhắn và tư vấn hồ sơ giúp em nhé", "cho em đăng ký làm thời vụ dán tem với", "inbox gửi em danh sách hồ sơ cần chuẩn bị nha", "cho em xin số điện thoại để gọi trao đổi công việc",
        "gửi em xin định vị xưởng đóng gói với sếp", "tư vấn cho em qua tin nhắn chờ với ad", "cho em đăng ký 2 slot nam nữ làm thời vụ", "inbox em thông tin cụ thể ca làm việc nha", "rep inbox tư vấn công việc dán nhãn giúp em",
        "cho em xin số zalo ad để gửi thông tin ứng tuyển", "cho em đăng ký phỏng vấn vào sáng mai nhé", "gọi lại tư vấn giúp em qua số này nha", "inbox hướng dẫn thủ tục nhận việc cho em với", "cho em đăng ký làm ca hành chính nha shop",
        "gửi định vị xưởng qua tin nhắn giúp em với sếp", "rep em nhắn tin nộp hồ sơ với ad", "tư vấn công việc đóng gói bánh kẹo giúp em nha", "cho em xin lịch hẹn phỏng vấn tuần này", "inbox tư vấn công việc chi tiết giúp em nhé"
    ),
    "praises" to listOf(
        "chỗ làm có vẻ mát mẻ", "bao ăn ở là thấy ngon rồi", "xưởng to sạch sẽ ghê",
        "quản lý thân thiện lắm", "thấy môi trường tốt ghê", "công việc thấy cũng ổn",
        "xưởng rộng rãi thông thoáng ghê", "chế độ công ty nghe rất tốt", "công việc nhìn chuyên nghiệp quá", "thấy quản lý vui vẻ hòa đồng ghê", "môi trường làm việc nhìn sạch sẽ phết",
        "phụ cấp với lương thưởng rõ ràng ghê", "chỗ làm mát mẻ sạch sẽ thích quá", "cơm ca nhìn ngon và nhiều món ghê", "cty quan tâm đời sống công nhân ghê", "xưởng sản xuất có máy lạnh mát mẻ quá",
        "tiền lương chuyên cần cao thích ghê", "môi trường làm việc năng động và sạch sẽ", "nghe chế độ thưởng chuyên cần rất tốt", "quản lý chỉ bảo nhiệt tình thích thật", "công việc đóng gói này nhẹ nhàng ghê",
        "chế độ bao ăn trưa quá là tốt luôn", "xưởng lắp ráp linh kiện nhìn hiện đại quá", "phụ cấp xăng xe điện thoại rất thực tế", "môi trường làm việc thân thiện ấm cúng", "cơm trưa công ty chuẩn bị đầy đủ quá",
        "chỗ gửi xe rộng rãi thoải mái ghê", "công ty quy mô lớn nhìn chuyên nghiệp quá", "nghe bảo công ty trả lương rất đúng hẹn", "môi trường làm việc không áp lực thích ghê", "được cấp phát đồng phục miễn phí là ưng rồi",
        "xưởng dán tem sạch sẽ và ngăn nắp quá", "các anh chị tổ trưởng chỉ dẫn nhiệt tình ghê", "chế độ bảo hiểm và ngày phép đầy đủ quá", "xưởng mát mẻ có cả nước uống đầy đủ", "nghe chế độ đãi ngộ công nhân rất chu đáo"
    ),
    "skeptical_questions" to listOf(
        "có mất phí môi giới không", "có bắt mua đồng phục không", "có cần đóng cọc gì không",
        "có hỗ trợ tìm phòng trọ gần công ty không", "có phụ cấp tiền nhà trọ hàng tháng không", "làm thời vụ có được bao cơm giữa ca không", "công ty có xe đưa đón công nhân không", "phỏng vấn ở văn phòng hay xuống trực tiếp xưởng vậy ad",
        "có bắt buộc phải nộp bằng gốc không", "hồ sơ chưa công chứng có cho nhận việc trước không", "làm ca đêm có được phụ cấp tiền cơm thêm không", "làm thời vụ thì bao lâu được ký hợp đồng", "có hỗ trợ ứng lương giữa tháng không ad",
        "đóng bảo hiểm xã hội từ tháng thứ mấy thế ad", "đăng ký xe đưa đón ở đâu vậy ad", "có bắt đóng tiền làm thẻ từ gửi xe không", "làm ca đêm có được nghỉ giữa ca 45 phút không", "có cần mang theo bút hay hồ sơ gì khi đi phỏng vấn không",
        "phỏng vấn xong có được biết kết quả luôn không ad", "thời vụ có tiền thưởng chuyên cần không shop", "làm xưởng này có mát mẻ, có máy lạnh không sếp", "có yêu cầu mặc quần tây áo sơ mi đi phỏng vấn không", "chưa có thẻ ngân hàng công ty có hỗ trợ làm giúp không",
        "chỉ có căn cước công dân có đi phỏng vấn trước được không", "lịch phỏng vấn là vào các ngày nào trong tuần thế ad", "có hỗ trợ tiền xe cho người ở quê lên không", "làm thời vụ có được thưởng lễ tết không", "có tủ đồ cá nhân khóa riêng cho công nhân không sếp",
        "vào làm có được cấp phát 2 bộ đồng phục miễn phí không", "có cần giấy xác nhận cư trú hay hộ khẩu không ad", "bên mình có nhận lao động ngoài tỉnh không shop", "thử việc mấy ngày thì được tính lương thế sếp", "có tổ chức khám sức khỏe định kỳ cho công nhân không"
    ),
    "location" to listOf(
        "Bình Dương", "KCN VSIP", "KCN Mỹ Phước", " Vsip 2A", "Nam tân uyên", "ST 3", "sóng thần 3", "vsip 2a", "Tân uyên", "Đồng an 2", "vĩnh tân", "Bến cát",
        "KCN VSIP 2",
        "Thủ Dầu Một", "Bến Cát"
    ),
    "wishes" to listOf(
        "chúc shop mau tuyển đủ người", "chúc công ty ngày càng phát triển", "chúc sớm tìm được nhân viên",
        "chúc sếp mau kiếm được lính", "chúc kênh ngày càng phát triển",
        "chúc cty làm ăn phát tài", "chúc shop ngày càng đắt khách", "chúc công ty mình luôn phát triển mạnh mẽ", "chúc sếp có một ngày làm việc vui vẻ", "chúc kênh tuyển dụng được nhiều nhân sự tốt",
        "chúc công ty luôn ngập tràn đơn hàng", "chúc cả nhà mình ngày mới nhiều niềm vui", "chúc sếp luôn nhiều sức khoẻ để dẫn dắt công ty", "chúc công ty ngày càng mở rộng quy mô", "chúc cho toàn thể công nhân cty nhiều sức khoẻ",
        "chúc anh chị nhân sự tuần mới nhiều năng lượng", "chúc xưởng mình luôn hoạt động suôn sẻ", "chúc công việc của sếp luôn thuận lợi", "chúc cty ngày càng tuyển được nhiều nhân tài", "chúc doanh nghiệp mình vươn xa hơn nữa",
        "chúc mọi người trong cty luôn đoàn kết", "chúc admin luôn xinh đẹp và nhiều năng lượng", "chúc công việc tuyển dụng luôn thuận buồm xuôi gió", "chúc cho xưởng đóng gói ngày càng đắt hàng", "chúc công ty gặt hái được nhiều thành công",
        "chúc cho các bạn công nhân thời vụ làm việc vui vẻ", "chúc cho mọi người luôn bình an và may mắn", "chúc doanh nghiệp mình phát lộc phát tài", "chúc kênh của sếp sớm đạt triệu follow", "chúc toàn thể công ty một năm hồng phát",
        "chúc mọi người luôn có nhiều sức khỏe", "chúc công việc kinh doanh luôn phát đạt", "chúc công ty luôn dẫn đầu trong ngành", "chúc cả nhà luôn tràn đầy năng lượng tích cực", "chúc công ty mình ngày càng thịnh vượng"
    ),
    "interaction_bait" to listOf(
        "tương tác chéo nha cả nhà", "đẩy bài giúp ad", "thả tim chéo uy tín",
        "trả tương tác giúp em", "lên xu hướng nào", "chúc ngày mới năng lượng",
        "tim chéo uy tín nha mọi người", "tương tác đẩy bài cùng phát triển", "cho em xin 1 follow nha cả nhà", "thả tim cùng đẩy lên xu hướng nào", "tương tác cùng nhau phát triển nhé",
        "trả tim uy tín 100% nha mọi người", "follow chéo cùng lên xu hướng nha", "chúc cả nhà ngày mới ngập tràn niềm vui", "tương tác nhiệt tình trả đủ nha", "thả tim ủng hộ kênh tuyển dụng nha",
        "chúc video của sếp sớm đạt triệu view", "tương tác chéo cùng nhau tiến bộ", "thả tim giúp em để em có động lực", "tương tác chéo uy tín chất lượng nha", "chúc kênh ngày càng có nhiều tương tác",
        "đẩy tương tác nhiệt tình nha cả nhà", "thả tim dạo trả đủ nha mọi người", "chúc mọi người ngày làm việc hiệu quả", "tương tác chéo đẩy bài viết lên top", "thả tim và follow chéo cùng phát triển",
        "chúc cả nhà một ngày nhiều may mắn", "tương tác nhiệt tình đẩy xu hướng nha", "follow chéo nhận tương tác ngay nha", "thả tim chéo chất lượng cao nha cả nhà", "chúc ngày mới may mắn và thành công",
        "tương tác đẩy bài giúp kênh bay xa", "thả tim tương tác nhận lại ngay nha", "chúc cả nhà ngày mới làm việc năng suất", "tương tác cùng nhau lên xu hướng nhé", "thả tim và lưu video ủng hộ nha shop"
    ),
    "fillers" to listOf(
        "nha", "ạ", "nhé", "với ạ", "được không", "với",
        "nhé cả nhà", "giúp em với", "nha sếp", "ạ sếp", "được không ad",
        "nhe", "nha cả nhà", "với nhé", "với nha", "giúp em nhé",
        "nha shop", "nhé shop", "ạ ad", "giúp em với ạ", "được không ạ",
        "thế sếp", "nha mọi người", "nhé mọi người", "với ad", "giúp mình với",
        "nhé ad", "nha ad", "nha chị", "nha anh", "nhé chị",
        "nhé anh", "ạ chị", "ạ anh", "giúp với ạ", "được không sếp"
    ),
    "emojis" to listOf(
        "👍", "😊", "🙏", "💪", "🔥", "💯", "😅", "👌", "🤝", "👀", "❤️",
        "✨", "🎉", "🌟", "🍀", "😃", "🥰", "😍", "🤩", "🤗", "🥳",
        "🌻", "🌸", "🎈", "🌞", "🌈", "🙌", "👏", "✌️", "⭐", "💝",
        "💖", "💘", "💗", "💓", "💛", "💚", "💙", "💜", "🧡", "☘️"
    )
)

// Các mẫu câu templates thiết kế chặt chẽ
val templates = listOf(
    // Nhóm hỏi còn tuyển không
    "{greetings} còn tuyển không {fillers}",
    "Còn tuyển không {greetings}",
    "Còn nhận người không {greetings}",
    "Cho em hỏi {job_target} còn tuyển không {fillers}",
    "Inbox mình thông tin {job_target} nhé {greetings}",
    "{greetings} còn nhận hồ sơ {job_target} nữa không {fillers}",
    "Bên mình còn slot làm {job_target} không {greetings} ơi",
    "Thời điểm này còn nhận người nữa không {greetings}",
    "Nữ {candidate_status_young} có nhận làm {job_target} không ạ",
    "Còn tuyển lao động thời vụ không {greetings}",
    "Cho em xin thông tin tuyển dụng {job_target} với {greetings}",

    // Nhóm hỏi chi tiết lương & chế độ
    "{greetings} cho em hỏi {ask_salary} {fillers}",
    "Làm {job_target} thì {ask_salary} {fillers}",
    "Lương lậu của {job_target} thế nào vậy {greetings}",
    "Có bao ăn ở hay phụ cấp gì không {greetings}",
    "Làm {job_target} có tăng ca nhiều không {greetings}",
    "Lương cơ bản của {job_target} là bao nhiêu vậy ạ",
    "Tháng đầu tiên có được ứng lương không {greetings}",
    "Bên mình đóng bảo hiểm sau bao lâu thế {greetings}",
    "{job_target} này tính theo sản phẩm hay thời gian thế ạ",
    "Tổng thu nhập trung bình một tháng được bao nhiêu vậy shop",

    // Nhóm hỏi yêu cầu & độ tuổi
    "Em {candidate_status_young}, {ask_requirements} {fillers}",
    "Tầm {candidate_status_adult}, {ask_requirements} {fillers}",
    "{greetings} ơi em {candidate_status_young} có nhận không {fillers}",
    "Không biết xăm mình hay cận thị {ask_requirements} {greetings}",
    "Em có con nhỏ, {ask_requirements} {fillers}",
    "Không có bằng cấp 2 {ask_requirements} {greetings}",
    "Chưa có kinh nghiệm gì, {ask_requirements} {fillers}",
    "Em cận nhẹ {ask_requirements} {greetings}",
    "Hồ sơ cần công chứng không {greetings}",
    "Nhuộm tóc sáng màu {ask_requirements} {greetings}",

    // Nhóm muốn xin đi làm ngay
    "Em muốn đăng ký làm {job_target}, {actions_apply}",
    "{greetings} check inbox {actions_apply}",
    "Đang cần việc gấp ở {location}, {actions_apply}",
    "Cho em {actions_apply} để trao đổi trực tiếp",
    "Em {candidate_status_young} muốn xin làm luôn, {actions_apply}",
    "Mình ở {location}, cần tìm việc làm ngay, {actions_apply}",
    "Có gì {greetings} liên hệ mình số này nha, {actions_apply}",
    "Mai em qua nộp hồ sơ được không ạ, {actions_apply}",
    "Cho em xin thông tin để mai qua nhận việc luôn {greetings}",

    // Nhóm địa điểm
    "{job_target} làm ở {location} đúng không {greetings}",
    "Khu vực {location} còn tuyển {job_target} không {fillers}",
    "Có xe đưa đón từ khu vực khác đến {location} không",
    "{job_target} này làm ở chi nhánh {location} hay đâu thế {greetings}",
    "Em ở gần {location}, {job_target} này làm gần đó không",
    "Có chỗ ở lại cho người từ xa đến làm ở {location} không ạ",
    "Ở {location} có hỗ trợ tìm phòng trọ không shop",

    // Nhóm khen ngợi & chúc mừng
    "{praises}. {wishes}",
    "Thấy {praises} quá. {wishes} nha shop",
    "Việc tốt quá. {wishes} {emojis}",
    "Môi trường có vẻ tốt. {wishes} {emojis}",
    "Nghe review bên mình rất ok. {wishes} ạ",
    "{praises}. Mong cty ngày càng phát triển {emojis}",
    "Chúc shop buôn may bán đắt và mau tuyển đủ người {emojis}",

    // Nhóm nghi ngờ / cảnh giác
    "Cho em hỏi thật là {skeptical_questions} {fillers}",
    "Xin việc bên mình {skeptical_questions}",
    "Làm {job_target} có chắc là {skeptical_questions} không",
    "{greetings} cho em hỏi có mất phí môi giới hay giữ cccd không {fillers}",
    "Vào làm có bắt đóng tiền đồng phục gì không thế {greetings}",
    "Nghe bảo {skeptical_questions}, có thật không ạ",
    "Chỉ sợ {skeptical_questions} thui chứ đi làm ngại gì cực",

    // Nhóm bình luận tương tác
    "{interaction_bait} {emojis}",
    "Chấm hóng {job_target}. {interaction_bait}",
    "Ủng hộ kênh. {wishes}",
    "Thả tim chéo nha shop. {interaction_bait}",
    "Chúc ngày mới may mắn nhé cả nhà. {interaction_bait}",

    // Nhóm ngắn gọn / Chỉ dùng Emoji
    ".",
    "Quan tâm",
    "Chấm",
    "Hóng",
    "{emojis}",
    "{emojis} {emojis}",
    "{emojis} {emojis} {emojis}",

    // Nhóm hỏi còn tuyển hay không & hình thức (Chính thức/Thời vụ) (15 templates)
    "Bên cty còn nhận hso làm {job_target} ko {fillers}",
    "Còn nhận đăng ký {job_target} ca đêm ko {greetings} ơi",
    "{greetings} , cho hỏi {job_target} còn nhận người ko {fillers}",
    "Cho hỏi {job_target} còn tuyển lao động phổ thông ko {fillers}",
    "{job_target} này là tuyển chính thức hay thời vụ vậy {greetings}",
    "Thời vụ với chính thức bên cty chênh lệch lương thế nào {fillers}",
    "Cty có nhận thêm người làm {job_target} nữa ko {greetings}",
    "{candidate_status_young} muốn xin làm {job_target} còn tuyển ko ạ",
    "Còn tuyển thời vụ đi làm ở {location} ko {greetings} ơi",
    "Cho hỏi còn tuyển công nhân làm {job_target} ko ạ",
    "Bên cty còn slot {job_target} nào đi làm ngay đc ko sếp",
    "Đợt này còn nhận hso thời vụ 3 tháng ko {greetings}",
    "{greetings} ơi công việc {job_target} này còn nhận đăng ký ko",
    "Mọi người cho hỏi {job_target} còn nhận hso photo tại cổng ko ạ",
    "Còn tuyển công nhân chính thức làm {job_target} ko sếp",

    // Nhóm hỏi chi tiết về lương, phụ cấp, cơm ca, đứng/ngồi (20 templates)
    "Làm {job_target} này {ask_salary} {emojis}",
    "Cho hỏi lương ca đêm của {job_target} tính thế nào {fillers}",
    "Chế độ phụ cấp xăng xe nhà trọ của {job_target} thế nào {greetings}",
    "Tăng ca ngày chủ nhật bên cty tính lương thế nào {fillers}",
    "Làm {job_target} có đc phụ cấp tiền cơm trưa ko {greetings}",
    "Tiền cơm ca bên cty bao ăn hay tự túc vậy {greetings}",
    "Làm thời vụ {job_target} cơm nước tự túc hay cty lo {fillers}",
    "Làm {job_target} này là đứng hay ngồi làm thế sếp",
    "Có đc ngồi làm ko hay bắt buộc đứng suốt ca vậy ad",
    "Lương chuyên cần của {job_target} là bao nhiêu vậy ạ",
    "Bên cty mùng mấy hằng tháng thì chuyển khoản lương vậy ad",
    "Làm {job_target} có đc hỗ trợ tiền trọ ko sếp",
    "Lương thử việc của {job_target} tính bao nhiêu phần trăm lương chính thế ạ",
    "Làm {job_target} ca đêm có phụ cấp ca ko {greetings}",
    "Cho hỏi phụ cấp độc hại của {job_target} là bao nhiêu {fillers}",
    "Cty trả lương qua thẻ ngân hàng nào vậy {greetings}",
    "Lương thời vụ có được nhận theo tuần ko {greetings} ơi",
    "Cho hỏi {job_target} này có thưởng năng suất hàng tháng ko ạ",
    "Tổng thu nhập bình quân cả tăng ca của {job_target} khoảng bao nhiêu ạ",
    "Chế độ bhxh và phép năm của {job_target} tính thế nào vậy shop",

    // Nhóm hỏi về hồ sơ photo, chưa đủ tuổi, hình xăm, tomboy (20 templates)
    "{candidate_status_young}, chưa có kinh nghiệm {ask_requirements} {fillers}",
    "{candidate_status_adult}, có con nhỏ {ask_requirements} {fillers}",
    "Chỉ có cccd photo công chứng {ask_requirements} nhận việc ko shop",
    "Chưa có cccd gốc, chỉ có cccd photo {ask_requirements} ko sếp",
    "Chưa đủ tuổi, sinh năm 2k9 {ask_requirements} làm thời vụ ko sếp",
    "Chưa đủ 18 tuổi {ask_requirements} nhận ko {greetings} ơi",
    "Người có xăm mình nhiều {ask_requirements} ko sếp",
    "Xăm ở cổ tay {ask_requirements} nhận làm ko ad",
    "Bên cty có nhận tomboy làm {job_target} ko {greetings}",
    "Tomboy cắt tóc ngắn {ask_requirements} tuyển ko shop",
    "Ko có bằng cấp 3 {ask_requirements} đối với {job_target} {greetings}",
    "Chưa có kinh nghiệm gì {ask_requirements} ko ad",
    "Người ngoài tỉnh mới lên Bình Dương {ask_requirements} ko ạ",
    "Độ tuổi tối đa để nhận làm {job_target} là bao nhiêu vậy {greetings}",
    "Hso xin việc {job_target} cần những giấy tờ công chứng gì ạ",
    "Hso photo chưa công chứng {ask_requirements} nhận việc trước ko shop",
    "Mắt cận có đeo kính {ask_requirements} làm việc đc ko ad",
    "Chưa có thẻ ngân hàng {ask_requirements} cty làm giúp ko {greetings}",
    "Phỏng vấn {job_target} xong là có kết quả nhận việc luôn ko sếp",
    "Có nhận người làm thời vụ ngắn hạn 1 tháng ko {greetings}",
    "Hso công chứng bị quá hạn 6 tháng {ask_requirements} ko ạ",

    // Nhóm muốn xin đi làm ngay, liên hệ nhân sự (15 templates)
    "Muốn đăng ký phỏng vấn làm {job_target} ngay ngày mai, {actions_apply}",
    "Có slot {job_target} nào đi làm ngay được ở {location} ko, {actions_apply}",
    "{candidate_status_young} cần việc làm ngay ở {location}, {actions_apply}",
    "Cho đăng ký 2 slot nam nữ làm thời vụ {job_target}, {actions_apply}",
    "Đang rảnh cả ngày, muốn xin làm {job_target} ngay, {actions_apply}",
    "{greetings} check tin nhắn hỗ trợ nộp hso {job_target} với nhé",
    "Cho xin thông tin liên lạc của phòng nhân sự để đăng ký đi làm",
    "Cho xin số zalo liên hệ trực tiếp cho nhanh nha {greetings}",
    "Ib hướng dẫn quy trình nộp hso nhận việc {job_target} với ad",
    "Mai mang hso trực tiếp qua cổng cty phỏng vấn được ko sếp",
    "Cho xin địa chỉ chính xác của cty để qua nộp hso luôn ạ",
    "Ib giúp thời gian và địa điểm phỏng vấn cụ thể nha {greetings}",
    "Đang ở {location} cần đi làm gấp, {actions_apply} giúp với",
    "Cho xin một chấm để ib trao đổi công việc trực tiếp nhé shop",
    "Mai có lịch phỏng vấn ko sếp, {actions_apply} để chuẩn bị hso",

    // Nhóm địa điểm, Xe đưa đón, Ca kíp & Tăng ca, Mang dép (15 templates)
    "{job_target} này làm việc ở khu vực nào trong {location} thế sếp",
    "Có xe đưa đón công nhân từ các huyện khác về {location} làm việc ko ạ",
    "Làm {job_target} ở {location} có đc hỗ trợ tìm trọ gần nơi làm ko",
    "Bên cty có hỗ trợ tiền trọ hàng tháng cho công nhân ở {location} ko shop",
    "Đi làm {job_target} có bắt buộc đi giày ko hay được mang dép vậy sếp",
    "Làm việc có được mang dép quai hậu đi làm ko ad",
    "{job_target} có chia ca ngày ca đêm ko hay làm cố định sếp",
    "Có ca chuyên ngày hoặc chuyên đêm ko {greetings} ơi",
    "Bận việc gia đình ko tăng ca đc ko hay bắt buộc tăng ca thế shop",
    "Không tăng ca có bị phạt hay ảnh hưởng gì ko sếp",
    "{job_target} này tăng ca nhiều ko, tháng trung bình bao nhiêu tiếng sếp",
    "Tăng ca có đều ko hay tùy đợt hàng vậy ad",
    "Thời gian làm việc là từ mấy giờ đến mấy giờ vậy {greetings} {emojis}{emojis}{emojis}",
    "Làm {job_target} này thời gian làm việc thế nào {fillers}",
    "Ở gần {location} có nhiều trọ cho công nhân thuê ko shop",

    // Nhóm khen ngợi, chúc tốt đẹp & Nghỉ giải lao (10 templates)
    "Giữa ca có đc nghỉ giải lao giải trí ko hay làm liên tục vậy shop",
    "Ca 12 tiếng thì được nghỉ giải lao mấy lần thế sếp",
    "{praises}. Chúc cty ngày càng phát triển phát đạt {emojis}",
    "Thấy {praises} quá chừng. {wishes} nha shop {emojis}",
    "Môi trường làm việc nhìn sạch đẹp thật. {wishes} {emojis}",
    "Nghe review cơm ca và chế độ rất ok. {wishes} ạ {emojis}",
    "{praises}. Chúc cty luôn ngập tràn đơn hàng và sớm tuyển đủ người",
    "Chỗ gửi xe và xưởng rộng rãi chuyên nghiệp quá. {wishes} sếp",
    "Quản lý và tổ trưởng hướng dẫn nhiệt tình thế là thích rồi. {wishes}",
    "Môi trường làm mát mẻ sạch sẽ là cực ưng rồi. {wishes} cả nhà",

    // Nhóm hỏi đáp trung lập về quy trình nhận việc, đồng phục, cọc (5 templates)
    "Vào làm {job_target} {skeptical_questions} {fillers}",
    "Xin đi làm bên mình {skeptical_questions} {greetings} ơi",
    "Làm thời vụ {job_target} có đc cấp phát đồng phục miễn phí ko sếp",
    "{greetings} cho hỏi phỏng vấn xong {skeptical_questions} ko ạ",
    "Làm ở cty này có cần đóng tiền làm thẻ từ gửi xe ko {fillers}"
)

// Từ điển các lỗi sai phổ biến
val exactTypos = mapOf(
    "việc" to listOf("ziệc", "viêc"),
    "chỗ" to listOf("chổ"),
    "nghỉ" to listOf("nghĩ"),
    "nghĩ" to listOf("ngĩ"),
    "tuổi" to listOf("tủi", "tuỗi", "t"),
    "tuyển" to listOf("tuyễn"),
    "lương" to listOf("lươg"),
    "kinh" to listOf("kih"),
    "nghiệm" to listOf("ngiệm"),
    "hồ" to listOf("hô"),
    "sơ" to listOf("sờ"),
    "tháng" to listOf("thág"),
    "gì" to listOf("j", "zì"),
    "thì" to listOf("thỳ"),
    "quá" to listOf("wá", "qá"),
    "được" to listOf("đươc", "dc", "đc"),
    "làm" to listOf("lm", "lam", "mần"),
    "cũ" to listOf("củ"),
    "kỹ" to listOf("kỉ", "kĩ"),
    "sao" to listOf("s"),
    "rồi" to listOf("rùi", "dồi", "r", "ròi"),
    "dạ" to listOf("zạ"),
    "thời vụ" to listOf("tv", "thời vu"),
    "chính thức" to listOf("9 thức", "chíh thức", "ct"),
    "xoay ca" to listOf("x.ca", "xay ca"),
    "căn cước công dân" to listOf("cccd"),
    "tăng ca" to listOf("tca"),

)

// Các quy tắc thay thế chữ cái (dùng Regex)
val typoRules = listOf(
    Regex("t$") to "c",
    Regex("c$") to "t",
    Regex("n$") to "ng",
    Regex("ng$") to "n",
    Regex("^l") to "n",
    Regex("ả") to "ã", Regex("ã") to "ả",
    Regex("ẻ") to "ẽ", Regex("ẽ") to "ẻ",
    Regex("ỉ") to "ĩ", Regex("ĩ") to "ỉ",
    Regex("ỏ") to "õ", Regex("õ") to "ỏ",
    Regex("ủ") to "ũ", Regex("ũ") to "ủ"
)

// Hàm tạo lỗi chính tả tự nhiên
fun applyTypos(text: String, isTypoEnabled: Boolean): String {
    if (!isTypoEnabled) return text

    // Tỉ lệ câu có lỗi chính tả là 30%
    if (Random.nextDouble() > 0.30) return text

    val words = text.split(" ").toMutableList()
    if (words.isEmpty()) return text

    // Chọn ngẫu nhiên tối đa 1 hoặc 2 vị trí từ để tạo lỗi
    val typoCount = if (words.size > 5) Random.nextInt(1, 3) else 1
    val indicesToModify = words.indices.shuffled().take(typoCount)

    for (index in indicesToModify) {
        val word = words[index]
        val isFirstCharUpper = word.isNotEmpty() && word[0].isUpperCase()

        // Tách dấu câu ra khỏi từ
        val matchResult = Regex("^([a-zA-ZÀ-ỹ]+)([.,!?]*)$").find(word)

        if (matchResult != null) {
            val pureWord = matchResult.groupValues[1].lowercase()
            val punctuation = matchResult.groupValues[2]
            var newWord = pureWord

            // Kiểm tra trong từ điển lỗi kinh điển
            if (exactTypos.containsKey(pureWord)) {
                newWord = exactTypos[pureWord]!!.random()
            } else {
                // Áp dụng quy tắc Regex
                val applicableRules = typoRules.filter { it.first.containsMatchIn(pureWord) }
                if (applicableRules.isNotEmpty()) {
                    val rule = applicableRules.random()
                    newWord = pureWord.replace(rule.first, rule.second)
                }
            }

            // Khôi phục viết hoa và dấu câu
            if (isFirstCharUpper) {
                newWord = newWord.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }
            }
            words[index] = newWord + punctuation
        }
    }
    return words.joinToString(" ")
}

// Hàm tạo bình luận
fun generateComment(enableTypos: Boolean = true): String {
    var template = templates.random()

    // Sử dụng Regex để tìm các Placeholder {tên_biến} và thay thế
    val regex = Regex("\\{(\\w+)\\}")
    template = regex.replace(template) { matchResult ->
        val key = matchResult.groupValues[1]
        val pool = keywordPool[key]
        if (!pool.isNullOrEmpty()) {
            pool.random()
        } else {
            matchResult.value // Giữ nguyên {tên_biến} nếu không có dữ liệu
        }
    }

    // Tự động chuẩn hóa dấu câu ở cuối câu hỏi
    val isQuestion = template.contains("không") || template.contains("chưa") || 
            template.contains("nào") || template.contains("bao nhiêu") || 
            template.contains("sao") || template.contains("thế nào")
            
    if (isQuestion && !template.endsWith("?") && !template.endsWith(".") && !template.endsWith("!")) {
        // Tỷ lệ thêm dấu chấm hỏi là 80% đối với câu hỏi
        if (Random.nextDouble() < 0.80) {
            template += "?"
        }
    }

    // Giả lập viết thường toàn bộ (40% cơ hội)
    template = if (Random.nextDouble() < 0.40) {
        template.lowercase()
    } else {
        // Viết hoa chữ cái đầu tiên của câu
        if (template.isNotEmpty()) {
            template.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
        } else template
    }

    return applyTypos(template, enableTypos)
}