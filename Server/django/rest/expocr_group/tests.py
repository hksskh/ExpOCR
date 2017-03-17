from django.test import TestCase
from django.core import serializers
from expocr_group.models import Group, Member, Group_Transaction
from expocr_user.models import User
from expocr_transaction.models import Transaction
# Create your tests here.



class UserTestCase(TestCase):
    def setUp(self):
    	Group.create_group(name="group1") # id 1
    	Group.create_group(name="group2") # id 2
    	User.create_user(username="joesmith1", email="joesmith1@gmail.com", password="joesmith1password") # id 1
    	User.create_user(username="joesmith2", email="joesmith2@gmail.com", password="joesmith2password") # id 2
    	User.create_user(username="joesmith3", email="joesmith3@gmail.com", password="joesmith3password") # id 3
    	User.create_user(username="joesmith4", email="joesmith4@illinois.edu", password="joesmith4password") # id 4
    	Member.add_member(g_id="1", u_id="1")
    	Member.add_member(g_id="1", u_id="2")
    	Member.add_member(g_id="1", u_id="3")
    	Transaction.create_transaction(sender_id="1", receiver_id="2", category="Entertainment", memo="Lego Movie", amount="10.00", date="1000-01-01T00:20:00Z") # id 1
        Transaction.create_transaction(sender_id="2", receiver_id="3", category="Food", memo="Chipotle", amount="10.00", date="1000-01-01T00:20:00Z") # id 2
        Transaction.create_transaction(sender_id="2", receiver_id="3", category="Apartment", memo="Toilet Paper", amount="10.00", date="1000-01-01T00:20:00Z") # id 3
        Transaction.create_transaction(sender_id="2", receiver_id="2", category="Apartment", memo="Rent", amount="-500.00", date="1000-01-01T00:20:00Z") # id 4
    	

    def test_group_add_member(self):
    	members = Member.get_members(g_id="1")
    	self.assertEqual(len(members), 3)
    	test2 = Member.get_groups(u_id="1")
    	self.assertEqual(len(test2), 1)

    def test_group_add_transaction(self):
    	test = Group_Transaction.add_transaction(g_id="1", t_id="1")
    	self.assertEqual(test[0].G_Id, '1')
    	self.assertEqual(test[0].T_Id, '1')
    	self.assertEqual(test[1], 0)

    def test_group_get_transactions(self):
    	test = Group_Transaction.get_transactions(g_id="1")
    	self.assertEqual(len(test), 0)
    	Group_Transaction.add_transaction(g_id="1", t_id="1")
    	Group_Transaction.add_transaction(g_id="1", t_id="2")
    	Group_Transaction.add_transaction(g_id="1", t_id="3")
    	Group_Transaction.add_transaction(g_id="1", t_id="4")
    	test2 = Group_Transaction.get_transactions(g_id="1")
    	self.assertEqual(len(test2), 4)

    def test_group_get_group(self):
    	test = Group_Transaction.get_group(t_id="1")
    	self.assertEqual(len(test), 0)
    	Group_Transaction.add_transaction(g_id="1", t_id="1")
    	test2 = Group_Transaction.get_group(t_id="1")
    	self.assertEqual(len(test2), 1)

