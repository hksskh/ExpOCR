from django.http import HttpResponse
from django.core import serializers
from django.views.decorators.csrf import csrf_exempt
from models import Group, Member, Group_Transaction
import json

# Create your views here.

@csrf_exempt
def expocr_group_create(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    name = params.get('name')
    result = Group.create_group(name)
    data = {}
    data['g_id'] = result.G_Id
    data['g_name'] = result.G_Name
    response = HttpResponse(json.dumps(data), content_type='application/json')
    return response

@csrf_exempt
def expocr_group_get_name(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    id = params.get('id')
    result = Group.get_group_name(id)
    data = serializers.serialize('json', result)
    response = HttpResponse(data, content_type='application/json')
    return response

@csrf_exempt
def expocr_group_update_name(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    id = params.get('id')
    name = params.get('name')
    result = Group.update_group_name(id, name)
    data = {}
    data['updated_rows'] = result
    response = HttpResponse(json.dumps(data), content_type='application/json')
    return response

@csrf_exempt
def expocr_group_delete(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    id = params.get('id')
    name = params.get('name')
    result = Group.delete_group(id, name)
    data = {}
    data['deleted rows'] = result[0]
    data['deleted details'] = result[1]
    response = HttpResponse(json.dumps(data), content_type='application/json')
    return response

@csrf_exempt
def expocr_group_add_member(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    g_id = params.get('g_id')
    u_id = params.get('u_id')
    result = Member.add_member(g_id, u_id)
    data_list = []
    if result[1] == 0:
        data = {}
        data['g_id'] = result[0].G_Id
        data['u_id'] = result[0].U_Id
        data_list.append(data)
    else:
        for entry in result[0]:
            data = {}
            data['g_id'] = int(entry['G_Id'])
            data['u_id'] = int(entry['U_Id'])
            data_list.append(data)
    response = HttpResponse(json.dumps(data_list), content_type='application/json')
    return response

@csrf_exempt
def expocr_group_get_members(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    id = params.get('g_id')
    result = Member.get_members(id)
    data_list = []
    for entry in result:
        data = {}
        data['u_id'] = int(entry['U_Id'])
        data_list.append(data)
    response = HttpResponse(json.dumps(data_list), content_type='application/json')
    return response

@csrf_exempt
def expocr_group_get_groups_by_member(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    id = params.get('u_id')
    result = Member.get_groups(id)
    data_list = []
    for entry in result:
        data = {}
        data['g_id'] = int(entry['G_Id'])
        data_list.append(data)
    response = HttpResponse(json.dumps(data_list), content_type='application/json')
    return response

@csrf_exempt
def expocr_group_delete_member(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    g_id = params.get('g_id')
    u_id = params.get('u_id')
    result = Member.delete_member(g_id, u_id)
    data = {}
    data['deleted rows'] = result[0]
    data['deleted details'] = result[1]
    response = HttpResponse(json.dumps(data), content_type='application/json')
    return response

@csrf_exempt
def expocr_group_add_transaction(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    g_id = params.get('g_id')
    t_id = params.get('t_id')
    result = Group_Transaction.add_transaction(g_id, t_id)
    if result[1] == 0:
        data = {}
        data['g_id'] = result[0].G_Id
        data['t_id'] = result[0].T_Id
        data = json.dumps(data)
    else:
        data = serializers.serialize('json', result[0])
    response = HttpResponse(data, content_type='application/json')
    return response

@csrf_exempt
def expocr_group_get_transactions(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    g_id = params.get('g_id')
    result = Group_Transaction.get_transactions(g_id)
    data_list = []
    for entry in result:
        data = {}
        data['t_id'] = int(entry['T_Id'])
        data_list.append(data)
    response = HttpResponse(json.dumps(data_list), content_type='application/json')
    return response

@csrf_exempt
def expocr_group_get_group_by_transaction(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    t_id = params.get('t_id')
    result = Group_Transaction.get_group(t_id)
    data_list = []
    for entry in result:
        data = {}
        data['g_id'] = int(entry['G_Id'])
        data_list.append(data)
    response = HttpResponse(json.dumps(data_list), content_type='application/json')
    return response

@csrf_exempt
def expocr_group_delete_transaction(request):
    if request.method == 'GET':
        params = request.GET
    elif request.method == 'POST':
        params = request.POST
    g_id = params.get('g_id')
    t_id = params.get('t_id')
    result = Group_Transaction.delete_transaction(g_id, t_id)
    data = {}
    data['deleted rows'] = result[0]
    data['deleted details'] = result[1]
    response = HttpResponse(json.dumps(data), content_type='application/json')
    return response

