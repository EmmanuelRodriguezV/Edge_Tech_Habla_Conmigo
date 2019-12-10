from flask import Flask
import numpy as np
import scipy.misc as smp
from flask import jsonify
import  ast
from random import randint
import re
from scripts.label_image import resultado
from PIL import Image
from flask import request
from gtts import gTTS
import pygame as pg
import time
import tensorflow as tf
import json
import cv2

app = Flask(__name__)
regex = r"[0-9]{1,3}"
file_name = "prueba.jpg"
model_file = "retrained_graph.pb"
iterador = 0
input_layer = "input"
output_layer = "final_result"
label_file = "retrained_labels.txt"
input_height = 224
input_width = 224
input_mean = 128
input_std = 128
@app.route('/get_cameras')
def get_camerasHandler():
    index = 0
    arr = []
    while True:
        cap = cv2.VideoCapture(index)
        if not cap.read()[0]:
            break
        else:
            arr.append(index)
        cap.release()
        index += 1
    return jsonify(
        numero_camaras=arr
    )


@app.route('/', methods=['GET', 'POST'])
def ejemplo():
    height = 0
    width = 0
    buffersito = []
    if request.method == "POST":
        try:

            camara = cv2.VideoCapture(int(request.form.get('camara')))
            for x in range(3):
                check, frame = camara.read()
                width = camara.get(3)  # float
                height = camara.get(4)
                buffersito.append(frame.tolist())
        finally:
            camara.release()
            return jsonify(bitmap=frame.tolist(), alto=height, ancho=width, bitmap_array=buffersito)


def read_tensor_from_image_file(file_name, input_height=299, input_width=299,
				input_mean=0, input_std=255):
  input_name = "file_reader"
  output_name = "normalized"
  file_reader = tf.io.read_file(file_name, input_name)

  if file_name.endswith(".png"):
    image_reader = tf.image.decode_png(file_reader, channels = 3,
                                       name='png_reader')
    IMAGEN_INTRUSO = image_reader
  elif file_name.endswith(".gif"):
    image_reader = tf.squeeze(tf.image.decode_gif(file_reader,
                                                  name='gif_reader'))
    IMAGEN_INTRUSO = image_reader
  elif file_name.endswith(".bmp"):
    image_reader = tf.image.decode_bmp(file_reader, name='bmp_reader')
    IMAGEN_INTRUSO = image_reader
  else:
    image_reader = tf.image.decode_jpeg(file_reader, channels = 3,
                                        name='jpeg_reader')
    IMAGEN_INTRUSO = image_reader
  float_caster = tf.cast(image_reader, tf.float32)
  dims_expander = tf.expand_dims(float_caster, 0);
  resized = tf.image.resize_bilinear(dims_expander, [input_height, input_width])
  normalized = tf.divide(tf.subtract(resized, [input_mean]), [input_std])
  sess = tf.Session()
  result = sess.run(normalized)

  return result
def load_labels(label_file):
  label = []
  proto_as_ascii_lines = tf.gfile.GFile(label_file).readlines()
  for l in proto_as_ascii_lines:
    label.append(l.rstrip())
  return label

def load_graph(model_file):
  graph = tf.Graph()
  graph_def = tf.GraphDef()
  with open(model_file, "rb") as f:
    graph_def.ParseFromString(f.read())
  with graph.as_default():
    tf.import_graph_def(graph_def)

  return graph

def play_music(music_file, volume=0.8):
    '''
    stream music with mixer.music module in a blocking manner
    this will stream the sound from disk while playing
    '''
    freq = 25000     # audio CD quality
    bitsize = -16    # unsigned 16 bit
    channels = 2     # 1 is mono, 2 is stereo
    buffer = 2048    # number of samples (experiment to get best sound)
    pg.mixer.init(freq, bitsize, channels, buffer)

    pg.mixer.music.set_volume(volume)
    clock = pg.time.Clock()
    try:
        pg.mixer.music.load(music_file)
        print("Music file {} loaded!".format(music_file))
    except pg.error:
        #print("File {} not found! ({})".format(music_file, pg.get_error()))
        return
    pg.mixer.music.play()
    while pg.mixer.music.get_busy():
        clock.tick(30)

@app.route('/playsound',methods=['POST'])
def handlerplaysound():
    if request.method =='POST':
        text_to_sound = request.form.get('text')
        tts = gTTS(text=text_to_sound,lang="es")
        tts.save("mensaje.mp3")
        play_music("mensaje.mp3",0.8)
        return "mensaje activado"
@app.route('/ejemplo2',methods=['POST'])
def ejemplo2Handler():
    encoded_data = request.form.get('BaseString')
    nparr = np.fromstring(encoded_data.decode('base64'), np.uint8)
    img = cv2.imdecode(nparr, cv2.IMREAD_ANYCOLOR)
    faceCascade = cv2.CascadeClassifier("cascade.xml")
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    faces = faceCascade.detectMultiScale(
        gray,
        scaleFactor=1.1,
        minNeighbors=5,
        minSize=(30, 30)
    )
    # Draw a rectangle around the faces
    RESPUESTA  = "no se encontro una mano en la foto"
    for (x, y, w, h) in faces:
        RESPUESTA = "SE ENCONTRO UNA MANO EN LA FOTO"
        datos = np.squeeze(np.asarray(img[y:y + h, x:x + w]))
        foto = Image.fromarray(datos)
        foto.save("prueba"+".jpg")
        foto.close()
        letra =    resultado()
        if letra == "no":
            return "$"
        else:
            return  letra
    return "$"

@app.route('/getImage', methods=['GET', 'POST', 'DELETE', 'PATCH'])
def getImageHandler():
    if request.method == 'POST':
        indice_camara = request.form.get('camara')
        print("hola")
        webcam = cv2.VideoCapture(indice_camara)
        check, frame = webcam.read()
        webcam.release()
        print (frame)
        return jsonify(foto=frame)
if __name__ == '__main__':
    app.run()