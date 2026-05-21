package com.aki.akiwarmup.random

import java.util.Locale
import kotlin.random.Random

// Dữ liệu Keyword Pool sạch (được làm mới toàn bộ hướng tới vai trò Nữ và không chứa vị trí cụ thể)
val keywordPool = mapOf(
    "greetings" to listOf(
        "Anh ơi", "Chị ơi", "Ad ơi", "Shop ơi", "Sếp ơi", "Cả nhà ơi",
        "Mọi người ơi", "Bên mình ơi", "Bác ơi", "Chủ thớt ơi", "Chủ kênh ơi"
    ),
    "job_target" to listOf(
        "công việc này", "việc thời vụ", "việc y như video", "việc bên mình",
        "công việc", "việc này"
    ),
    "candidate_status_young" to listOf(
        "sinh năm 2k4", "mới ra trường", "muốn làm thêm dịp hè", "chưa có kinh nghiệm gì",
        "sinh năm 2005", "18 tuổi", "mới nghỉ công ty cũ", "đang rảnh"
    ),
    "candidate_status_adult" to listOf(
        "ngoài 30 tuổi", "ngoài 40 tuổi", "đã có gia đình", "có con nhỏ",
        "mẹ bỉm sữa", "mới ở quê lên", "từng làm công nhân may", "từng làm giày da",
        "sức khoẻ tốt", "muốn tìm việc làm thêm"
    ),
    "ask_vacancy" to listOf(
        "còn tuyển không", "còn nhận người không", "còn slot không", "còn nhận hồ sơ không",
        "còn tuyển thời vụ không", "còn nhận nữ không", "có nhận nữ không", "còn slot cho nữ không",
        "nữ còn nhận không"
    ),
    "ask_salary" to listOf(
        "lương cơ bản bao nhiêu", "tổng thu nhập tháng thế nào", "tính lương theo ngày hay theo tháng",
        "có được tăng ca nhiều không", "có bao ăn ở không", "mùng mấy thì được lãnh lương",
        "có cho ứng lương không"
    ),
    "ask_requirements" to listOf(
        "không có bằng cấp có làm được không", "tuổi cao có nhận không",
        "cận thị có làm được không", "hồ sơ thiếu bổ sung sau được không", "chưa kinh nghiệm có đào tạo không",
        "nhuộm tóc có nhận không", "thử việc có lương không"
    ),
    "actions_apply" to listOf(
        "xin địa chỉ qua làm với", "cho xin số điện thoại liên hệ", "tư vấn giúp em",
        "inbox em nha", "rep tin nhắn em với", "mai em qua nộp hồ sơ luôn nhé",
        "cho em đăng ký với", "gọi cho em số này nhé"
    ),
    "praises" to listOf(
        "chỗ làm có vẻ mát mẻ", "bao ăn ở là thấy ngon rồi", "xưởng to sạch sẽ ghê",
        "quản lý thân thiện lắm", "thấy môi trường tốt ghê", "công việc thấy cũng ổn"
    ),
    "skeptical_questions" to listOf(
        "có mất phí môi giới không", "có bắt mua đồng phục không", "có bị giam lương không",
        "có cần đóng cọc gì không", "có lừa đảo gì không", "có trừ đầu trừ đuôi gì không"
    ),
    "location" to listOf(
        "Bình Dương", "KCN VSIP","KCN Mỹ Phước"," Vsip 2A", "Nam tân uyên", "ST 3", "sóng thần 3", "vsip 2a", "Tân uyên", "Đồng an 2", "vĩnh tân", "Bến cát"
    ),
    "wishes" to listOf(
        "chúc shop mau tuyển đủ người", "chúc công ty ngày càng phát triển", "chúc sớm tìm được nhân viên",
        "chúc sếp mau kiếm được lính", "chúc kênh ngày càng phát triển"
    ),
    "interaction_bait" to listOf(
        "tương tác chéo nha cả nhà", "đẩy bài giúp ad", "thả tim chéo uy tín",
        "trả tương tác giúp em", "lên xu hướng nào", "chúc ngày mới năng lượng"
    ),
    "fillers" to listOf(
        "nha", "ạ", "nhé", "với ạ", "được không", "với"
    ),
    "emojis" to listOf(
        "👍", "😊", "🙏", "💪", "🔥", "💯", "😅", "👌", "🤝", "👀", "❤️"
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
    "{greetings} cho em hỏi có giam lương hay giữ cccd không {fillers}",
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
    "{emojis} {emojis} {emojis}"
)

// Từ điển các lỗi sai phổ biến
val exactTypos = mapOf(
    "việc" to listOf("ziệc", "viêc"),
    "chỗ" to listOf("chổ"),
    "nghỉ" to listOf("nghĩ"),
    "nghĩ" to listOf("ngĩ"),
    "tuổi" to listOf("tủi", "tuỗi"),
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
    "làm" to listOf("lm", "lam"),
    "cũ" to listOf("củ"),
    "kỹ" to listOf("kỉ", "kĩ"),
    "sao" to listOf("s"),
    "rồi" to listOf("rùi", "dồi", "r", "ròi"),
    "dạ" to listOf("zạ")
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