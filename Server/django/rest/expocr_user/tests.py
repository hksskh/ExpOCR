from django.test import TestCase
from django.core import serializers
from models import User


# Create your tests here.

class UserTestCase(TestCase):
    def setUp(self):
        User.create_user(username="joesmith1", email="joesmith1@gmail.com", password="joesmith1password")
        User.create_user(username="joesmith2", email="joesmith2@gmail.com", password="joesmith2password")
        User.create_user(username="joesmith3", email="joesmith3@gmail.com", password="joesmith3password")
        User.create_user(username="joesmith4", email="joesmith4@gmail.com", password="joesmith4password")

    def tearDown(self):
        User.delete_user(username="joesmith1", email="joesmith1@gmail.com", password="joesmith1password")
        User.delete_user(username="joesmith2", email="joesmith2@gmail.com", password="joesmith2password")
        User.delete_user(username="joesmith3", email="joesmith3@gmail.com", password="joesmith3password")
        User.delete_user(username="joesmith4", email="joesmith4@gmail.com", password="joesmith4password")
        # self.assertEqual(0, User.get_user_by_email("joesmith1@gmail.com").count())
        # self.assertEqual(0, User.get_user_by_email("joesmith2@gmail.com").count())
        # self.assertEqual(0, User.get_user_by_email("joesmith3@gmail.com").count())
        # self.assertEqual(0, User.get_user_by_email("joesmith4@gmail.com").count())

    def test_try_create_user_fail(self):
        test = User.try_create_user(username="joesmith1", email="joesmith1@gmail.com", password="joesmith1password")
        self.assertEqual(test[0], 'Email Exists')

    def test_try_create_user(self):
        test = User.try_create_user(username="joesmith5", email="joesmith5@gmail.com", password="joesmith5password")
        self.assertEqual(test[0], 'Please check activation mail in joesmith5@gmail.com')

    def test_create_facebook_user_fail(self):
        test = User.create_facebook_user(username="joesmith1", email="joesmith1@gmail.com")
        self.assertEqual(test[0], 'Email Exists')

    def test_create_facebook_user(self):
        test = User.create_facebook_user(username="joesmith6", email="joesmith6@gmail.com")
        self.assertEqual(test[0].Password, "0")
        test2 = User.login_with_facebook("joesmith6@gmail.com")
        self.assertEqual(test2[0], True)
        self.assertEqual(test2[1][0]['Email'], "joesmith6@gmail.com")
        self.assertEqual(test2[1][0]['U_Name'], "joesmith6")

    def test_user_create_existing_email(self):
        test = User.create_user(username="joesmith1", email="joesmith1@gmail.com", password="joesmith1password")
        self.assertEqual(test[0], 'Email Exists')


    def test_user_get_user_by_email(self):
        test = User.get_user_by_email(email="joesmith1@gmail.com")
        self.assertEqual(test[0].U_Name, 'joesmith1')
        id = test[0].U_Id
        test = User.get_user_by_id(id)
        self.assertEqual("joesmith1@gmail.com", test[0].Email)


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

    def test_login_with_facebook_fail(self):
        test = User.login_with_facebook("joesmith@gmail.com")
        self.assertEqual(test[0], False)
        self.assertEqual(test[1], 'Email not exists')


    def test_add_vericode_fail(self):
        test = User.add_vericode("joesmith@gmail.com", "AAAAAA")
        self.assertEqual(test[0], "Email Does Not Exist")

    def test_add_vericode(self):
        test = User.add_vericode("joesmith1@gmail.com", "AAAAAA")
        new_user = User.get_user_by_email(email="joesmith1@gmail.com")
        self.assertEqual(new_user[0].Vericode, 'AAAAAA')
        test = User.check_vericode("joesmith1@gmail.com", "BBBBBB")
        self.assertEqual(test[0], False)
        self.assertEqual(test[1], "Vericode incorrect")
        test2 = User.check_vericode("joesmith1@gmail.com", "AAAAAA")
        self.assertEqual(test2[0], True)
        self.assertEqual(test2[1], "joesmith1@gmail.com")

    def test_check_vericode_fail(self):
        test = User.check_vericode("joesmith@gmail.com", "BBBBBB")
        self.assertEqual(test[0], False)
        self.assertEqual(test[1], 'Email not exists')

    def test_change_user_password(self):
        test = User.change_user_password("joesmith1@gmail.com", "newPassword")
        new_user = User.get_user_by_email(email="joesmith1@gmail.com")
        self.assertEqual(new_user[0].Password, 'newPassword')

    def test_change_user_password_fail(self):
        test = User.change_user_password("joesmith@gmail.com", "newPassword")
        self.assertEqual(test[0], False)
        self.assertEqual(test[1], 'Email not exists')




