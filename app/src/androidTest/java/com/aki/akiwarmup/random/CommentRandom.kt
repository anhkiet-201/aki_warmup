package com.aki.akiwarmup.random

import java.util.Locale
import kotlin.random.Random

// Dữ liệu Keyword Pool sạch (được làm mới toàn bộ hướng tới vai trò Nữ và không chứa vị trí cụ thể)
val keywordPool = mapOf(
    "emojis" to listOf(
        "👍", "😊", "🙏", "💪", "🔥", "💯", "😅", "👌", "🤝", "👀", "❤️",
        "✨", "🎉", "🌟", "🍀", "😃", "🥰", "😍", "🤩", "🤗", "🥳",
        "🌻", "🌸", "🎈", "🌞", "🌈", "🙌", "👏", "✌️", "⭐", "💝",
        "💖", "💘", "💗", "💓", "💛", "💚", "💙", "💜", "🧡", "☘️"
    )
)

// Các mẫu câu templates thiết kế chặt chẽ
val templates = setOf(
    "O đâu z p",
    "còn k ạ",
    "Còn tuyển k ạ",
    "làm ở đâu vậy",
    "check zalo tư vấn e nhé",
    "làm việc gì",
    "cung ty o dau day ạ",
    "còn tuyển dụng ko bạn",
    "Công Ty này thì làm công việc gì vậy hả ?",
    "ở đâu",
    "CTY ở đâu vậy",
    "Còn tuyển công nhân không ạ",
    "có nhận làm thời vụ ko ạ",
    "cty lm về gì ạ",
    "ib",
    "có hỗ trợ trọ k",
    "còn tuyển không",
    "tu vấn với ạ",
    "ở đâu",
    ".",
    "lương s",
    "có nhận tv k",
    "có đi hành chính ko",
    "làm tv rồi lên ct đc ko",
    "còn nhận hk",
    "ib đi",
    "hành chính hay đi ca ạ",
    "cận có nhận ko ạ",
    "còn hk ạ",
    "lương sao vậy ạ",
    "16t nhận hk ạ",
    "có cần hs gì ko ạ",
    "tca hay xoay ca vậy em",
    "làm tca đến mấy gì em",
    "ở chỗ nào",
    "có nhận ct hk c",
    "có nhận bà bầu ko ạ",
    "rep cmt cái sếp ơi",
    "CT hay TV ạ",
    "có tuyển chính thức ko",
    "cv nhẹ ko ạ",
    "lương ntn vậy",
    "tv em với",
    "nhận thiếu tủi ko ạ",
    "có hỗ trợ trọ k ạ",
    "xin sdt",
    "làm dễ ko",
    "ko tăng ca đc ko",
    "còn nhận ko ạ",
    "có nhận QC hk c",
    "cty gì đó",
    "lm từ mấy giờ",
    "công nhân lương thực lãnh bao nhiêu",
    "có thưởng gì k",
    "có chạy sản lượng ko",
    "thiếu tháng có nhận ko chị",
    "có tc ko",
    "tca đến mấy giờ",
    "không tăng ca đc ko",
    "có làm chủ nhật ko",
    "đường số mấy",
    "có nhận xăm hk",
    "này 9thức hay tvụ",
    "cty cũ em làm nè",
    "tca bao nhiêu 1 tiếng",
    "làm ngồi hay sao ạ",
    "em mang dép đc hk",
    "cccd photo đc ko",
    "làm hành chính hay đi ca vậy . Vô ca mấy giờ",
    "có bắt sản lượng hk ạ",
    "thời vụ 8 tiếng bao nhiêu vậy",
    "cho e xin địa chỉ CTY với",
    "e 17t làm đc k ạ sắp 18",
    "CTY làm ngồi hay đứng vậy ak",
    "tuyển lại rồi hả",
    "Còn tuyển K cj",
    "Cty co di ca đêm k ak",
    "tuan hay thang vậy",
    "Lương S z",
    "cty làm gì ạ",
    "Ib cj ơi",
    "con tuyen k bn",
    "nào nhận vc",
    "luong sao ak",
    "nhạn đến bn tuổi v",
    "còn tiển kh e",
    "tuyển nam nữ kh",
    "có tiển vk ck ko e",
    "thiếu tuổi dc kh",
    " luong s",
    "còn k",
    "bao nhieu 1 ngày",
    "làm ngày hay làm đêm v",
    "có nhận chính thức k",
    "ở đâu v ak",
    "lv từ mấy h",
    "cho e hỏi còn nhạn kh ak",
    "ad ơi còn nhận hk",
    "cho xin 1 chân vs",
    "40t có nhạn kh e",
    "cho cj 1 chân vơi nha",
    "còn nhan tv kh c",
    "c ơi, còn nhận ng ko",
    "có nhận xâm hk",
    "tomboy có nhận hk",
    "dễ làm kh ad ơi",
    "xăm có nhạn hok ak",
    "lương sao v bn oi",
    "cty còn k c",
    "còn tuyẻn kg",
    "cty nằm đâu",
    "nằm ở đâu ak",
    "{emojis}{emojis}{emojis}"
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
    var template = templates.shuffled().random()

    if (Random.nextDouble() < 0.10) {
        template += " {emojis}"
    }

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