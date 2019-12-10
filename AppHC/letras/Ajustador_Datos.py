from PIL import Image,ImageFilter
import sys
import os
print "PREPARANDO DATOS"
FOTOS_RUTA = os.listdir(sys.argv[1])
#print "dame el nombre de la carpeta de fotos a usar "
#RUTA_IMAGENES =  raw_input()
#FOTOS = os.listdir(RUTA_IMAGENES)
#print "Dame un nombre para darle a la carpeta a usar "
NOMBRE = sys.argv[2]
#NOMBRE = raw_input()
if not os.path.exists(NOMBRE):
    os.system("mkdir "+NOMBRE)
#RUTA = "/root/Desktop/Proyecto/"+NOMBRE+"/"+NOMBRE
#RUTA = sys.argv[3]
#RUTA = "SENAS_NUEVAS/"+str(sys.argv[3])
RUTA = NOMBRE+"/"

for foto in FOTOS_RUTA:
    ORIGINAL2 = Image.open(sys.argv[1]+"/"+foto,"r")
    ORIGINAL = ORIGINAL2.convert("RGB")
    
    i =0
    while i< 360:
        GIRADO = ORIGINAL.rotate(i)
        GIRADO.save(RUTA+str(i)+"."+"jpg")
        blur = GIRADO.filter(ImageFilter.BLUR)
        blur.save(RUTA+str(i)+"blur"+"."+"jpg")
        sharp = GIRADO.filter(ImageFilter.SHARPEN)
        sharp.save(RUTA+str(i)+"sharp"+"."+"jpg")
        suavizado = GIRADO.filter(ImageFilter.SMOOTH_MORE)
        suavizado.save(RUTA+str(i)+"suave"+"."+"jpg")
        i+=1
        print str(i * 4 ) + " Fotos hecha de " +str(360 * 4)
    #print " script terminado"
