package com.aki.akiwarmup

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    // Sử dụng State để Compose tự động cập nhật lại UI khi giá trị thay đổi
    private val messageState = mutableStateOf("Chưa có nội dung từ ADB")

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Đọc dữ liệu từ Intent lúc Activity được tạo
        messageState.value = (intent.getStringExtra("message") ?: "Chưa có nội dung từ ADB").replace("_", " ")

        setContent {
            MaterialTheme {
                Scaffold(
                    topBar = {
                        LargeTopAppBar(
                            title = { Text("ADB Message Receiver") },
                            colors = TopAppBarDefaults.largeTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                titleContentColor = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(24.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Icon chuẩn Material 3
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Message",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(40.dp)
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))

                                // Tiêu đề phụ
                                Text(
                                    text = "Tin nhắn nhận được",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))

                                // Nội dung tin nhắn chính
                                Text(
                                    text = messageState.value,
                                    style = MaterialTheme.typography.headlineMedium,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Hàm này được gọi khi Activity đã chạy và nhận thêm một Intent mới
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // Cập nhật lại Intent của Activity
        
        // Cập nhật giá trị mới vào State để UI tự động vẽ lại
        messageState.value = (intent.getStringExtra("message") ?: "Chưa có nội dung từ ADB").replace("_", " ")
    }
}
