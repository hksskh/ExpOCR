from __future__ import print_function
from django.shortcuts import render
from django.http import HttpResponse
from django.core import serializers
from django.core import signing
from django.core.mail import EmailMessage
from django.views.decorators.csrf import csrf_exempt
from models import User
from expocr_transaction.models import Transaction
import json
import random
import string

# Create your views here.

create_user_key = 'j8Pmnh23f443E2mi'


@csrf_exempt
def expocr_user_get_user_by_id(request):
    if request.method == 'GET':
        id = request.GET
    elif request.method == 'POST':
        id = request.POST
    id = id.get('id')
    data = serializers.serialize('json', User.get_user_by_id(id), fields=('U_Name', 'Email'))
    response = HttpResponse(data, content_type="application/json")
    return response


@csrf_exempt
def expocr_user_login_by_email(request):
    try:
        if request.method == 'GET':
            params = request.GET
        elif request.method == 'POST':
            params = request.POST
        email = params.get('email')
        password = params.get("password")
        result = User.login_by_email(email, password)
        data = {}
        if result[0]:
            for entry in result[1]:
                data['id'] = int(entry['U_Id'])
                data['name'] = entry['U_Name']
                data['email'] = entry['Email']
        else:
            data['warning'] = result[1]
        response = HttpResponse(json.dumps(data), content_type='application/json')
    except Exception as e:
        print('%s (%s)' % (e.message, type(e)))
    return response


@csrf_exempt
def expocr_user_check_vericode(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    email = params.get('email')
    vericode = params.get('vericode')
    result = User.check_vericode(email, vericode)
    data = {}
    if result[0]:
        data['email'] = result[1]
    else:
        data['warning'] = result[1]
    response = HttpResponse(json.dumps(data), content_type='application/json')
    return response


@csrf_exempt
def expocr_user_update_name(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    email = params.get('email')
    password = params.get('password')
    username = params.get('username')
    result = User.update_user_name(email, password, username)
    data = {'updated rows': result}
    response = HttpResponse(json.dumps(data), content_type="application/json")
    return response


@csrf_exempt
def expocr_user_try_create_user(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    username = params.get('username')
    email = params.get('email')
    password = params.get('password')
    data = {}
    user = User.try_create_user(username, email, password)
    if user[1] == 0:
        username_encrp = signing.dumps(username, create_user_key)
        email_encrp = signing.dumps(email, create_user_key)
        password_encrp = signing.dumps(password, create_user_key)
        activation = request.build_absolute_uri('/') \
                     + 'user/create?username=' + username_encrp + '&email=' + email_encrp + '&password=' + password_encrp
        content = 'Hi, this email has recently be used to sign up in ExpOCR.<br>' \
                  'Please activate your account via link:<br>' \
                  '<a href=' + activation + ' target="_blank">' + activation + '</a>'
        msg = EmailMessage(
            'Activate your account',
            content,
            'ExpOCR428@gmail.com',
            [email],
        )
        msg.content_subtype = 'html'
        ret = msg.send()
        data['email_sending_status'] = ret
        data['activate'] = user[0]
    else:
        data['warning'] = user[0]
    response = HttpResponse(json.dumps(data), content_type="application/json")
    return response


@csrf_exempt
def expocr_user_create_user(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    username = params.get('username')
    email = params.get('email')
    password = params.get('password')
    username_decrp = signing.loads(username, create_user_key)
    email_decrp = signing.loads(email, create_user_key)
    password_decrp = signing.loads(password, create_user_key)
    user = User.create_user(username_decrp, email_decrp, password_decrp)
    if user[1] == 0:
        data = {'u_id': user[0].U_Id, 'u_name': user[0].U_Name, 'email': user[0].Email}
    else:
        data = {'warning': user[0]}
    response = HttpResponse(json.dumps(data), content_type="application/json")
    return response


@csrf_exempt
def expocr_user_delete(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    username = params.get('username')
    email = params.get('email')
    password = params.get('password')
    data = {}
    result = User.delete_user(username, email, password)
    data['deleted rows'] = result[0]
    data['deleted details'] = result[1]
    response = HttpResponse(json.dumps(data), content_type="application/json")
    return response


@csrf_exempt
def expocr_user_send_vericode(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    email = params.get('email')
    vericode = ''.join(random.SystemRandom().choice(string.ascii_uppercase + string.digits) for _ in range(8))
    result = User.add_vericode(email, vericode)

    data = {}

    if result[1] == 0:
        data['warning'] = result[0]
    else:
        data['email'] = email
        data['vericode'] = vericode
        msg = EmailMessage('Your Verification Code from ExpOCR!',
                           'Hello, \nYour verification code is ' + vericode + '. Please enter it within 5 minutes.',
                           'ExpOCR428@gmail.com',
                           [email],
                           )
        msg.content_subtype = 'html'
        ret = msg.send()

        data['email sending status'] = ret

    response = HttpResponse(json.dumps(data), content_type='application/json')
    return response


@csrf_exempt
def expocr_user_change_password(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    email = params.get('email')
    password = params.get('password')
    result = User.change_user_password(email, password)
    data = {'updated rows': result}
    response = HttpResponse(json.dumps(data), content_type="application/json")
    return response

@csrf_exempt
def expocr_user_get_two_users_by_id(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    id1 = params.get('id1')
    id2 = params.get('id2')
    data = serializers.serialize('json', User.get_two_users_by_id(id1, id2), fields=('U_Id', 'U_Name', 'Email'))
    response = HttpResponse(data, content_type="application/json")
    return response

@csrf_exempt
def expocr_login_with_facebook(request):
    try:
        if request.method == 'GET':
            params = request.GET
        elif request.method == 'POST':
            params = request.POST
        email = params.get('email')
        result = User.login_with_facebook(email)
        data = {}
        if result[0]:
            for entry in result[1]:
                data['id'] = int(entry['U_Id'])
                data['name'] = entry['U_Name']
                data['email'] = entry['Email']
        else:
            data['warning'] = result[1]
        response = HttpResponse(json.dumps(data), content_type='application/json')
    except Exception as e:
        print('%s (%s)' % (e.message, type(e)))
    return response

@csrf_exempt
def expocr_create_facebook_user(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    username = params.get('username')
    email = params.get('email')
    user = User.create_facebook_user(username, email)
    if user[1] == 0:
        data = {'u_id': user[0].U_Id, 'u_name': user[0].U_Name, 'email': user[0].Email}
    else:
        data = {'warning': user[0]}
    response = HttpResponse(json.dumps(data), content_type="application/json")
    return response

@csrf_exempt
def expocr_user_delete_friend_by_id(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    u_id = params.get('u_id')
    my_u_id = params.get('my_u_id')
    data = {'deleted rows': 0, 'deleted details': ''}
    result = Transaction.delete_transaction_between(u_id, my_u_id)
    data['deleted rows'] += int(result[0])
    data['deleted details'] += str(result[1])
    result = Transaction.delete_transaction_between(my_u_id, u_id)
    data['deleted rows'] += int(result[0])
    data['deleted details'] += str(result[1])

    response = HttpResponse(json.dumps(data), content_type="application/json")
    return response

@csrf_exempt
def expocr_user_get_briefs(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    u_ids = params.get('u_ids')
    u_id_list = u_ids.split(',')
    print(u_id_list)

    receiver_bulk = User.manager.in_bulk(u_id_list)
    index = 0
    data_list = []
    while index < len(u_id_list):
        pk = int(u_id_list[index])
        data = {}
        data['u_id'] = int(receiver_bulk[pk].U_Id)
        data['u_name'] = str(receiver_bulk[pk].U_Name)
        data['email'] = str(receiver_bulk[pk].Email)
        data_list.append(data)
        index += 1

    response = HttpResponse(json.dumps(data_list), content_type="application/json")
    return response
