from django.test import TestCase
from django.core import serializers
from models import Group, Member, Group_Transaction
from expocr_user.models import User
from expocr_transaction.models import Transaction


# Create your tests here.



class GroupTestCase(TestCase):
	def setUp(self):
		Group.create_group(name="group1")  # id may not be 1, can print to see the id
		Group.create_group(name="group2")  # id may not be 2
		User.create_user(username="joesmith1", email="joesmith1@gmail.com", password="joesmith1password")  # id 1
		User.create_user(username="joesmith2", email="joesmith2@gmail.com", password="joesmith2password")  # id 2
		User.create_user(username="joesmith3", email="joesmith3@gmail.com", password="joesmith3password")  # id 3
		User.create_user(username="joesmith4", email="joesmith4@illinois.edu", password="joesmith4password")  # id 4
		Member.add_member(g_id="1", u_id="1")
		Member.add_member(g_id="1", u_id="2")
		Member.add_member(g_id="1", u_id="3")
		Transaction.create_transaction(sender_id="1", receiver_id="2", category="Entertainment", memo="Lego Movie",
									   amount="10.00", date="1000-01-01T00:20:00Z")  # id 1
		Transaction.create_transaction(sender_id="2", receiver_id="3", category="Food", memo="Chipotle", amount="10.00",
									   date="1000-01-01T00:20:00Z")  # id 2
		Transaction.create_transaction(sender_id="2", receiver_id="3", category="Apartment", memo="Toilet Paper",
									   amount="10.00", date="1000-01-01T00:20:00Z")  # id 3
		Transaction.create_transaction(sender_id="2", receiver_id="2", category="Apartment", memo="Rent",
									   amount="-500.00", date="1000-01-01T00:20:00Z")  # id 4


	def test_update_group_name(self):
		test = Group.create_group('group3')
		self.assertEqual(test.G_Name, 'group3')
		Group.update_group_name(id=test.G_Id, name='group111')
		test = Group.get_group_name(id=test.G_Id)
		self.assertEqual(test[0].G_Name, 'group111')

	def test_group_add_member(self):
		members = Member.get_members(g_id="1")
		self.assertEqual(members.count(), 3)
		test2 = Member.get_groups(u_id="1")
		self.assertEqual(test2.count(), 1)
		test = Member.add_member(g_id=1, u_id=1)
		self.assertEqual(test[1], 1)
		test = Member.add_member(g_id=1, u_id=4)
		self.assertEqual(test[1], 0)
		self.assertEqual(test[0].U_Id, 4)
		test = Member.get_members(g_id=1)
		self.assertEqual(test.count(), 4)

	def test_member_get_groups(self):
		Member.add_member(g_id=2, u_id=2)
		test = Member.get_groups(u_id=2)
		self.assertEqual(test.count(), 2)
		Member.delete_member(g_id=1, u_id=2)
		test = Member.get_groups(u_id=2)
		self.assertEqual(test.count(), 1)

	def test_group_add_transaction(self):
		test = Group_Transaction.add_transaction(g_id="1", t_id="1")
		self.assertEqual(test[0].G_Id, '1')
		self.assertEqual(test[0].T_Id, '1')
		self.assertEqual(test[1], 0)

	def test_group_get_transactions(self):
		test = Group_Transaction.get_transactions(g_id="1")
		self.assertEqual(test.count(), 0)
		Group_Transaction.add_transaction(g_id="1", t_id="1")
		Group_Transaction.add_transaction(g_id="1", t_id="2")
		Group_Transaction.add_transaction(g_id="1", t_id="3")
		Group_Transaction.add_transaction(g_id="1", t_id="4")
		Group_Transaction.delete_transaction(g_id=1, t_id=1)
		test2 = Group_Transaction.get_transactions(g_id="1")
		self.assertEqual(test2.count(), 3)

	def test_transaction_get_group(self):
		test = Group_Transaction.get_group(t_id="1")
		self.assertEqual(test.count(), 0)
		Group_Transaction.add_transaction(g_id="1", t_id="1")
		test2 = Group_Transaction.get_group(t_id="1")
		self.assertEqual(test2.count(), 1)
