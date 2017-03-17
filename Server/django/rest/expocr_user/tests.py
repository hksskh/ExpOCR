from django.test import TestCase
from django.core import serializers
from models import User


# Create your tests here.

class UserTestCase(TestCase):
    def setUp(self):
        User.create_user(username="expocrtest1", email="joesmith1@test.com", password="joesmith1password")
        User.create_user(username="expocrtest2", email="joesmith2@test.com", password="joesmith2password")
        User.create_user(username="expocrtest3", email="joesmith3@test.com", password="joesmith3password")
        User.create_user(username="expocrtest4", email="joesmith4@test.edu", password="joesmith4password")

    def tearDown(self):
        User.delete_user(username="expocrtest1", email="joesmith1@test.com", password="joesmith1password")
        User.delete_user(username="expocrtest2", email="joesmith2@test.com", password="joesmith2password")
        User.delete_user(username="expocrtest3", email="joesmith3@test.com", password="joesmith3password")
        User.delete_user(username="expocrtest4", email="joesmith4@test.edu", password="joesmith4password")
        self.assertEqual(0, User.get_user_by_name("expocrtest1").count())
        self.assertEqual(0, User.get_user_by_name("expocrtest2").count())
        self.assertEqual(0, User.get_user_by_name("expocrtest3").count())
        self.assertEqual(0, User.get_user_by_name("expocrtest4").count())

    def test_user_create_existing_username(self):
        test = User.create_user(username="expocrtest1", email="joesmith@gmail.com", password="joesmith1password")
        self.assertEqual(test[0], 'Username Exists')

    def test_user_create_existing_email(self):
        test = User.create_user(username="expocrtest1", email="joesmith1@test.com", password="joesmith1password")
        self.assertEqual(test[0], 'Email Exists')

    def test_user_get_gmail_user(self):
        test = User.get_gmail_user()
        for user in test:
            self.assertTrue('@gmail.com' in user.Email)

    def test_user_get_user_by_email(self):
        test = User.get_user_by_email(email="joesmith1@test.com")
        self.assertEqual(test[0].U_Name, 'expocrtest1')
        id = test[0].U_Id
        test = User.get_user_by_id(id)
        self.assertEqual("joesmith1@test.com", test[0].Email)

    def test_user_update_user_name(self):
        User.update_user_name(email="joesmith1@test.com", password="joesmith1password", username="1expocrtest1")
        new_user = User.get_user_by_email(email="joesmith1@test.com")
        self.assertEqual(new_user[0].U_Name, '1expocrtest1')

    def test_user_login_by_email(self):
        test = User.login_by_email(email="joesmith11@test.com", password="joesmith1password")
        self.assertFalse(test[0])
        test = User.login_by_email(email="joesmith1@test.com", password="joesmith11password")
        self.assertEqual(test[1], 'Password incorrect')
        test = User.login_by_email(email="joesmith2@test.com", password="joesmith2password")
        self.assertEqual(test[1][0]['U_Name'], 'expocrtest2')
