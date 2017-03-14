from __future__ import unicode_literals

from django.db import models
from django.db.models import Q


# Create your models here.

class User(models.Model):
    U_Id = models.AutoField(primary_key=True, unique=True)
    U_Name = models.CharField(max_length=255)
    Email = models.EmailField(max_length=255, unique=True)
    Password = models.CharField(max_length=255)

    manager = models.Manager()

    class Meta:
        db_table = 'users'
        ordering = ['U_Id']

    @staticmethod
    def create_user(username, email, password):
        user = User.manager.create(U_Name=username, Email=email, Password=password)
        return user

    @staticmethod
    def get_user_by_id(id):
        query = Q(U_Id=id)
        user = User.manager.filter(query)
        return user

    @staticmethod
    def get_gmail_user():
        query = Q(Email__contains='@gmail.com') & Q(U_Id__gte=4)
        users = User.manager.filter(query).exclude(U_Id=5).order_by('U_Id', 'U_Name')
        return users

    @staticmethod
    def count_edu_user():
        return User.manager.filter(Email__regex=r'^.+@.+\.edu').count()

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
