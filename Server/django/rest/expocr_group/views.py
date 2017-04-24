from django.http import HttpResponse
from django.core import serializers
from django.views.decorators.csrf import csrf_exempt
from models import Group, Member, Group_Transaction
from expocr_user.models import User
import json

# Create your views here.

@csrf_exempt
def expocr_get_all_groups(request):
	data = serializers.serialize('json', Group.get_all_groups())
	response = HttpResponse(data, content_type="application/json")
	return response

@csrf_exempt
def expocr_get_all_members(request):
	data = serializers.serialize('json', Member.get_all_members())
	response = HttpResponse(data, content_type="application/json")
	return response

@csrf_exempt
def expocr_get_all_group_transactions(request):
	data = serializers.serialize('json', Group_Transaction.get_all_group_transactions())
	response = HttpResponse(data, content_type="application/json")
	return response
	
@csrf_exempt
def expocr_net_balance(request):
    
    uid = params.get('U_id')
    gid = params.get('G_Id')
    data = {}
    transactions = Group_Transaction.get_transactions(g_id)
    sum = 0
    for transaction in transactions():
        transaction_uid = int(transaction['U_Id'])
        if (uid == transaction_uid):
           sum=sum+float(transaction['Amount'])
    data['net_balance'] = sum
    response = HttpResponse(data, content_type="application/json")
    return response

#def expocr_group_create(request):
#    if request.method == 'GET':
#        params = request.GET
#    elif request.method == 'POST':
#        params = request.POST
#    name = params.get('name')
#    result = Group.create_group(name)
#    data = {}
#    data['g_id'] = result.G_Id
#    data['g_name'] = result.G_Name
#    response = HttpResponse(json.dumps(data), content_type='application/json')
#    return response
@csrf_exempt
def expocr_group_create(request):
	if request.method == 'GET':
		params = request.GET
	elif request.method == 'POST':
		params = request.POST
	name = params.get('group_name')
	print ("G Name from views.py")
	print (name)
	result = Group.create_group(name)
	data = {}
	gid = result.G_Id
	data['g_id'] = int(result.G_Id)
	data['g_name'] = str(result.G_Name)
	uid = params.get('u_id')
	emails = params.get('group_members_emails').split(',')
	uid_res = Member.add_member(gid,uid)
	#emails = data['emails'].split(',')
	for email in emails:
		if email is not "":
			user = User.get_user_by_email(email)
			for result in user:
				uid = int(result.U_Id)
				Member.add_member(gid, uid)
				#Member.add_member(gid,email)
	response = HttpResponse(json.dumps(data), content_type='application/json')
	return response

@csrf_exempt
def expocr_group_update_group_name(request):
	if request.method == 'GET':
		params = request.GET
	elif request.method == 'POST':
		params = request.POST
	g_id = params.get('g_id')
	g_name = params.get('g_name')
	result = Group.update_group_name(g_id, g_name)
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
def expocr_group_add_member_by_email(request):
	if request.method == 'GET':
		params = request.GET
	elif request.method == 'POST':
		params = request.POST
	g_id = params.get('g_id')
	u_email = params.get('u_email')
	result = Member.add_member_by_email(g_id, u_email)
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
		data['g_name'] = Group.get_group_name(data['g_id'])[0].G_Name
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

	u_id = params.get('receiver_id')
	g_id = params.get('group_id')
	category = params.get('category')
	memo = params.get('memo')
	amount = float(params.get('amount'))
	date = params.get('date')
	result = Group_Transaction.add_transaction(g_id, u_id, category, memo, amount, date)
	data = {'g_id': result.G_Id, 'u_id': result.U_Id, 'amount': amount}
	response = HttpResponse(json.dumps(data), content_type='application/json')
	return response

@csrf_exempt
def expocr_group_add_transaction_by_email(request):
	if request.method == 'GET':
		params = request.GET
	elif request.method == 'POST':
		params = request.POST

	u_email = params.get('receiver_email')
	g_id = params.get('group_id')
	category = params.get('category')
	memo = params.get('memo')
	amount = float(params.get('amount'))
	date = params.get('date')

	u_id = User.get_user_by_email(u_email)[0].U_Id
	result = Group_Transaction.add_transaction(g_id, u_id, category, memo, amount, date)

	data = {'g_id': result.G_Id, 'u_id': result.U_Id, 'amount': amount}
	response = HttpResponse(json.dumps(data), content_type='application/json')
	return response

@csrf_exempt
def expocr_group_get_user_transactions(request):
	if request.method == 'GET':
		params = request.GET
	elif request.method == 'POST':
		params = request.POST
	g_id = params.get('g_id')
	u_id = params.get('u_id')
	result = Group_Transaction.get_user_transactions(g_id, u_id)
	data_list = []
	for entry in result:
		data = {}
		data['amount'] = float(entry.Amount)
		data['category'] = str(entry.Category)
		data['memo'] = str(entry.Memo)
		data['date'] = str(entry.Date)
		data_list.append(data)
	response = HttpResponse(json.dumps(data_list), content_type='application/json')
	return response

@csrf_exempt
def expocr_group_get_group_transactions(request):
	if request.method == 'GET':
		params = request.GET
	elif request.method == 'POST':
		params = request.POST
	g_id = params.get('g_id')
	result = Group_Transaction.get_group_transactions(g_id)
	id_list = []
	data_list = []
	for entry in result:
		data = {}
		data['g_id'] = int(entry.G_Id)
		data['u_id'] = int(entry.U_Id)
		data['amount'] = float(entry.Amount)
		data_list.append(data)
		id_list.append(entry.U_Id)
	receiver_bulk = User.manager.in_bulk(id_list)
	index = 0
	while index < len(id_list):
		pk = id_list[index]
		data_list[index]['u_name'] = receiver_bulk[pk].U_Name
		data_list[index]['u_email'] = receiver_bulk[pk].Email
		index += 1
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
def expocr_group_delete_by_date(request):
	if request.method == 'GET':
		params = request.GET
	elif request.method == 'POST':
		params = request.POST
	date = params.get('date')
	data = {}
	print("hi")
	result = Group_Transaction.delete_transaction_by_date(date)
	print("bye")
	data['deleted rows'] = result[0]
	data['deleted details'] = result[1]
	response = HttpResponse(json.dumps(data), content_type="application/json")
	return response



