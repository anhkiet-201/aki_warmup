package com.aki.akiwarmup.core.dsl

import androidx.test.uiautomator.By

class DetectBuilder {
    fun hasResourceId(id: String) = DetectPredicate { device ->
        device.findObject(By.res(id)) != null
    }
    
    fun hasContentDesc(desc: String) = DetectPredicate { device ->
        device.findObject(By.desc(desc)) != null
    }
    
    fun hasText(text: String) = DetectPredicate { device ->
        device.findObject(By.text(text)) != null
    }
    
    fun currentPackageIs(pkg: String) = DetectPredicate { device ->
        device.currentPackageName == pkg
    }
}
