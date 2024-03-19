import cv2
import numpy as np
from PIL import Image
import os
import re

path = 'dataset'

recognizer = cv2.face.LBPHFaceRecognizer_create()
detector = cv2.CascadeClassifier("haarcascade_frontalface_default.xml")

def getImagesAndLabels(path):
    imagePaths = [os.path.join(path, f) for f in os.listdir(path)]
    faceSamples = []
    ids = []

    for imagePath in imagePaths:
        PIL_img = Image.open(imagePath).convert('L')
        img_numpy = np.array(PIL_img, 'uint8')

        # Sử dụng regex để lấy phần số từ tên file
        match = re.search(r'\d+', os.path.splitext(os.path.basename(imagePath))[0])
        if match:
            id = int(match.group())
            faces = detector.detectMultiScale(img_numpy)

            for (x, y, w, h) in faces:
                faceSamples.append(img_numpy[y:y + h, x:x + w])
                ids.append(id)

    return faceSamples, ids

print("\n [INFO] Đang training dữ liệu ...")
faces, ids = getImagesAndLabels(path)
recognizer.train(faces, np.array(ids))

# Lưu model đã train
recognizer.write('trainer/trainer.yml')

print("\n [INFO] {0} khuôn mặt được train. Thoát".format(len(np.unique(ids))))
