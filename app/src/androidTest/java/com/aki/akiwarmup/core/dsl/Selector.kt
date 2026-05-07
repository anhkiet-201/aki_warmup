package com.aki.akiwarmup.core.dsl

import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import java.util.regex.Pattern

/**
 * Interface đại diện cho một tiêu chí tìm kiếm UI Element.
 */
interface Selector {
    fun find(device: UiDevice): UiObject2?
    fun findAll(device: UiDevice): List<UiObject2>
    fun find(parent: UiObject2): UiObject2?
    fun findAll(parent: UiObject2): List<UiObject2>
    
    fun exists(device: UiDevice): Boolean = find(device) != null
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
    var index: Int? = null
    
    // Lưu các con/cháu bắt buộc
    var descendant: Selector? = null

    fun toBySelector(): BySelector {
        // Khởi tạo một selector cơ bản. UI Automator yêu cầu ít nhất 1 tiêu chí.
        // Chúng ta dùng package name hiện tại làm mặc định nếu không có gì khác.
        val s = By.pkg(Pattern.compile(".*"))
        
        resourceId?.let { s.res(it) }
        text?.let { s.text(it) }
        textContains?.let { s.textContains(it) }
        textMatches?.let { s.text(Pattern.compile(it)) }
        desc?.let { s.desc(it) }
        descContains?.let { s.descContains(it) }
        className?.let { s.clazz(it) }
        pkg?.let { s.pkg(it) }
        clickable?.let { s.clickable(it) }
        enabled?.let { s.enabled(it) }
        depth?.let { s.depth(it) }
        index?.let { s.index(it) }
        
        descendant?.let { 
            if (it is SimpleSelector) {
                s.hasDescendant(it.toBySelector())
            }
        }
        
        return s
    }

    override fun find(device: UiDevice): UiObject2? = device.findObject(toBySelector())
    override fun findAll(device: UiDevice): List<UiObject2> = device.findObjects(toBySelector())
    override fun find(parent: UiObject2): UiObject2? = parent.findObject(toBySelector())
    override fun findAll(parent: UiObject2): List<UiObject2> = parent.findObjects(toBySelector())

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
    fun index(v: Int) = apply { index = v }
    
    fun contains(child: Selector) = apply { descendant = child }

    infix fun and(other: Selector): Selector {
        if (other is SimpleSelector) {
            // Merge criteria nếu cả hai đều là SimpleSelector
            val merged = SimpleSelector()
            merged.resourceId = other.resourceId ?: this.resourceId
            merged.text = other.text ?: this.text
            merged.textContains = other.textContains ?: this.textContains
            merged.textMatches = other.textMatches ?: this.textMatches
            merged.desc = other.desc ?: this.desc
            merged.descContains = other.descContains ?: this.descContains
            merged.className = other.className ?: this.className
            merged.pkg = other.pkg ?: this.pkg
            merged.clickable = other.clickable ?: this.clickable
            merged.enabled = other.enabled ?: this.enabled
            merged.descendant = other.descendant ?: this.descendant
            return merged
        }
        return AndSelector(this, other)
    }

    infix fun or(other: Selector): Selector = OrSelector(this, other)
}

/**
 * Hỗ trợ toán tử OR.
 */
class OrSelector(val left: Selector, val right: Selector) : Selector {
    override fun find(device: UiDevice): UiObject2? = left.find(device) ?: right.find(device)
    override fun findAll(device: UiDevice): List<UiObject2> = (left.findAll(device) + right.findAll(device)).distinct()
    override fun find(parent: UiObject2): UiObject2? = left.find(parent) ?: right.find(parent)
    override fun findAll(parent: UiObject2): List<UiObject2> = (left.findAll(parent) + right.findAll(parent)).distinct()
}

/**
 * Hỗ trợ toán tử AND phức hợp (khi không thể merge vào SimpleSelector).
 */
class AndSelector(val left: Selector, val right: Selector) : Selector {
    override fun find(device: UiDevice): UiObject2? {
        // Tìm element thỏa mãn left, sau đó kiểm tra xem nó có thỏa mãn right không.
        // Điều này hơi khó vì Selector find trả về object. 
        // Đơn giản nhất là findAll và filter.
        return findAll(device).firstOrNull()
    }

    override fun findAll(device: UiDevice): List<UiObject2> {
        val leftResults = left.findAll(device)
        // Đây là phần khó: làm sao biết UiObject2 khớp với selector?
        // UI Automator không cung cấp hàm matches(UiObject2, BySelector).
        // Giải pháp: Dùng findAll của right và lấy giao điểm.
        val rightResults = right.findAll(device)
        return leftResults.filter { l -> rightResults.any { r -> l == r } }
    }

    override fun find(parent: UiObject2): UiObject2? = findAll(parent).firstOrNull()
    override fun findAll(parent: UiObject2): List<UiObject2> {
        val leftResults = left.findAll(parent)
        val rightResults = right.findAll(parent)
        return leftResults.filter { l -> rightResults.any { r -> l == r } }
    }
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
fun index(v: Int) = SimpleSelector().index(v)

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
    return this // Không hỗ trợ like cho Or/And selector trực tiếp
}

infix fun Selector.or(other: Selector): Selector = OrSelector(this, other)
infix fun Selector.and(other: Selector): Selector {
    if (this is SimpleSelector) return this.and(other)
    return AndSelector(this, other)
}
