from __future__ import print_function
from django.shortcuts import render
from django.http import HttpResponse
from django.core import serializers
from django.views.decorators.csrf import csrf_exempt
from models import Transaction
from cvapi import CVAPI
from expocr_user.models import User
from PIL import Image
import json
import io
import os


# Create your views here.


@csrf_exempt
def expocr_transaction_get_by_sender_id(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    sender_id = params.get('sender_id')
    category = params.get('category', None)
    no_payment = (category == 'no_payment')
    data_list = []
    id_list = []
    result = Transaction.get_transaction_by_sender_id(sender_id)
    for entry in result:
        if no_payment and str(entry['Category']) == 'Payment':
            continue
        data = {}
        data['receiver_id'] = int(entry['Receiver_Id'])
        id_list.append(data['receiver_id'])
        data['amount'] = float(entry['Amount'])
        data['date'] = str(entry['Date'])
        data['Category'] = str(entry['Category'])
        data_list.append(data)
    receiver_bulk = User.manager.in_bulk(id_list)
    index = 0
    while index < len(id_list):
        pk = id_list[index]
        data_list[index]['receiver_name'] = receiver_bulk[pk].U_Name
        index += 1
    response = HttpResponse(json.dumps(data_list), content_type="application/json")
    return response

@csrf_exempt
def expocr_transaction_get_by_receiver_id(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    receiver_id = params.get('receiver_id')
    category = params.get('category', None)
    no_payment = (category == 'no_payment')
    data_list = []
    id_list = []
    result = Transaction.get_transaction_by_receiver_id(receiver_id)
    for entry in result:
        if no_payment and str(entry['Category']) == 'Payment':
            continue
        data = {}
        data['sender_id'] = int(entry['Sender_Id'])
        id_list.append(data['sender_id'])
        data['amount'] = float(entry['Amount'])
        data['date'] = str(entry['Date'])
        data['Category'] = str(entry['Category'])
        data_list.append(data)
    sender_bulk = User.manager.in_bulk(id_list)
    index = 0
    while index < len(id_list):
        pk = id_list[index]
        data_list[index]['sender_name'] = sender_bulk[pk].U_Name
        index += 1
    response = HttpResponse(json.dumps(data_list), content_type="application/json")
    return response

@csrf_exempt
def expocr_transaction_get_by_uid(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    uid = params.get('U_Id')
    data_list = []
    id_list = []
    user = User.get_user_by_id(uid)
    username = "You"
    # username = user['U_Name']
    # result = Transaction.get_transaction_by_sender_id(uid)

    # result = Transaction.get_transaction_by_receiver_id(uid)

    result = Transaction.get_transaction_by_id(uid)

    for entry in result:
        data = {}
        if entry['Sender_Id'] == int(uid):
            print('in sender_id')
            other_user = User.get_user_by_id(int(entry['Receiver_Id']))
            other_name_set = other_user.values('U_Name')
            for val in other_name_set:
                other_name = val['U_Name']
            data['who_paid'] = other_name
            data['whom'] = username
            data['amount'] = float(entry['Amount'])
        else:
            other_user = User.get_user_by_id(int(entry['Sender_Id']))
            other_name_set = other_user.values('U_Name')
            for val in other_name_set:
                other_name = val['U_Name']
            data['who_paid'] = username
            data['whom'] = other_name
            data['amount'] = -float(entry['Amount'])
        data['date'] = str(entry['Date'])
        data['Category'] = str(entry['Category'])
        data_list.append(data)
    print(data_list)
    response = HttpResponse(json.dumps(data_list), content_type="application/json")
    return response


@csrf_exempt
def expocr_transaction_get_all_friends(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    user_id = params.get('user_id')
    data_list = []
    id_list = []
    result = Transaction.get_all_friends_sender(user_id)
    for entry in result:
        data = {}
        data['friend_id'] = int(entry['Receiver_Id'])
        id_list.append(data['friend_id'])
        data['balance'] = -float(entry['Amount__sum'])
        data_list.append(data)
    result = Transaction.get_all_friends_receiver(user_id)
    for entry in result:
        data = {}
        data['friend_id'] = int(entry['Sender_Id'])
        if data['friend_id'] not in id_list:
            id_list.append(data['friend_id'])
            data['balance'] = float(entry['Amount__sum'])
            data_list.append(data)
        else:
            index = id_list.index(data['friend_id'])
            data_list[index]['balance'] = data_list[index]['balance'] + float(entry['Amount__sum'])
    receiver_bulk = User.manager.in_bulk(id_list)
    index = 0
    while index < len(id_list):
        pk = id_list[index]
        data_list[index]['friend_name'] = receiver_bulk[pk].U_Name
        data_list[index]['friend_email'] = receiver_bulk[pk].Email
        index += 1
    response = HttpResponse(json.dumps(data_list), content_type="application/json")
    return response


@csrf_exempt
def expocr_transaction_get_between(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    uid = params.get('sender_id')
    friend_id = params.get('receiver_id')
    result = Transaction.get_transaction_between(uid, friend_id)
    data_list = []
    for entry in result:
        amount = float(entry['Amount'])
        if (int(entry['Sender_Id']) == int(uid)):
            amount = -amount
            print(amount)
        data = {'id': entry['T_Id'], 'category': entry['Category'], 'memo': entry['Memo'], 'amount': amount,
                'date': str(entry['Date'])}
        data_list.append(data)
    response = HttpResponse(json.dumps(data_list), content_type="application/json")
    return response


@csrf_exempt
def expocr_transaction_get_by_t_id(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    t_id = params.get('t_id')
    result = Transaction.get_transaction_by_t_id(t_id)
    data_list = []
    for entry in result:
        data = {'sender_id=': entry['Sender_Id'], 'receiver_id': entry['Receiver_Id'], 'category': entry['Category'],
                'memo': entry['Memo'], 'amount': float(entry['Amount']),
                'date': str(entry['Date'])}
        data_list.append(data)
    response = HttpResponse(json.dumps(data_list), content_type="application/json")
    return response



@csrf_exempt
def expocr_transaction_create(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    sender_id = params.get('sender_id')
    receiver_id = params.get('receiver_id')
    category = params.get('category')
    memo = params.get('memo')
    amount = params.get('amount')
    date = params.get('date')
    data = {}
    result = Transaction.create_transaction(sender_id, receiver_id, category, memo, amount, date)
    data['t_id'] = result.T_Id
    data['sender_id'] = result.Sender_Id
    data['receiver_id'] = result.Receiver_Id
    data['category'] = result.Category
    data['memo'] = result.Memo
    data['amount'] = result.Amount
    data['date'] = result.Date
    response = HttpResponse(json.dumps(data), content_type="application/json")
    return response


@csrf_exempt
def expocr_transaction_create_by_email(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    am_I_sender = params.get("am_I_sender")
    sender_id = params.get('sender_id')
    receiver_email = params.get('receiver_email')
    category = params.get('category')
    memo = params.get('memo')
    amount = params.get('amount')
    date = params.get('date')
    result = User.get_user_by_email(receiver_email)
    if result.count() == 0:
        data = {'warning': 'Friend email not exists'};
        response = HttpResponse(json.dumps(data), content_type="application/json")
        return response
    for entry in result:
        receiver_id = entry.U_Id
    if am_I_sender == "no":
        temp = receiver_id
        receiver_id = sender_id
        sender_id = temp
    result = Transaction.create_transaction(sender_id, receiver_id, category, memo, amount, date)
    data = {}
    data['t_id'] = result.T_Id
    data['sender_id'] = result.Sender_Id
    data['receiver_id'] = result.Receiver_Id
    data['category'] = result.Category
    data['memo'] = result.Memo
    data['amount'] = result.Amount
    data['date'] = result.Date
    response = HttpResponse(json.dumps(data), content_type="application/json")
    return response



@csrf_exempt
def expocr_transaction_delete_by_id(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    t_id = params.get('tid')
    data = {}
    result = Transaction.delete_transaction_by_id(t_id)
    data['deleted rows'] = result[0]
    data['deleted details'] = result[1]
    response = HttpResponse(json.dumps(data), content_type="application/json")
    return response


@csrf_exempt
def expocr_transaction_ocr_test(request):
    try:
        data = {}
        # print(request.META['CONTENT_LENGTH'])
        # print(request.method)
        body = request.body
        # print(len(body))

        image_index = body.index('image=')
        # print(image_index)
        image_string = body[image_index + len('image='):]
        print('image_string_length: ' + str(len(image_string)))

        image_jpg = Image.open(io.BytesIO(image_string))
        print('image size: ' + str(image_jpg.size))
        # image_jpg.show()

        result = CVAPI.send_image_on_disk(image_string)
        print('OCR return json string length: ' + str(len(json.dumps(result))))
        jsonArray = CVAPI.restore_receipt(result)

        data['receipt_sketch'] = jsonArray
    except Exception as e:
        print(repr(e))
        data['warning'] = 'Fail to retrieve receipt sketch'
    finally:
        response = HttpResponse(json.dumps(data), content_type="application/json")
    return response
