
# Required Imports
import os
from flask import Flask, request, jsonify, render_template
from firebase_admin import credentials, firestore, initialize_app

# Initialize Flask App
app = Flask(__name__)

# Initialize Firestore DB
cred = credentials.Certificate('key.json')
default_app = initialize_app(cred)
db = firestore.client()


CENTRAL = db.collection('central')
FIRE = db.collection('fire_department')
HOSPITAL = db.collection('hospital_department')
POLICE = db.collection('police_department')
TRAFFIC = db.collection('traffic_department')



# CENTRAL = db.collection('central')
# print(type(CENTRAL))
# documents = CENTRAL.list_documents()
# print(type(documents))
# for doc in documents:
#     print(type(doc))
    # print(u'{} => {}'.format(doc.id, doc.get().to_dict()['timestamp']))

# FIRE = db.collection('fire_department')
# temp  = FIRE.document('test3')
# print(temp.id)
# documents_fire = FIRE.list_documents()
# print(documents_fire)


# Create a callback on_fire_snapshot function to capture changes on 'FIRE' reports
def on_fire_snapshot(documents_central, changes, read_time):
    docs = dict()
    for doc in documents_central:
        # print(doc.id,doc.to_dict())
        docs[doc.id] =  doc.to_dict()
        if docs[doc.id]['department'] == 'FIRE':
            FIRE.document(doc.id).set(docs[doc.id])
            HOSPITAL.document(doc.id).set(docs[doc.id])
            POLICE.document(doc.id).set(docs[doc.id])
            TRAFFIC.document(doc.id).set(docs[doc.id])

col_query_fire = db.collection('central').where(u'department', u'==', u'FIRE')
# # Watch the collection query
query_watch_fire = col_query_fire.on_snapshot(on_fire_snapshot)


# Create a callback on_accident_snapshot function to capture changes on 'FIRE' reports
def on_accident_snapshot(documents_central, changes, read_time):
    docs = dict()
    print("\n\nACCIDENTS!!\n\n")
    for doc in documents_central:
        docs[doc.id] =  doc.to_dict()
        # print(docs)
        # print(doc.id)
        if docs[doc.id]['department'] == 'ACCIDENT' and docs[doc.id]['status'] == 'OPEN':
            HOSPITAL.document(doc.id).set(docs[doc.id])
            POLICE.document(doc.id).set(docs[doc.id])            
            TRAFFIC.document(doc.id).set(docs[doc.id])

col_query_accident = db.collection('central').where(u'department', u'==', u'ACCIDENT')
# # Watch the collection query
query_watch_accident = col_query_accident.on_snapshot(on_accident_snapshot)


@app.route('/', methods = ['GET'])
def index():
    return render_template('index.html')




@app.route('/central', methods = ['GET'])
def get_central_data():

    documents_central = CENTRAL.list_documents()
    documents_fire = FIRE.list_documents()
    documents_hospital = HOSPITAL.list_documents()

    docs = dict()
    for doc in documents_central:
        docs[doc.id] =  doc.get().to_dict()
        # if docs[doc.id]['department'] == 'FIRE':
        #     FIRE.document(doc.id).set(docs[doc.id])
        #     HOSPITAL.document(doc.id).set(docs[doc.id])
        #     POLICE.document(doc.id).set(docs[doc.id])
        #     TRAFFIC.document(doc.id).set(docs[doc.id])

        # if docs[doc.id]['department'] == 'ACCIDENT':
        #     HOSPITAL.document(doc.id).set(docs[doc.id])
        #     POLICE.document(doc.id).set(docs[doc.id])            
        #     TRAFFIC.document(doc.id).set(docs[doc.id])
            
    # print(docs)
    
    return render_template('central.html', docs = docs)


@app.route('/central/geotag', methods = ['GET'])
def get_geotag_data():
    pass

@app.route('/central/fire', methods = ['GET'])
def get_fire_data():
    pass

@app.route('/central/hospital', methods = ['GET'])
def get_hospital_data():
    pass

if __name__ == "__main__":
    app.run(debug = True)