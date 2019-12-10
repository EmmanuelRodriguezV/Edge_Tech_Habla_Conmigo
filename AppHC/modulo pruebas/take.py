import cv2
import sys
import os
import numpy as np
from PIL import Image
#cascPath = sys.argv[1]
#faceCascade = cv2.CascadeClassifier(cascPath)
nombre_de_letra = sys.argv[1]
video_capture = cv2.VideoCapture(0)
y = 40
x = 40
if not os.path.exists(nombre_de_letra):
    os.mkdir(nombre_de_letra)
height = 320
width = 300
framecount = 0
bandera = False
while True:
    # Capture frame-by-frame
    
    ret, frame = video_capture.read()
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    foto = Image.new("I",(width,height))
    datos = np.squeeze(np.asarray(frame[y:y+height ,x:x+width]))
    print (datos[0][0])
    print(len(datos))
    if cv2.waitKey(1) & 0xFF == ord('r'):
        print("Grabacion Activada")
        bandera = True
    if cv2.waitKey(1) & 0xFF == ord('s'):
        print("Grabacion pausada")
        bandera = False

    if bandera :
        foto = Image.fromarray(datos)
        foto.save(""+nombre_de_letra+"/"+nombre_de_letra+str(framecount)+".jpg")
        print("Guardando Imagen")
        foto.close()

    
    cv2.rectangle(frame, (x,y), (width , height), (255, 0, 0), 2)
    cv2.imshow('Video', frame)
    if cv2.waitKey(1) & 0xFF == ord('q'):
        print("Saliendo de la aplicacion")
        break
    
    framecount += 1 

# When everything is done, release the capture
video_capture.release()
cv2.destroyAllWindows()