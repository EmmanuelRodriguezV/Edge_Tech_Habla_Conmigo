# Copyright 2017 The TensorFlow Authors. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ==============================================================================

from __future__ import absolute_import
from __future__ import division
from __future__ import print_function
from email.mime.image import MIMEImage
from email.MIMEText import MIMEText
from email.MIMEMultipart import MIMEMultipart
import argparse
import sys
import time

import numpy as np
import tensorflow as tf
IMAGEN_INTRUSO =None
def load_graph(model_file):
  graph = tf.Graph()
  graph_def = tf.GraphDef()

  with open(model_file, "rb") as f:
    graph_def.ParseFromString(f.read())
  with graph.as_default():
    tf.import_graph_def(graph_def)

  return graph

def read_tensor_from_image_file(file_name, input_height=299, input_width=299,
				input_mean=0, input_std=255):
  input_name = "file_reader"
  output_name = "normalized"
  file_reader = tf.read_file(file_name, input_name)
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

if __name__ == "__main__":
  file_name = "tf_files/flower_photos/daisy/3475870145_685a19116d.jpg"
  model_file = "tf_files/retrained_graph.pb"
  label_file = "tf_files/retrained_labels.txt"
  input_height = 224
  input_width = 224
  input_mean = 128
  input_std = 128
  input_layer = "input"
  output_layer = "final_result"
  parser = argparse.ArgumentParser()
  parser.add_argument("--image", help="image to be processed")
  parser.add_argument("--graph", help="graph/model to be executed")
  parser.add_argument("--labels", help="name of file containing labels")
  parser.add_argument("--input_height", type=int, help="input height")
  parser.add_argument("--input_width", type=int, help="input width")
  parser.add_argument("--input_mean", type=int, help="input mean")
  parser.add_argument("--input_std", type=int, help="input std")
  parser.add_argument("--input_layer", help="name of input layer")
  parser.add_argument("--output_layer", help="name of output layer")
  args = parser.parse_args()

  if args.graph:
    model_file = args.graph
  if args.image:
    file_name = args.image
  if args.labels:
    label_file = args.labels
  if args.input_height:
    input_height = args.input_height
  if args.input_width:
    input_width = args.input_width
  if args.input_mean:
    input_mean = args.input_mean
  if args.input_std:
    input_std = args.input_std
  if args.input_layer:
    input_layer = args.input_layer
  if args.output_layer:
    output_layer = args.output_layer

  graph = load_graph(model_file)
  t = read_tensor_from_image_file(file_name,
                                  input_height=input_height,
                                  input_width=input_width,
                                  input_mean=input_mean,
                                  input_std=input_std)

  input_name = "import/" + input_layer
  output_name = "import/" + output_layer
  input_operation = graph.get_operation_by_name(input_name);
  output_operation = graph.get_operation_by_name(output_name);

  with tf.Session(graph=graph) as sess:

    start = time.time()
    results = sess.run(output_operation.outputs[0],
                      {input_operation.outputs[0]: t})
    end=time.time()
  results = np.squeeze(results)

  top_k = results.argsort()[-5:][::-1]
  labels = load_labels(label_file)
  import smtplib
  import threading
  import datetime

  #from email.mime.image import MIMEImage

  texto = "Alerta se ah encontrado una persona Desconocida a las "+str(datetime.datetime.now())
  CUERPO = texto
  SMTP_SERVER = "smtp-mail.outlook.com:587"
  DE = "ema-stuff@hotmail.com"
  HACIA="0ma_test@protonmail.ch"
  ASUNTO="Alerta Persona Desconocida!!!"
  MENSAJE = MIMEMultipart()
  MENSAJE.attach(MIMEText(CUERPO,'plain'))
  MENSAJE.attach(MIMEImage(IMAGEN_INTRUSO,"INTRUSO"))
  mensaje = MENSAJE.as_string(MENSAJE)
  print('\nEvaluation time (1-image): {:.3f}s\n'.format(end-start))
  desicion = False
  numero =None
  #from decimal import Decimal
  for i in top_k:
    if float(results[i])>.986:
        desicion = True
        numero =i
        print ("si entro")
    print(labels[i], results[i])
print (str(desicion))
from gtts import gTTS
if (desicion==True):
      #print "Se encontro en el grupo la persona es"+str(labels[numero])
      print("Persona encontrada")
      tts = gTTS(text='Acceso Autorizado Bienvenido '+labels[numero], lang='es')
      conector=smtplib.SMTP(SMTP_SERVER)
      conector.starttls()
      conector.login(DE,"starcraft123")
      MENSAJE=MIMEMultipart()
      CUERPO = "ah entrado "+labels[numero]+" al complejo"
      MENSAJE.attach(MIMEText(CUERPO,'plain'))
      mensaje = MENSAJE.as_string(MENSAJE)
      conector.sendmail(DE,HACIA,mensaje)
      conector.quit()
      tts.save("reaccion.mp3")

     # for i in top_k:
    #        print(labels[i], results[i])
else:
    print("No esta en el grupo la persona")
    tts = gTTS(text='Acceso  Denegado persona no Encontrada ', lang='es')
    print (IMAGEN_INTRUSO)
    conector = smtplib.SMTP(SMTP_SERVER)
    conector.starttls()
    conector.login(DE,"starcraft123")
    conector.sendmail(DE,HACIA,mensaje)
    conector.quit()
    print ("MENSAJE ENVIADO ALERTA")
tts.save("reaccion.mp3")

for i in top_k:
      print(labels[i], results[i])
import pygame as pg
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
        #print("Music file {} loaded!".format(music_file))
    except pg.error:
        #print("File {} not found! ({})".format(music_file, pg.get_error()))
        return
    pg.mixer.music.play()
    while pg.mixer.music.get_busy():

        clock.tick(30)

music_file = "reaccion.mp3"

volume = 0.8
play_music(music_file, volume)
