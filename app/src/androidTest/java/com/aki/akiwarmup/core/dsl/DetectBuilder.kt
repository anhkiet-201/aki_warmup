package com.aki.akiwarmup.core.dsl

import com.aki.akiwarmup.core.logger.AkiLog
import com.aki.akiwarmup.core.logger.LogTag

class DetectBuilder {
    fun has(selector: Selector) = DetectPredicate { device ->
        val exists = selector.exists(device)
        AkiLog.d(LogTag.DETECT, "has($selector) → $exists")
        exists
    }

    fun any(vararg predicates: DetectPredicate) = DetectPredicate { device ->
        val result = predicates.any { it.evaluate(device) }
        AkiLog.d(LogTag.DETECT, "any(...) → $result")
        result
    }

    fun any(vararg selectors: Selector) = DetectPredicate { device ->
        val result = selectors.any { it.exists(device) }
        AkiLog.d(LogTag.DETECT, "any(${selectors.joinToString()}) → $result")
        result
    }

    fun all(vararg predicates: DetectPredicate) = DetectPredicate { device ->
        val result = predicates.all { it.evaluate(device) }
        AkiLog.d(LogTag.DETECT, "all(...) → $result")
        result
    }

    fun all(vararg selectors: Selector) = DetectPredicate { device ->
        val result = selectors.all { it.exists(device) }
        AkiLog.d(LogTag.DETECT, "all(${selectors.joinToString()}) → $result")
        result
    }
    
    fun currentPackageIs(pkg: String) = DetectPredicate { device ->
        val current = device.currentPackageName
        val result = current == pkg
        AkiLog.d(LogTag.DETECT, "pkg($pkg) → $result (actual: $current)")
        result
    }
}
