import cv2
import os

cam = cv2.VideoCapture(0)
cam.set(3, 640)
cam.set(4, 480)

face_detector = cv2.CascadeClassifier('haarcascade_frontalface_default.xml')
face_id = input('\n Nhập ID khuôn mặt <Enter>: ')
print("\n [INFO] Khởi tạo camera ...")
count = 0

while True:
    ret, img = cam.read() #ret chỉ ra đọc dữ liệu có thành công hay không

    # Đảo ngược trục nếu hình ảnh bị ngược
    img = cv2.flip(img, 1)

    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    faces = face_detector.detectMultiScale(gray, 1.3, 5)

    for (x, y, w, h) in faces:
        cv2.rectangle(img, (x, y), (x + w, y + h), (255, 0, 0), 2)

        # Lưu toàn bộ khung hình chứa khuôn mặt
        captured_image = gray[y:y+h,x:x+w]

        count += 1
        cv2.imwrite("dataset/Desktop" + str(face_id) + '.' + str(count) + ".jpg", captured_image)
        cv2.imshow('Captured Image', captured_image)

    cv2.imshow('Webcam Feed', img)

    k = cv2.waitKey(100) & 0xff
    if k == 27:
        break
    elif count >= 30:
        break

print("\n [INFO] Thoát")
cam.release()
cv2.destroyAllWindows()
