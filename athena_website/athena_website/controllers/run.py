#!/usr/bin/env python

import os
import csv

from athena_website import app
from flask import render_template, request, jsonify, flash

from . import athenahealthapi
from athena_website import _base
from . import FFT as fft

# OAuth information
key = r'6uqkebvamka8r5ra7u7w7hv4'
secret = 'hpHPdcP6agKSmBS'
version = 'preview1'
practiceid = 195900
api = athenahealthapi.APIConnection(version, key, secret, practiceid)

# Rendering the home page
@app.route("/", methods=["GET"])
def index():
    print('getting index')
    return render_template('bootstrap/index.html')

# Save the CSV data
def save_csv(timestamps):
    csv_path = os.path.join(_base, "static") + "/timestamps.csv" 
    csv_file = open(csv_path, "w")
    csv_file.write("timestamps,x,y,z\n")
    for timestamp in timestamps:
        csv_file.write(str(timestamp[0]) + "," + str(timestamp[1]) + ","  + str(timestamp[2]) + "," + str(timestamp[3]) + '\n')
    return

fell = False
number = 0
@app.route("/data", methods=["GET"])
def get_data():
    global number
    global fell
    data = []
    x = []
    y = []
    z = []
    csv_path = os.path.join(_base, "static") + "/timestamps.csv" 
    with open(csv_path) as csv_file:
        csv_dict = csv.DictReader(csv_file)
        for row in csv_dict:
            data.append({"x": row["x"], "y": row["y"], "z": row["z"]})
            x.append(row["x"])
            y.append(row["y"])
            z.append(row["z"])
        return jsonify({"data": data, "fell": fell, "number": number})

@app.route("/reset", methods=["POST"])
def post_reset():
    global fell
    fell = False
    return "OK", 200


# Collect post information
@app.route("/data", methods=["POST"])
def post_data():
    global fell
    global number
    # Read in the collected user info
    x = []
    y = []
    z = []
    timestamps = []
    username = None
    for data in request.json:
        if username is None:
            username = data["email"]
        timestamps.append((data["timestamp"], data["xVal"], data["yVal"], data["zVal"]))
        x.append(data["xVal"])
        y.append(data["yVal"])
        z.append(data["zVal"])

    print("Checking FFT")
    (fft_mag, mean, fft_sum) = fft.get_FFT(x, y, z)
    print("Got FFT")
    print(fft_sum)
    if(fft_sum > 1600):
        fell = True
        number += 1

    # Save the data to an CSV
    save_csv(timestamps)

    return "OK", 200

@app.route("/test")
def test():
    return render_template('graph.html')


# Rending the data page
@app.route("/graph", methods=["GET"])
def get_data3():
    print("Routing data")
    return render_template('/bootstrap/index.html')
    #return render_template('multi-line-full.html')

# Example D3 webpage
@app.route("/d3", methods=["GET"])
def get_d3():
    return render_template('d3.html', data=[1,2,3,4,5,6,7])

# Testing AthenaHealth
@app.route('/login', methods=["GET","POST"])
def get_athena():
    api._authenticate()
    res = api.GET('/practiceinfo')
    print(res)

# Populate a fake patient
@app.route('/populate', methods=["GET"])
def get_populate():
    api._authenticate()
    patient_info = {
    	'lastname': 'Foo',
    	'firstname': 'Jason',
    	'address1': '123 Any Street',
    	'city': 'Cambridge',
    	'countrycode3166': 'US',
    	'departmentid': 1,
    	'dob': '6/18/1987',
    	'language6392code': 'declined',
    	'maritalstatus': 'S',
    	'race': 'declined',
    	'sex': 'M',
    	'ssn': '*****1234',
    	'zip': '02139',
    }
    new_patient = api.POST('/patients', patient_info)
    return("Populated User")
