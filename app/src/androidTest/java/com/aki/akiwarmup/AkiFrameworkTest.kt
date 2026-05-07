package com.aki.akiwarmup

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.UiDevice
import com.aki.akiwarmup.core.config.AdbConfigBridge
import com.aki.akiwarmup.core.human.HumanBehaviorEngine
import com.aki.akiwarmup.core.logger.SessionLogger
import com.aki.akiwarmup.core.loop.ActionLoop
import com.aki.akiwarmup.core.scene.SceneRegistry
import com.aki.akiwarmup.core.screen.ScreenDetector
import com.aki.akiwarmup.scenes.TikTokWarmupScene
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AkiFrameworkTest {

    private lateinit var device: UiDevice
    private lateinit var logger: SessionLogger
    private lateinit var humanEngine: HumanBehaviorEngine

    @Before
    fun setup() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        device = UiDevice.getInstance(instrumentation)
        logger = SessionLogger()
        humanEngine = HumanBehaviorEngine()
        
        // Register all available scenes
        SceneRegistry.register(TikTokWarmupScene)
    }

    @Test
    fun runScene() = runBlocking {
        val runConfig = AdbConfigBridge.load()
        
        val scene = SceneRegistry.get(runConfig.sceneName)
        val detector = ScreenDetector(device, scene.screens)
        
        val loop = ActionLoop(
            device = device,
            scene = scene,
            detector = detector,
            humanEngine = humanEngine,
            logger = logger,
            runConfig = runConfig
        )

        try {
            // Ensure app is launched before starting loop
            launchApp(scene.config.targetPackage)
            
            // Start the main action loop
            loop.run()
        } finally {
            // Export report to Logcat
            logger.finalize()
        }
    }

    private fun launchApp(packageName: String) {
        if (packageName.isEmpty()) return
        val context = InstrumentationRegistry.getInstrumentation().context
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            // Wait for app to launch
            Thread.sleep(5000)
        }
    }
}
