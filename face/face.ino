#include <Firebase_ESP_Client.h>
#include "addons/TokenHelper.h"
#include "addons/RTDBHelper.h"
#include <WiFi.h>
#include <TaskScheduler.h>

#define API_KEY "AIzaSyA6LWifvshwEBTiIA7jicMeqj6lsTTjsjc"
#define USER_EMAIL "Nhatesp32test@gmail.com"
#define USER_PASSWORD "Nmn0397712669"
#define DATABASE_URL "https://test01-d8bc9-default-rtdb.asia-southeast1.firebasedatabase.app/"

const char* ssid = "Color";
const char* password = "ngaysinhmay";

FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;
const int LED_PIN = 2;
Scheduler runner;

// Define tasks for Firebase and BlinkLED
void TaskFirebase(void* pvParameters);
// void TaskBlinkLED(void *pvParameters);
void TaskFirebaseData(void* pvParameters);

// Task handles
TaskHandle_t firebase_task_handle;
// TaskHandle_t blink_led_task_handle;
TaskHandle_t firebase_data_task_handle;

void initializeFirebase() {
  config.api_key = API_KEY;
  auth.user.email = USER_EMAIL;
  auth.user.password = USER_PASSWORD;
  config.database_url = DATABASE_URL;
  Firebase.reconnectWiFi(true);
  fbdo.setResponseSize(4096);
  config.token_status_callback = tokenStatusCallback;
  config.max_token_generation_retry = 5;
  Firebase.begin(&config, &auth);
}

void sendString(String path, String value) {
  if (Firebase.RTDB.setString(&fbdo, path.c_str(), value)) {
    Serial.print("Writing value: ");
    Serial.print(value);
    Serial.print(" on the following path: ");
    Serial.println(path);
    Serial.println("PASSED");
    Serial.println("PATH: " + fbdo.dataPath());
    Serial.println("TYPE: " + fbdo.dataType());
  } else {
    if (fbdo.errorReason() == "path not exist") {
      // Try creating the path
      if (Firebase.RTDB.setString(&fbdo, path.c_str(), "")) {
        Serial.println("Path created. Retry writing value.");
        sendString(path, value);  // Retry writing after creating the path
      } else {
        Serial.println("Failed to create path.");
      }
    } else {
      Serial.println("FAILED");
      Serial.println("REASON: " + fbdo.errorReason());
    }
  }
}
void faceDatabase() {
  if (Firebase.RTDB.getString(&fbdo, "py/send_data")) {
    if (fbdo.dataAvailable()) {
      Serial.print("Received Reset Command: ");
      Serial.println(fbdo.stringData());

      String resetCommand = fbdo.stringData();
      if (resetCommand == "Inaccurate face") {
        Serial.println("Normal operation...");
        // digitalWrite(LED_PIN, LOW);
      } else if (resetCommand == "exact face") {
        Serial.println("Performing reset...");
        // digitalWrite(LED_PIN, HIGH);
        sendString("py/send_data", "Inaccurate face");
      } else {
        // Xử lý trường hợp khác nếu cần
      }
    }
  } else {
    Serial.println("Failed to get Reset Command");
    Serial.println("REASON: " + fbdo.errorReason());
  }
}
// void blinkLED() {
//   digitalWrite(LED_PIN, HIGH);
//   delay(5000);
//   digitalWrite(LED_PIN, LOW);
//   delay(5000);
// }

void receiveDataFromFirebase() {
  if (Firebase.RTDB.getString(&fbdo, "/ESP32/OUTPUT/Digital/Reset")) {
    if (fbdo.dataAvailable()) {
      Serial.print("Received Reset Command: ");
      Serial.println(fbdo.stringData());
      String resetCommand = fbdo.stringData();
      if (resetCommand == "Off") {
        Serial.println("Normal operation...");
      } else if (resetCommand == "On") {
        Serial.println("Normal operation1...");
      } else {
        // Xử lý trường hợp khác nếu cần
      }
    }
  } else {
    Serial.println("Failed to get Reset Command");
    Serial.println("REASON: " + fbdo.errorReason());
  }
}

void setup() {
  Serial.begin(115200);
  pinMode(LED_PIN, OUTPUT);
  Serial.print("Connecting to ");
  Serial.println(ssid);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(5000);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected.");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());

  // Initialize Firebase
  initializeFirebase();

  // Initialize tasks
  xTaskCreatePinnedToCore(
    TaskFirebase,
    "Firebase Task",
    8192,
    NULL,
    1,
    &firebase_task_handle,
    ARDUINO_RUNNING_CORE);

  // xTaskCreatePinnedToCore(
  //   TaskBlinkLED,
  //   "Blink LED Task",
  //   1024,
  //   NULL,
  //   2,
  //   &blink_led_task_handle,
  //   ARDUINO_RUNNING_CORE
  // );
  xTaskCreatePinnedToCore(
    TaskFirebaseData,
    "Firebase Data Task",
    8192,
    NULL,
    2,
    &firebase_data_task_handle,
    ARDUINO_RUNNING_CORE);
  xTaskCreatePinnedToCore(
    TaskDeleteLocalData,
    "Delete Local Data Task",
    8192,
    NULL,
    2,
    NULL,
    ARDUINO_RUNNING_CORE);
}

void loop() {
  // Empty, since tasks are managed by FreeRTOS
  // receiveDataFromFirebase();
  delay(1000);
}

void TaskFirebase(void* pvParameters) {
  for (;;) {
    Serial.print("taskFirebase :");
    Serial.println(xPortGetCoreID());
    faceDatabase();                   // Call your function to interact with Firebase
    vTaskDelay(pdMS_TO_TICKS(1000));  // Delay 1 second
  }
}

// void TaskBlinkLED(void *pvParameters) {
//   for (;;) {
//     Serial.print("taskLed :");
//     Serial.println(xPortGetCoreID());
//     blinkLED(); // Call your function to blink LED
//     vTaskDelay(pdMS_TO_TICKS(5000)); // Delay 5 seconds
//   }
// }
void TaskFirebaseData(void* pvParameters) {
  for (;;) {
    Serial.print("taskData :");
    Serial.println(xPortGetCoreID());
    receiveDataFromFirebase();        // Call your function to receive data from Firebase
    vTaskDelay(pdMS_TO_TICKS(5000));  // Delay 1 second
  }
}

// Cấu trúc để lưu trữ dữ liệu cần xóa
struct LocalData {
  int* ptr;
};

// Hàm xóa dữ liệu cục bộ
void deleteLocalData(void* pvParameters) {
  // Ép kiểu con trỏ pvParameters về kiểu LocalData
  LocalData* data = (LocalData*)pvParameters;

  // Kiểm tra xem dữ liệu có hợp lệ không
  if (data != nullptr && data->ptr != nullptr) {
    // Giải phóng bộ nhớ đã cấp phát
    delete data->ptr;
    data->ptr = nullptr;

    // Hiển thị thông báo sau khi giải phóng bộ nhớ
    Serial.println("Memory freed successfully.");
  }
}


void TaskDeleteLocalData(void* pvParameters) {
  for (;;) {
    Serial.print("Task Delete Local Data - Core ID: ");
    Serial.println(xPortGetCoreID());
    deleteLocalData(pvParameters);
    vTaskDelay(pdMS_TO_TICKS(60000));  // Delay 1 phút (60.000 ms)
  }
}