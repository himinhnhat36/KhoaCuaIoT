import cv2
import numpy as np
import os

import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

cred = credentials.Certificate("firebasetest.json")

firebase_admin.initialize_app(cred, {'databaseURL': 'https://test01-d8bc9-default-rtdb.asia-southeast1.firebasedatabase.app'})

ref = db.reference('py/')

# Khởi tạo bộ nhận diện khuôn mặt và đọc mô hình đã được huấn luyện
recognizer = cv2.face.LBPHFaceRecognizer_create()
recognizer.read('trainer/trainer.yml')

# Đường dẫn tới tệp mô tả khuôn mặt của OpenCV
cascadePath = "haarcascade_frontalface_default.xml"
faceCascade = cv2.CascadeClassifier(cascadePath)

# Font để hiển thị thông tin trên video
font = cv2.FONT_HERSHEY_COMPLEX

names =['Nhat', 'Tran Quynh', 'Dung', 'Diem Quynh', 'Lisa']

# Khởi tạo Camera và thiết lập kích thước khung hình
cam = cv2.VideoCapture(0)
cam.set(3, 640)
cam.set(4, 480)

# Thiết lập kích thước tối thiểu của khuôn mặt để nhận diện
minW = 0.1 * cam.get(3)
minH = 0.1 * cam.get(4)

while True:
    # Đọc khung hình từ camera
    ret, img = cam.read()
    img = cv2.flip(img, 1)

    # Chuyển ảnh sang ảnh đen trắng (ảnh xám)
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

    # Phát hiện khuôn mặt trong khung hình
    faces = faceCascade.detectMultiScale(gray, scaleFactor=1.2, minNeighbors=5, minSize=(int(minW), int(minH)))

    for (x, y, w, h) in faces:
        # Vẽ hình chữ nhật xung quanh khuôn mặt
        cv2.rectangle(img, (x, y), (x + w, y + h), (0, 255, 0), 2)

        # Dự đoán ID và độ chắc chắn của khuôn mặt
        id, confidence = recognizer.predict(gray[y:y + h, x:x + w])

        # Kiểm tra xem ID có hợp lệ trong danh sách tên không
        if confidence < 100:
            face_id = id
            if face_id < len(names):  # Kiểm tra xem face_id có nằm trong phạm vi của danh sách tên không
                name = names[face_id]
                send_data = 'exact face'  # 1 chỉ định một khuôn mặt được nhận diện
            else:
                name = "Unknown"
                send_data = 'Inaccurate face'  # 0 chỉ định một khuôn mặt không xác định

            # Cập nhật dữ liệu trên Firebase
            ref.update({'name': name, 'confidence': confidence, 'send_data': send_data})
            id = name
        else:
            id = "Unknown"
            send_data = 'Inaccurate face'
            ref.update({'name': id, 'confidence': confidence, 'send_data': send_data})

        confidence = "  {0}%".format(round(100 - confidence))
        cv2.putText(img, str(id), (x + 5, y - 5), font, 1, (255, 255, 255), 2)
        cv2.putText(img, str(confidence), (x + 5, y + h - 5), font, 1, (255, 255, 0), 1)

    # Resize ảnh và hiển thị video
    img = cv2.resize(img, (640, 480))
    cv2.imshow('Face Recognition', img)

    # Chờ và kiểm tra nút thoát (ESC)
    k = cv2.waitKey(10) & 0xff
    if k == 27:
        break

print("\n [INFO] Exiting")
cam.release()
cv2.destroyAllWindows()
