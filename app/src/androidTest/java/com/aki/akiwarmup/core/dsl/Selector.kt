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

    fun toBySelector(): BySelector {
        // Xác định field nào được dùng để khởi tạo BySelector.
        // Quan trọng: KHÔNG được gọi setter cho field này thêm lần nữa,
        // UIAutomator sẽ throw IllegalStateException: "already defined".
        val initField = when {
            resourceId != null -> "resourceId"
            text != null       -> "text"
            desc != null       -> "desc"
            className != null  -> "className"
            else               -> "none"
        }

        val s: BySelector = when (initField) {
            "resourceId" -> By.res(resourceId!!)
            "text"       -> By.text(text!!)
            "desc"       -> By.desc(desc!!)
            "className"  -> By.clazz(className!!)
            else         -> By.pkg(Pattern.compile(".*"))
        }

        // Áp các tiêu chí phụ — bỏ qua field đã dùng khởi tạo
        if (initField != "resourceId") resourceId?.let { s.res(it) }
        if (initField != "text") {
            text?.let { s.text(it) }
            textMatches?.let { s.text(Pattern.compile(it)) }
        }
        textContains?.let { s.textContains(it) }
        if (initField != "desc") desc?.let { s.desc(it) }
        descContains?.let { s.descContains(it) }
        if (initField != "className") className?.let { s.clazz(it) }
        pkg?.let { s.pkg(it) }
        clickable?.let { s.clickable(it) }
        enabled?.let { s.enabled(it) }
        depth?.let { s.depth(it) }

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
        AkiLog.d(LogTag.DETECT, "find(${obj?.toName()}) → ${if (obj != null) "Found" else "Not Found"}")
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
        AkiLog.d(LogTag.DETECT, "findChild(${this}) → ${if (obj != null) "Found" else "Not Found"}")
        return obj
    }
    override fun findAll(parent: UiObject2): List<UiObject2> {
        val sel = toBySelector()
        val list = parent.findObjects(sel)
        AkiLog.d(LogTag.DETECT, "findChildren($this) → ${list.size} found")
        return list
    }
    override fun exists(device: UiDevice): Boolean = device.hasObject(toBySelector())

    override fun toString(): String {
        val parts = mutableListOf<String>()
        // Strip package prefix: "com.ss.android.ugc.trill:id/ubv" → "#ubv"
        resourceId?.let { parts.add("#${it.substringAfterLast('/')}") }
        text?.let { parts.add("\"${it.take(30)}\"") }
        desc?.let { parts.add("desc:${it.take(20)}") }
        pkg?.let { parts.add("pkg:$it") }
        return parts.joinToString("|")
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

    override fun toString(): String = "($left OR $right)"
}

/**
 * Hỗ trợ toán tử AND phức hợp (khi không thể merge vào SimpleSelector).
 */
class AndSelector(val left: Selector, val right: Selector) : Selector {
    override fun find(device: UiDevice): UiObject2? = findAll(device).firstOrNull()

    override fun findAll(device: UiDevice): List<UiObject2> {
        val leftResults = left.findAll(device)
        val rightResults = right.findAll(device)
        return leftResults.filter { l -> rightResults.any { r -> l == r } }
    }

    override fun find(parent: UiObject2): UiObject2? = findAll(parent).firstOrNull()
    override fun findAll(parent: UiObject2): List<UiObject2> {
        val leftResults = left.findAll(parent)
        val rightResults = right.findAll(parent)
        return leftResults.filter { l -> rightResults.any { r -> l == r } }
    }

    override fun exists(device: UiDevice): Boolean = left.exists(device) && right.exists(device)

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

    override fun findAll(parent: UiObject2): List<UiObject2> {
        TODO("Not yet implemented")
    }

    override fun exists(device: UiDevice): Boolean = !selector.exists(device)

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

    override fun exists(device: UiDevice): Boolean =
        parentSelector.exists(device) && parentSelector.child(childSelector).exists(device)

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
