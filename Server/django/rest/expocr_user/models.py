from __future__ import unicode_literals

from django.db import models
from django.db.models import Q
from apps import ExpocrUserConfig


# Create your models here.

class User(models.Model):
    U_Id = models.AutoField(primary_key=True, null=False, unique=True)
    U_Name = models.CharField(max_length=255, null=False)
    Email = models.EmailField(max_length=255, null=False, unique=True)
    Password = models.CharField(max_length=255, null=False)
    Vericode = models.CharField(max_length=255)

    defaultFacebookPassword="0"

    manager = models.Manager()

    class Meta:
        db_table = 'USERS'
        ordering = ['U_Id']

    @staticmethod
    def try_create_user(username, email, password):
        query = Q(Email__exact=email)
        result = User.manager.filter(query)
        if result.count() > 0:
            return 'Email Exists', result.count()
        msg = 'Please check activation mail in ' + email
        return msg, 0

    @staticmethod
    def create_user(username, email, password):
        query = Q(Email__exact=email)
        result = User.manager.filter(query)
        if result.count() > 0:
            return 'Email Exists', result.count()
        user = User.manager.create(U_Name=username, Email=email, Password=password)
        return user, 0

    @staticmethod
    def create_facebook_user(username, email):
        query = Q(Email__exact=email)
        result = User.manager.filter(query)
        if result.count() > 0:
            return 'Email Exists', result.count()
        user = User.manager.create(U_Name=username, Email=email, Password=User.defaultFacebookPassword)
        return user, 0

    @staticmethod
    def get_user_by_id(id):
        query = Q(U_Id=id)
        user = User.manager.filter(query)
        return user

    @staticmethod
    def get_user_by_email(email):
        query = Q(Email__exact=email)
        result = User.manager.filter(query)
        return result

    @staticmethod
    def login_by_email(email, password):
        query = Q(Email__exact=email)
        result = User.manager.filter(query)
        if password==User.defaultFacebookPassword:
            return False, 'Password incorrect'
        if result.count() == 0:
            return False, 'Email not exists'
        query = Q(Password__exact=password)
        result = result.filter(query)
        if result.count() == 0:
            return False, 'Password incorrect'
        return True, result.values('U_Id', 'U_Name', 'Email')

    @staticmethod
    def login_with_facebook(email):
        query = Q(Email__exact=email)
        result = User.manager.filter(query)
        if result.count() == 0:
            return False, 'Email not exists'
        return True, result.values('U_Id', 'U_Name', 'Email')

    @staticmethod
    def update_user_name(email, password, username):
        query = Q(Email__exact=email) & Q(Password__exact=password)
        result = User.manager.filter(query).update(U_Name=username)
        return result

    @staticmethod
    def delete_user(username, email, password):
        query = Q(U_Id__gt=12) & Q(U_Name__exact=username) & Q(Email__exact=email) & Q(Password__exact=password)
        result = User.manager.filter(query).delete()
        return result

    @staticmethod
    def add_vericode(email, vericode):
        query = Q(Email__exact=email)
        result = User.manager.filter(query)
        if result.count() == 0:
            return 'Email Does Not Exist', 0
        user = User.manager.filter(query).update(Vericode=vericode)
        return user, 1

    @staticmethod
    def check_vericode(email, vericode):
        query = Q(Email__exact=email)
        result = User.manager.filter(query)
        if result.count() == 0:
            return False, 'Email not exists'
        query = Q(Vericode__exact=vericode)
        result = result.filter(query)
        if result.count() == 0:
            return False, 'Vericode incorrect'
        return True, email

    @staticmethod
    def change_user_password(email, password):
        query = Q(Email__exact=email)
        result = User.manager.filter(query)
        if result.count() == 0:
            return False, 'Email not exists'
        query = Q(Email__exact=email)
        d = {"Password": password, "Vericode": ""}
        result = User.manager.filter(query).update(**d)
        return result

    @staticmethod
    def get_two_users_by_id(id1, id2):
        query = Q(U_Id=id1) | Q(U_Id=id2)
        users = User.manager.filter(query)
    
        return users
