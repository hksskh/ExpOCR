from django.shortcuts import render
from django.http import HttpResponse
from django.core import serializers
from django.views.decorators.csrf import csrf_exempt
from models import User
import json

# Create your views here.

@csrf_exempt
def expocr_user_count_edu_user(request):
    data = {}
    data['user_count'] = User.count_edu_user()
    response = HttpResponse(json.dumps(data), content_type="application/json")
    return response

@csrf_exempt
def expocr_user_get_gmail_user(request):
    data = serializers.serialize('json', User.get_gmail_user(), fields=('U_Name', 'Email'))
    response = HttpResponse(data, content_type="application/json")
    return response

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
def expocr_user_update_name(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    email = params.get('email')
    password = params.get('password')
    username = params.get('username')
    data = {}
    result = User.update_user_name(email, password, username)
    data['updated rows'] = result
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
    data = {}
    user = User.create_user(username, email, password)
    data['id'] = user.U_Id
    data['name'] = user.U_Name
    data['email'] = user.Email
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