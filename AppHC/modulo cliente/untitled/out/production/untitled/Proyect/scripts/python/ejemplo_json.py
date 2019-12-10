from flask import Flask
from flask import jsonify
app = Flask(__name__)
@app.route('/')
def hello_world():
    arreglito = []
    objeto = {"nombre" :"emmanuel","apellido":"Rodriguez","ncontrol":15130743}

    for i in range (10):
        arreglito.append(objeto)
    return jsonify(
        arr = arreglito
    )
if __name__ == '__main__':
    print ("SERVIDOR WEB CORRIENDO")
    app.run()

