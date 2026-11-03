package com.aki.akiwarmup.core.dsl

import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import java.util.regex.Pattern
import com.aki.akiwarmup.core.logger.AkiLog
import com.aki.akiwarmup.core.logger.LogTag

/**
 * Interface đại diện cho một tiêu chí tìm kiếm UI Element.
 */
interface Selector {
    fun find(device: UiDevice): UiObject2?
    fun findAll(device: UiDevice): List<UiObject2>
    fun find(parent: UiObject2): UiObject2?
    fun findAll(parent: UiObject2): List<UiObject2>
    fun exists(device: UiDevice): Boolean
    fun exists(parent: UiObject2): Boolean = find(parent) != null
}

/**
 * Selector cơ bản dựa trên UI Automator BySelector.
 */
class SimpleSelector : Selector {
    var resourceId: String? = null
    var text: String? = null
    var textContains: String? = null
    var textMatches: String? = null
    var desc: String? = null
    var descContains: String? = null
    var className: String? = null
    var pkg: String? = null
    var clickable: Boolean? = null
    var enabled: Boolean? = null
    var depth: Int? = null

    // Lưu các con/cháu bắt buộc
    var descendant: Selector? = null

    private var cachedTextPattern: Pair<String, Pattern>? = null

    companion object {
        private val ANY_PKG_PATTERN: Pattern = Pattern.compile(".*")
    }

    private fun resolveTextPattern(): Pattern? {
        val patternStr = textMatches ?: return null
        val cached = cachedTextPattern
        if (cached != null && cached.first == patternStr) {
            return cached.second
        }
        return Pattern.compile(patternStr).also {
            cachedTextPattern = patternStr to it
        }
    }

    internal fun tryMerge(other: SimpleSelector): SimpleSelector? {
        if (this.resourceId != null && other.resourceId != null && this.resourceId != other.resourceId) return null
        if (this.text != null && other.text != null && this.text != other.text) return null
        if (this.textContains != null && other.textContains != null && this.textContains != other.textContains) return null
        if (this.textMatches != null && other.textMatches != null && this.textMatches != other.textMatches) return null
        if (this.desc != null && other.desc != null && this.desc != other.desc) return null
        if (this.descContains != null && other.descContains != null && this.descContains != other.descContains) return null
        if (this.className != null && other.className != null && this.className != other.className) return null
        if (this.pkg != null && other.pkg != null && this.pkg != other.pkg) return null
        if (this.clickable != null && other.clickable != null && this.clickable != other.clickable) return null
        if (this.enabled != null && other.enabled != null && this.enabled != other.enabled) return null
        if (this.depth != null && other.depth != null && this.depth != other.depth) return null
        if (this.descendant != null && other.descendant != null) return null

        // Không gộp nếu cả hai cùng định nghĩa tiêu chí text khác loại (VD: text và textContains) vì BySelector chỉ cho phép 1 tiêu chí text
        val thisHasText = text != null || textContains != null || textMatches != null
        val otherHasText = other.text != null || other.textContains != null || other.textMatches != null
        if (thisHasText && otherHasText && (text != other.text || textContains != other.textContains || textMatches != other.textMatches)) {
            return null
        }

        val thisHasDesc = desc != null || descContains != null
        val otherHasDesc = other.desc != null || other.descContains != null
        if (thisHasDesc && otherHasDesc && (desc != other.desc || descContains != other.descContains)) {
            return null
        }

        return SimpleSelector().also { merged ->
            merged.resourceId = this.resourceId ?: other.resourceId
            merged.text = this.text ?: other.text
            merged.textContains = this.textContains ?: other.textContains
            merged.textMatches = this.textMatches ?: other.textMatches
            merged.desc = this.desc ?: other.desc
            merged.descContains = this.descContains ?: other.descContains
            merged.className = this.className ?: other.className
            merged.pkg = this.pkg ?: other.pkg
            merged.clickable = this.clickable ?: other.clickable
            merged.enabled = this.enabled ?: other.enabled
            merged.depth = this.depth ?: other.depth
            merged.descendant = this.descendant ?: other.descendant
        }
    }

    fun toBySelector(): BySelector {
        val textPattern = resolveTextPattern()

        // Xác định field nào được dùng để khởi tạo BySelector.
        // Quan trọng: KHÔNG được gọi setter cho field này thêm lần nữa,
        // UIAutomator sẽ throw IllegalStateException: "already defined".
        val initField: String
        val s: BySelector = when {
            resourceId != null   -> { initField = "resourceId"; By.res(resourceId!!) }
            text != null         -> { initField = "text"; By.text(text!!) }
            textContains != null -> { initField = "textContains"; By.textContains(textContains!!) }
            textPattern != null  -> { initField = "textMatches"; By.text(textPattern) }
            desc != null         -> { initField = "desc"; By.desc(desc!!) }
            descContains != null -> { initField = "descContains"; By.descContains(descContains!!) }
            className != null    -> { initField = "className"; By.clazz(className!!) }
            pkg != null          -> { initField = "pkg"; By.pkg(pkg!!) }
            clickable != null    -> { initField = "clickable"; By.clickable(clickable!!) }
            enabled != null      -> { initField = "enabled"; By.enabled(enabled!!) }
            depth != null        -> { initField = "depth"; By.depth(depth!!) }
            else                 -> { initField = "none"; By.pkg(ANY_PKG_PATTERN) }
        }

        // Áp các tiêu chí phụ — bỏ qua field đã dùng khởi tạo và tránh xung đột nhóm text/desc
        if (initField != "resourceId") resourceId?.let { s.res(it) }
        if (initField != "text" && initField != "textContains" && initField != "textMatches") {
            when {
                text != null         -> s.text(text!!)
                textContains != null -> s.textContains(textContains!!)
                textPattern != null  -> s.text(textPattern)
            }
        }
        if (initField != "desc" && initField != "descContains") {
            when {
                desc != null         -> s.desc(desc!!)
                descContains != null -> s.descContains(descContains!!)
            }
        }
        if (initField != "className") className?.let { s.clazz(it) }
        if (initField != "pkg") pkg?.let { s.pkg(it) }
        if (initField != "clickable") clickable?.let { s.clickable(it) }
        if (initField != "enabled") enabled?.let { s.enabled(it) }
        if (initField != "depth") depth?.let { s.depth(it) }

        descendant?.let {
            if (it is SimpleSelector) {
                s.hasDescendant(it.toBySelector())
            }
        }

        return s
    }

    override fun find(device: UiDevice): UiObject2? {
        val sel = toBySelector()
        val obj = device.findObject(sel)
        AkiLog.d(LogTag.DETECT, "find($this) → ${if (obj != null) "Found" else "Not Found"}")
        return obj
    }
    override fun findAll(device: UiDevice): List<UiObject2> {
        val sel = toBySelector()
        val list = device.findObjects(sel)
        AkiLog.d(LogTag.DETECT, "findAll($this) → ${list.size} found")
        return list
    }
    override fun find(parent: UiObject2): UiObject2? {
        val sel = toBySelector()
        val obj = parent.findObject(sel)
        AkiLog.d(LogTag.DETECT, "findChild($this) → ${if (obj != null) "Found" else "Not Found"}")
        return obj
    }
    override fun findAll(parent: UiObject2): List<UiObject2> {
        val sel = toBySelector()
        val list = parent.findObjects(sel)
        AkiLog.d(LogTag.DETECT, "findChildren($this) → ${list.size} found")
        return list
    }
    override fun exists(device: UiDevice): Boolean = device.hasObject(toBySelector())
    override fun exists(parent: UiObject2): Boolean = parent.hasObject(toBySelector())

    override fun toString(): String {
        val parts = mutableListOf<String>()
        // Strip package prefix: "com.ss.android.ugc.trill:id/ubv" → "#ubv"
        resourceId?.let { parts.add("#${it.substringAfterLast('/')}") }
        text?.let { parts.add("\"${it.take(30)}\"") }
        textContains?.let { parts.add("~\"${it.take(30)}\"") }
        textMatches?.let { parts.add("/${it.take(30)}/") }
        desc?.let { parts.add("desc:${it.take(20)}") }
        descContains?.let { parts.add("desc~:${it.take(20)}") }
        className?.let { parts.add(".${it.substringAfterLast('.')}") }
        pkg?.let { parts.add("pkg:$it") }
        clickable?.let { parts.add("clickable:$it") }
        enabled?.let { parts.add("enabled:$it") }
        depth?.let { parts.add("depth:$it") }
        descendant?.let { parts.add("has($it)") }
        return if (parts.isEmpty()) "*" else parts.joinToString("|")
    }

    // Fluent API
    fun id(id: String) = apply { resourceId = id }
    fun text(v: String) = apply { text = v }
    fun textContains(v: String) = apply { textContains = v }
    fun textLike(pattern: String) = apply { textMatches = pattern }
    fun desc(v: String) = apply { desc = v }
    fun descContains(v: String) = apply { descContains = v }
    fun clazz(v: String) = apply { className = v }
    fun pkg(v: String) = apply { pkg = v }
    fun clickable(v: Boolean) = apply { clickable = v }
    fun enabled(v: Boolean) = apply { enabled = v }

    fun contains(child: Selector) = apply { descendant = child }

    infix fun and(other: Selector): Selector = AndSelector(this, other)

    infix fun or(other: Selector): Selector = OrSelector(this, other)

    /**
     * Hỗ trợ đảo ngược điều kiện tìm kiếm (phủ định).
     * Phục vụ cho việc kiểm tra element KHÔNG tồn tại (VD: Selector.not().exists()).
     */
    fun not(): Selector = NotSelector(this)
}

/**
 * Hỗ trợ toán tử OR.
 */
class OrSelector(val left: Selector, val right: Selector) : Selector {
    override fun find(device: UiDevice): UiObject2? = left.find(device) ?: right.find(device)
    override fun findAll(device: UiDevice): List<UiObject2> =
        (left.findAll(device) + right.findAll(device)).distinct()

    override fun find(parent: UiObject2): UiObject2? = left.find(parent) ?: right.find(parent)
    override fun findAll(parent: UiObject2): List<UiObject2> =
        (left.findAll(parent) + right.findAll(parent)).distinct()

    override fun exists(device: UiDevice): Boolean = left.exists(device) || right.exists(device)
    override fun exists(parent: UiObject2): Boolean = left.exists(parent) || right.exists(parent)

    override fun toString(): String = "($left OR $right)"
}

/**
 * Hỗ trợ toán tử AND phức hợp (khi không thể merge vào SimpleSelector).
 */
class AndSelector(val left: Selector, val right: Selector) : Selector {
    private val mergedSimple: SimpleSelector? =
        if (left is SimpleSelector && right is SimpleSelector) left.tryMerge(right) else null

    override fun find(device: UiDevice): UiObject2? {
        mergedSimple?.find(device)?.let { return it }
        if (left is NotSelector) {
            return if (left.exists(device)) right.find(device) else null
        }
        if (right is NotSelector) {
            return if (right.exists(device)) left.find(device) else null
        }
        val leftObj = left.find(device) ?: return null
        return if (right.exists(device)) leftObj else null
    }

    override fun findAll(device: UiDevice): List<UiObject2> {
        mergedSimple?.findAll(device)?.takeIf { it.isNotEmpty() }?.let { return it }
        if (left is NotSelector) {
            return if (left.exists(device)) right.findAll(device) else emptyList()
        }
        if (right is NotSelector) {
            return if (right.exists(device)) left.findAll(device) else emptyList()
        }
        val leftResults = left.findAll(device)
        if (leftResults.isEmpty()) return emptyList()
        val rightResults = right.findAll(device)
        if (rightResults.isEmpty()) return emptyList()
        return (leftResults + rightResults).distinct()
    }

    override fun find(parent: UiObject2): UiObject2? {
        mergedSimple?.find(parent)?.let { return it }
        if (left is NotSelector) {
            return if (left.exists(parent)) right.find(parent) else null
        }
        if (right is NotSelector) {
            return if (right.exists(parent)) left.find(parent) else null
        }
        val leftObj = left.find(parent) ?: return null
        return if (right.exists(parent)) leftObj else null
    }

    override fun findAll(parent: UiObject2): List<UiObject2> {
        mergedSimple?.findAll(parent)?.takeIf { it.isNotEmpty() }?.let { return it }
        if (left is NotSelector) {
            return if (left.exists(parent)) right.findAll(parent) else emptyList()
        }
        if (right is NotSelector) {
            return if (right.exists(parent)) left.findAll(parent) else emptyList()
        }
        val leftResults = left.findAll(parent)
        if (leftResults.isEmpty()) return emptyList()
        val rightResults = right.findAll(parent)
        if (rightResults.isEmpty()) return emptyList()
        return (leftResults + rightResults).distinct()
    }

    override fun exists(device: UiDevice): Boolean = left.exists(device) && right.exists(device)
    override fun exists(parent: UiObject2): Boolean = left.exists(parent) && right.exists(parent)

    override fun toString(): String = "($left AND $right)"
}

/**
 * Hỗ trợ toán tử NOT (phủ định).
 * Chủ yếu dùng để kiểm tra sự không tồn tại của một UI Element, do UIAutomator không có sẵn khái niệm phủ định cho việc tìm kiếm trực tiếp.
 */
class NotSelector(val selector: Selector): Selector {
    override fun find(device: UiDevice): UiObject2? = null

    override fun findAll(device: UiDevice): List<UiObject2> = emptyList()

    override fun find(parent: UiObject2): UiObject2? = null

    override fun findAll(parent: UiObject2): List<UiObject2> = emptyList()

    override fun exists(device: UiDevice): Boolean = !selector.exists(device)
    override fun exists(parent: UiObject2): Boolean = !selector.exists(parent)

    override fun toString(): String = "NOT ($selector)"
}

/**
 * Hỗ trợ tìm kiếm lồng nhau (Child search).
 */
class NestedSelector(val parentSelector: Selector, val childSelector: Selector) : Selector {
    override fun find(device: UiDevice): UiObject2? {
        val p = parentSelector.find(device) ?: return null
        return childSelector.find(p)
    }

    override fun findAll(device: UiDevice): List<UiObject2> {
        return parentSelector.findAll(device).flatMap { childSelector.findAll(it) }.distinct()
    }

    override fun find(parent: UiObject2): UiObject2? {
        val p = parentSelector.find(parent) ?: return null
        return childSelector.find(p)
    }

    override fun findAll(parent: UiObject2): List<UiObject2> {
        return parentSelector.findAll(parent).flatMap { childSelector.findAll(it) }.distinct()
    }

    override fun exists(device: UiDevice): Boolean {
        if (parentSelector is SimpleSelector && childSelector is SimpleSelector) {
            return device.hasObject(parentSelector.toBySelector().hasDescendant(childSelector.toBySelector()))
        }
        return find(device) != null
    }

    override fun exists(parent: UiObject2): Boolean {
        if (parentSelector is SimpleSelector && childSelector is SimpleSelector) {
            return parent.hasObject(parentSelector.toBySelector().hasDescendant(childSelector.toBySelector()))
        }
        return find(parent) != null
    }

    override fun toString(): String = "$parentSelector -> $childSelector"
}

// --- DSL Functions ---

fun id(id: String) = SimpleSelector().id(id)
fun text(v: String) = SimpleSelector().text(v)
fun textContains(v: String) = SimpleSelector().textContains(v)
fun desc(v: String) = SimpleSelector().desc(v)
fun descContains(v: String) = SimpleSelector().descContains(v)
fun clazz(v: String) = SimpleSelector().clazz(v)
fun pkg(v: String) = SimpleSelector().pkg(v)
fun clickable(v: Boolean) = SimpleSelector().clickable(v)

fun any(vararg selectors: Selector): Selector {
    if (selectors.isEmpty()) return SimpleSelector()
    var result = selectors[0]
    for (i in 1 until selectors.size) {
        result = OrSelector(result, selectors[i])
    }
    return result
}

fun all(vararg selectors: Selector): Selector {
    if (selectors.isEmpty()) return SimpleSelector()
    var result = selectors[0]
    for (i in 1 until selectors.size) {
        result = result.and(selectors[i])
    }
    return result
}

fun contains(child: Selector) = SimpleSelector().contains(child)

fun Selector.child(child: Selector): Selector = NestedSelector(this, child)
fun Selector.like(pattern: String): Selector {
    if (this is SimpleSelector) return this.textLike(pattern)
    return this
}

infix fun Selector.or(other: Selector): Selector = OrSelector(this, other)
infix fun Selector.and(other: Selector): Selector {
    if (this is SimpleSelector) return this.and(other)
    return AndSelector(this, other)
}
