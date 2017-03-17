from django.test import TestCase
from django.core import serializers
from models import User


# Create your tests here.

class UserTestCase(TestCase):
    def setUp(self):
        User.create_user(username="joesmith1", email="joesmith1@gmail.com", password="joesmith1password")
        User.create_user(username="joesmith2", email="joesmith2@gmail.com", password="joesmith2password")
        User.create_user(username="joesmith3", email="joesmith3@gmail.com", password="joesmith3password")
        User.create_user(username="joesmith4", email="joesmith4@illinois.edu", password="joesmith4password")

    def tearDown(self):
        User.delete_user(username="joesmith1", email="joesmith1@gmail.com", password="joesmith1password")
        User.delete_user(username="joesmith2", email="joesmith2@gmail.com", password="joesmith2password")
        User.delete_user(username="joesmith3", email="joesmith3@gmail.com", password="joesmith3password")
        User.delete_user(username="joesmith4", email="joesmith4@illinois.edu", password="joesmith4password")
        self.assertEqual(0, User.get_user_by_name("joesmith1").count())
        self.assertEqual(0, User.get_user_by_name("joesmith2").count())
        self.assertEqual(0, User.get_user_by_name("joesmith3").count())
        self.assertEqual(0, User.get_user_by_name("joesmith4").count())

    def test_get_all_users(self):
        test = User.get_all_users()
        self.assertEqual(4, test.count())

    def test_user_create_existing_username(self):
        test = User.create_user(username="joesmith1", email="joesmith@gmail.com", password="joesmith1password")
        self.assertEqual(test[0], 'Username Exists')

    def test_user_create_existing_email(self):
        test = User.create_user(username="joesmith1", email="joesmith1@gmail.com", password="joesmith1password")
        self.assertEqual(test[0], 'Email Exists')

    def test_get_user_by_name(self):
        test = User.get_user_by_name('joesmith4')
        id = test[0]['U_Id']
        test = User.get_user_by_id(id)
        self.assertEqual(test[0].Email, 'joesmith4@illinois.edu')

    def test_user_get_user_by_email(self):
        test = User.get_user_by_email(email="joesmith1@gmail.com")
        self.assertEqual(test[0].U_Name, 'joesmith1')
        id = test[0].U_Id
        test = User.get_user_by_id(id)
        self.assertEqual("joesmith1@gmail.com", test[0].Email)

    def test_user_get_gmail_user(self):
        test = User.get_gmail_user()
        self.assertEqual(test.count(), 3)
        for user in test:
            self.assertTrue('@gmail.com' in user.Email)

    def test_user_update_user_name(self):
        User.update_user_name(email="joesmith1@gmail.com", password="joesmith1password", username="1expocrtest1")
        new_user = User.get_user_by_email(email="joesmith1@gmail.com")
        self.assertEqual(new_user[0].U_Name, '1expocrtest1')

    def test_user_login_by_email(self):
        test = User.login_by_email(email="joesmith11@gmail.com", password="joesmith1password")
        self.assertFalse(test[0])
        test = User.login_by_email(email="joesmith1@gmail.com", password="joesmith11password")
        self.assertEqual(test[1], 'Password incorrect')
        test = User.login_by_email(email="joesmith2@gmail.com", password="joesmith2password")
        self.assertEqual(test[1][0]['U_Name'], 'joesmith2')

    def test_count_edu_user(self):
        test = User.count_edu_user()
        self.assertEqual(test, 1)
