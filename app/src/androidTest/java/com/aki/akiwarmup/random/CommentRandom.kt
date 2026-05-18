package com.aki.akiwarmup.random

import java.util.Locale
import kotlin.random.Random

// Dữ liệu Keyword Pool mở rộng
val keywordPool = mapOf(
    "greetings" to listOf(
        "Anh ơi", "Chị ơi", "Dạ", "Ad ơi", "Chào a/c",
        "Shop ơi", "Bên mình", "Chủ thớt", "Bác ơi", "E chào a", "Cho xin hỏi",
        "Sếp ơi", "Chủ xưởng", "Nhà tuyển dụng ơi", "E hóng", "Chủ kênh ơi"
    ),
    "job_role" to listOf(
        "công việc này", "chỗ này", "việc này", "làm khâu này",
        "việc trên video", "làm y như video", "thời vụ"
    ),
    "age_status" to listOf(
        "30 tuổi", "45 tuổi", "ngoài 40", "mới ở quê lên",
        "từng làm công trình r", "làm bốc vác r", "công nhân may nghỉ đẻ xong",
        "thất nghiệp mấy tháng nay", "làm mộc 10 năm",
        "đã có gia đình", "đang nợ nần", "trông con nhỏ", "đi bộ đội mới về",
        "có bằng B2", "lái xe 5 năm", "biết hàn xì", "có chứng chỉ xe nâng",
        "sức khoẻ yếu", "lớn tuổi rồi", "từng làm giày da"
    ),
    "young_status" to listOf(
        "sn 2k", "e 2k4", "e 2k2", "vừa nghỉ cty r",
        "mới ra trường", "sn 2k1", "99 chưa có vk", "đang rảnh",
        "genz", "bỏ học sớm", "đang đợi lấy bằng", "muốn làm dịp hè",
        "đi làm thêm", "rớt đại học", "18 tủi", "chưa có kinh nghiệm gì"
    ),
    "questions_salary" to listOf(
        "lương cơ bản nhiêu v?", "tổng thu nhập tháng chục củ k?",
        "lương tính ngày hay tháng s a?", "có đc tăng ca k a?",
        "bao ăn ở k ạ?", "có cho ứng lương mùng 10 k?", "lương thực lãnh bn?",
        "lương cứng nhiu", "khoán hay tính ngày", "có tiền chuyên cần ko",
        "đóng bhxh ko", "lễ tết có thưởng k", "có hay trễ lương ko",
        "mấy tây thì lãnh lương", "làm khoán hay tính công"
    ),
    "short_questions" to listOf(
        "còn tuyển k?", "lương s?", "ở đâu v?", "địa chỉ s?",
        "xăm trổ nhận k?", "có ca đêm k?", "nhận nam k?",
        "phỏng vấn ở đâu?", "cần hs ko?", "thử việc mấy ngày?", "còn slot k?",
        "còn việc ko?", "có xe đưa rước k?",
        "mang cccd đi làm luôn đc k?", "hồ sơ thiếu bổ sung sau đc k?",
        "làm mấy tiếng 1 ca?", "xoay ca đc ko?", "chốt sổ chưa", "chính thức hay thời vụ", "có cccd photo thôi được không", "làm từ mấy giừo đến mấy giờ", "tăng ca nhiều không",
        "làm nóng không", "làm hctc hay xoay ca vậy", "có ca đêm k", "ca ngày lương sao", "có nhận chính thức không", "còn nhận k", "mai có nhận k"
    ),
    "questions_requirements" to listOf(
        "có cần bằng cấp 2 k?", "xăm full tay nhận k a?",
        "không có hồ sơ xin việc ngay đc k?", "tuổi cao có nhận k ạ?",
        "không biết chữ nhận ko?", "chưa biết việc có người chỉ ko?", "cận thị làm đc k?",
        "cận nặng có làm đc k", "1m50 có mần đc ko", "không kinh nghiệm có đào tạo k",
        "mù chữ nhưng lanh lẹ đc k", "có xét lý lịch k",
        "nhuộm tóc đc k", "mất cccd gốc nhận k", "xăm kín cổ đc k",
        "được mang dép k", "hút thuóc được khong"
    ),
    "actions" to listOf(
        "xin địa chỉ qua làm với", "cho e xin sdt liên hệ",
        "ib e với", "tư vấn e vs", "e muốn đi làm luôn",
        "gọi e số này nhé", "mai e lên nhận việc đc ko",
        "cho e xin 1 chân", "e đăng ký", "hướng dẫn e dăng kí vs",
        "chỉ e chỗ đk với", "liên hệ ai để phỏng vấn",
        "mai e mang hồ sơ qua luôn", "cho e địa chỉ chính xác"
    ),
    "abbrev_actions" to listOf(
        "xin dchi", "cho xin sdt", "ib nha", "check ib a ơi",
        "rep tn đi a", "alo e sdt này", "chấm", ". ib e", "xin in4",
        "xin 1 vé", "đk 1 slot", "inb zalo e", "lh e", "xin tt", "hóng", "ở đâu"
    ),
    "praise" to listOf(
        "cty làm ăn đàng hoàng", "chỗ làm mát mẻ",
        "bao ăn ở là ngon", "xưởng bự ghê", "việc có vẻ nhẹ",
        "làm v thoải mái", "xịn xò", "chỗ này quen quen",
        "việc ngon đó", "chỗ này đợt t làm r ok lắm",
        "bên này quản lý dễ thương", "thấy review tốt"
    ),
    "skeptical_questions" to listOf(
        "việc này có thu phí môi giới k ạ?", "lại đa cấp phải k?",
        "có chắc là k giam lương k?", "xin việc có mất tiền k a?",
        "có bắt mua đồng phục k?", "nhìn ảo ảo sao ấy nhỉ?",
        "việc ngon vậy có lừa k ae", "tin được không đây mọi người?",
        "vào làm có trừ tiền này kia k v?", "chắc không phải lừa đảo chứ?",
        "có đóng cọc k sốp?", "làm đàng hoàng k hay vô trừ đầu trừ đuôi?"
    ),
    "location" to listOf(
        "Bình Dương", "KCN VSIP","KCN Mỹ Phước"," Vsip 2A", "Nam tân uyên", "ST 3", "sóng thần 3", "vsip 2a", "Tân uyên", "Đồng an 2", "vĩnh tân", "Bến cát"
    ),
    "emojis" to listOf(
        "👍", "🙏", "💪", "😊", "🖐", "👋", "👀", "🔥", "💯", "😅", "😂", "🤔",
        "🥲", "😭", "🛑", "✅", "📍", "🤝", "👌", "👇"
    ),
    "filler" to listOf(
        "nha", "ạ", "vs ạ", "nhé", "đc ko a", "vậy ạ",
        "đó ạ", "vậy sốp", "luôn á", "trời", "đi anh", "giúp e"
    ),
    "wishes" to listOf(
        "chúc shop mau tuyển đủ người", "chúc cty ngày càng phát triển",
        "chủ kênh sớm tìm được người nha", "chúc a/c tuyển dụng suôn sẻ",
        "chúc may mắn", "mau tìm đc nhân viên nhé", "sớm chốt đc người nha sốp",
        "làm ăn phát đạt nhé", "chúc xưởng đắt hàng", "nhanh tuyển đủ slot nhé",
        "chúc sếp mau kiếm đc lính"
    ),
    "interaction_bait" to listOf(
        "tương tác nhé", "tt tốt nha", "qua lại uy tín", "trả fl nhé",
        "chúc ngày mới năng lượng", "đẩy bài giúp ad", "lên xu hướng nào",
        "tim chéo nha", "chấm tương tác", "ủn mông cho sốp", "tt chéo k m.n",
        "hỗ trợ nhau lên xu hướng", "chào ngày mới m.n", "ủng hộ kênh", "chấm mút trả tim"
    ),
    "teen_code_filler" to listOf(
        "z", "zậy", "ko", "k", "dc k", "đc k", "rùi", "r", "nà", "k ạ",
        "nek", "nhaaa", "đó chời", "á", "dới", "ik", "lm k", "lm j"
    )
)

val templates = listOf(
    "{short_questions}",
    "{short_questions} {abbrev_actions}",
    "Còn tuyển ko a",
    "{abbrev_actions} {emojis}",
    ".",
    "Chấm mút",
    "Quan tâm",
    "{greetings} {short_questions} {emojis}",
    "{greetings}, {abbrev_actions} {teen_code_filler}",
    "Làm {job_role} {questions_salary}",
    "E {young_status}. {short_questions}",
    "{short_questions} E {age_status} làm đc ko?",
    "Cho xin {abbrev_actions} đi {emojis}",
    "{greetings}, em {age_status}, {questions_requirements} {filler} {emojis}",
    "{greetings} e làm đc {job_role}. Cho e {abbrev_actions} {teen_code_filler} {emojis}",
    "Dạ {job_role} còn tuyển không ạ? E {young_status} {actions} {emojis}",
    "{praise} {teen_code_filler}. E {age_status}, {actions} {emojis}",
    "{greetings}, em {age_status} nhưng chưa có kinh nghiệm. {questions_requirements} Nếu được {actions} {emojis}",
    "Nhìn {praise} quá a ơi. Dạ {questions_salary} Em {young_status} {actions} {filler} {emojis}",
    "Em {age_status} đang tìm việc gấp. {job_role} này {questions_requirements} Được thì {abbrev_actions} đi làm luôn {emojis}",
    "Làm ở {location} {short_questions}",
    "{greetings} ở {location} có tuyển {job_role} k {teen_code_filler}",
    "Có ai làm ở {location} chưa cho e xin review {filler} {emojis}",
    "{greetings} e {young_status} muốn xin làm {job_role} ở {location} {actions}",
    "Kho ở {location} {questions_salary}",
    "E {age_status} mún mần {job_role} tại {location}. {questions_salary}",
    "{skeptical_questions} {emojis}",
    "{job_role} này {skeptical_questions}",
    "Thấy hoang mang quá, {skeptical_questions} {emojis}",
    "Cho e hỏi {skeptical_questions} {teen_code_filler}",
    "E {young_status} đag tìm việc. {abbrev_actions} {emojis}",
    "Lương {job_role} dạo này s a? Đc {abbrev_actions} {teen_code_filler}",
    "{questions_requirements} {abbrev_actions} nha shop",
    "Cho {abbrev_actions}. E {young_status} {actions}",
    "E {age_status} {questions_requirements} {actions}",

    // --- CÁC TEMPLATES BỔ SUNG MỚI ---
    "Đang cần việc gấp, {greetings} {actions} {emojis}",
    "{greetings} cho e hỏi {skeptical_questions} Ok thì {actions}",
    "{praise} nhưng mà {skeptical_questions} {teen_code_filler}",
    "Mình ở {location}, {age_status}, {short_questions} {emojis}",
    "Cần tìm việc ở {location}. {age_status} làm {job_role} {questions_requirements}",
    "Nếu {questions_requirements} thì {actions} nha {filler}",
    "{short_questions} {questions_salary} Được thì {abbrev_actions} {emojis}",
    "Ai từng làm {job_role} ở {location} chưa, cho xin review với. {skeptical_questions}",
    "Nghe nói {praise} mà k biết {skeptical_questions} {emojis}",
    "Công việc {job_role} này {questions_salary} {filler} {abbrev_actions}",
    "Xin hỏi {questions_requirements} E {age_status} {actions} {emojis}",
    "{job_role} ở {location} {questions_salary} {questions_requirements}",
    "{greetings} check tin nhắn e với, e {young_status} muốn hỏi {job_role} {emojis}",
    "Làm {job_role} thì {questions_requirements} {emojis} Cho e {abbrev_actions} {filler}",
    "{skeptical_questions} Nếu làm ăn đàng hoàng thì {actions} nha {emojis}",
    "Đang rảnh, e mún làm {job_role} ở {location}. {questions_salary} {emojis}",
    "Từng làm ở {location} rồi, {job_role} ở đây {questions_salary} {emojis}",
    "Mọi người cho e hỏi {job_role} {questions_salary} {skeptical_questions} {emojis}",

    // --- CÁC TEMPLATES CHÚC MAY MẮN VÀ CHỈ DÙNG EMOJI ---
    "{wishes} {emojis}",
    "{wishes} {filler} {emojis}",
    "{praise}, {wishes} {emojis}",
    "{greetings} {wishes} {emojis}",
    "Tuyệt vời. {wishes} {emojis}",
    "{emojis}",
    "{emojis} {emojis}",
    "{emojis} {emojis} {emojis}",

    // --- CÁC TEMPLATES TƯƠNG TÁC DẠO (CÀY VIEW, CHÉO FOLLOW) ---
    "{interaction_bait}",
    "{interaction_bait} {emojis}",
    "{greetings} {interaction_bait} {emojis}",
    "{interaction_bait} nha {filler} {emojis}",
    "{praise}, {interaction_bait} {emojis}",
    "Vào thả tim cho video. {interaction_bait} {emojis}",
    "Đang rảnh đi {interaction_bait} {emojis}",
    "Lên xu hướng. {interaction_bait} {emojis}",
    "Cmt dạo. {interaction_bait} {emojis}"
)

// Từ điển các lỗi sai phổ biến
val exactTypos = mapOf(
    "việc" to listOf("ziệc"),
    "chỗ" to listOf("chổ"),
    "nghỉ" to listOf("nghĩ"),
    "nghĩ" to listOf("ngĩ"),
    "tuổi" to listOf("tủi", "tuỗi"),
    "tuyển" to listOf("tuyễn", "tuyển"),
    "lương" to listOf("lươg"),
    "kinh" to listOf("kih"),
    "nghiệm" to listOf("ngiệm"),
    "hồ" to listOf("hô"),
    "sơ" to listOf("sờ"),
    "tháng" to listOf("thág"),
    "gì" to listOf("j", "zì"),
    "thì" to listOf("thỳ"),
    "quá" to listOf("wá", "qá"),
    "được" to listOf("đươc", "được", "dc", "đc"),
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
    Regex("tr") to "ch",
    Regex("^ch") to "tr",
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

    val words = text.split(" ")
    val modifiedWords = words.map { word ->
        // Xác suất 20% bị sai chính tả ở mỗi từ
        if (Random.nextDouble() < 0.20) {
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
                newWord + punctuation
            } else {
                word // Trả lại nếu không khớp định dạng
            }
        } else {
            word // 80% giữ nguyên từ gốc
        }
    }
    return modifiedWords.joinToString(" ")
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

    // Viết hoa chữ cái đầu tiên của câu
    if (template.isNotEmpty()) {
        template = template.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
    }

    return applyTypos(template, enableTypos)
}