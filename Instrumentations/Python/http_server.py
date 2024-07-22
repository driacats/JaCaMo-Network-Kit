from flask import Flask, request, jsonify

app = Flask(__name__)

@app.route('/', methods=['GET', 'POST'])
def handle_request():
    if request.method == 'GET':
        return "Hello, World!"
    elif request.method == 'POST':
        # data = request.json  # Se stai inviando JSON
        # data = request.form  # Se stai inviando form data
        data = request.data.decode('utf-8')  # Se stai inviando dati raw
        return jsonify({"message": "Received POST request", "data": data})

if __name__ == '__main__':
    app.run(port=8080)