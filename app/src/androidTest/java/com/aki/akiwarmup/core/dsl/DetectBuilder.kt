package com.aki.akiwarmup.core.dsl

class DetectBuilder {
    fun has(selector: Selector) = DetectPredicate { device ->
        selector.exists(device)
    }

    fun any(vararg predicates: DetectPredicate) = DetectPredicate { device ->
        predicates.any { it.evaluate(device) }
    }

    fun any(vararg selectors: Selector) = DetectPredicate { device ->
        selectors.map { has(it) }.any { it.evaluate(device) }
    }

    fun all(vararg predicates: DetectPredicate) = DetectPredicate { device ->
        predicates.all { it.evaluate(device) }
    }

    fun all(vararg selectors: Selector) = DetectPredicate { device ->
        selectors.map { has(it) }.all { it.evaluate(device) }
    }
    
    fun currentPackageIs(pkg: String) = DetectPredicate { device ->
        device.currentPackageName == pkg
    }
}
