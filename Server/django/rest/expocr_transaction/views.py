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
def expocr_get_all_transactions(request):
    data = serializers.serialize('json', Transaction.get_all_transactions())
    response = HttpResponse(data, content_type="application/json")
    return response


@csrf_exempt
def expocr_transaction_count_sender(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    sender_id = params.get('sender_id')
    data = {}
    data['sender_count'] = Transaction.count_sender_transaction(sender_id)
    response = HttpResponse(json.dumps(data), content_type="application/json")
    return response


@csrf_exempt
def expocr_transaction_get_entertain(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    sender_id = params.get('sender_id')
    data = serializers.serialize('json', Transaction.get_entertain_transaction(sender_id),
                                 fields=('Receiver_Id', 'Memo', 'Amount', 'Date'))
    response = HttpResponse(data, content_type="application/json")
    return response


@csrf_exempt
def expocr_transaction_get_by_sender_id(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    sender_id = params.get('sender_id')
    data_list = []
    id_list = []
    result = Transaction.get_transaction_by_sender_id(sender_id)
    for entry in result:
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
def expocr_transaction_get_all_receivers(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    sender_id = params.get('sender_id')
    data_list = []
    id_list = []
    result = Transaction.get_all_receivers(sender_id)
    for entry in result:
        data = {}
        data['receiver_id'] = int(entry['Receiver_Id'])
        id_list.append(data['receiver_id'])
        data['balance'] = float(entry['Amount__sum'])
        data_list.append(data)
    receiver_bulk = User.manager.in_bulk(id_list)
    index = 0
    while index < len(id_list):
        pk = id_list[index]
        data_list[index]['receiver_name'] = receiver_bulk[pk].U_Name
        data_list[index]['receiver_email'] = receiver_bulk[pk].Email
        index += 1
    response = HttpResponse(json.dumps(data_list), content_type="application/json")
    return response


@csrf_exempt
def expocr_transaction_get_between(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    sender_id = params.get('sender_id')
    receiver_id = params.get('receiver_id')
    result = Transaction.get_transaction_between(sender_id, receiver_id)
    data_list = []
    for entry in result:
        data = {'category': entry['Category'], 'memo': entry['Memo'], 'amount': float(entry['Amount']),
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
def expocr_transaction_update_memo(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    t_id = params.get('t_id')
    memo = params.get('memo')
    data = {}
    result = Transaction.update_Memo(t_id, memo)
    data['updated rows'] = result
    response = HttpResponse(json.dumps(data), content_type="application/json")
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
def expocr_transaction_delete_between(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    sender_id = params.get('sender_id')
    receiver_id = params.get('receiver_id')
    data = {}
    result = Transaction.delete_transaction_between(sender_id, receiver_id)
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
