package com.aki.akiwarmup.core.dsl

import android.util.Log

class DetectBuilder {
    fun has(selector: Selector) = DetectPredicate { device ->
        val exists = selector.exists(device)
        Log.d("AkiFramework", "[Detect] has($selector) -> $exists")
        exists
    }

    fun any(vararg predicates: DetectPredicate) = DetectPredicate { device ->
        val result = predicates.any { it.evaluate(device) }
        Log.d("AkiFramework", "[Detect] any(...) -> $result")
        result
    }

    fun any(vararg selectors: Selector) = DetectPredicate { device ->
        val result = selectors.any { it.exists(device) }
        Log.d("AkiFramework", "[Detect] any(${selectors.joinToString()}) -> $result")
        result
    }

    fun all(vararg predicates: DetectPredicate) = DetectPredicate { device ->
        val result = predicates.all { it.evaluate(device) }
        Log.d("AkiFramework", "[Detect] all(...) -> $result")
        result
    }

    fun all(vararg selectors: Selector) = DetectPredicate { device ->
        val result = selectors.all { it.exists(device) }
        Log.d("AkiFramework", "[Detect] all(${selectors.joinToString()}) -> $result")
        result
    }
    
    fun currentPackageIs(pkg: String) = DetectPredicate { device ->
        val current = device.currentPackageName
        val result = current == pkg
        Log.d("AkiFramework", "[Detect] currentPackageIs($pkg) -> $result (actual: $current)")
        result
    }
}
