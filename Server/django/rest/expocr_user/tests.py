from django.test import TestCase
from django.core import serializers
from expocr_user.models import User
# Create your tests here.



class UserTestCase(TestCase):
    def setUp(self):
    	User.create_user(username="joesmith1", email="joesmith1@gmail.com", password="joesmith1password")
    	User.create_user(username="joesmith2", email="joesmith2@gmail.com", password="joesmith2password")
    	User.create_user(username="joesmith3", email="joesmith3@gmail.com", password="joesmith3password")
    	User.create_user(username="joesmith4", email="joesmith4@illinois.edu", password="joesmith4password")

    def test_user_create_existing_username(self):
    	test = User.create_user(username="joesmith1", email="joesmith@gmail.com", password="joesmith1password")
    	self.assertEqual(test[0], 'Username Exists')

    def test_user_create_existing_email(self):
    	test = User.create_user(username="joesmith5", email="joesmith1@gmail.com", password="joesmith1password")
    	self.assertEqual(test[0], 'Email Exists')

    def test_user_get_gmail_user(self):
    	test = User.get_gmail_user()
    	self.assertEqual(len(test), 3)
    	for user in test:
    		self.assertTrue('@gmail.com' in user.Email)

    def test_user_compare_pwd_by_email(self):
    	test = User.compare_pwd_by_email(email="joesmith1@gmail.com", password="joesmith1password")
    	self.assertTrue(test)
    	test2 = User.compare_pwd_by_email(email="joesmith1@gmail.com", password="joesmith")
    	self.assertFalse(test2)

    def test_user_count_edu_user(self):
    	test = User.count_edu_user()
    	self.assertEqual(test, 1)

    def test_user_update_user_name(self):
    	test = User.update_user_name(email="joesmith1@gmail.com", password="joesmith1password", username="joesmith")
    	new_user = User.get_user_by_email(email="joesmith1@gmail.com")
    	self.assertEqual(new_user[0].U_Name, 'joesmith')
