from __future__ import print_function
from __future__ import unicode_literals

from django.db import models
from django.db.models import Q


# Create your models here.

class Group(models.Model):
    G_Id = models.AutoField(primary_key=True, unique=True)
    G_Name = models.CharField(max_length=255)

    manager = models.Manager()

    class Meta:
        db_table = 'groups'
        ordering = ['G_Id']

    @staticmethod
    def create_group(name):
        result = Group.manager.create(G_Name=name)
        return result

    @staticmethod
    def get_group_name(id):
        query = Q(G_Id=id)
        result = Group.manager.filter(query)
        return result

    @staticmethod
    def update_group_name(id, name):
        query = Q(G_Id=id)
        result = Group.manager.filter(query).update(G_Name=name)
        return result

    @staticmethod
    def delete_group(id, name):
        query = Q(G_Id=id) & Q(G_Name=name)
        result = Group.manager.filter(query).delete()
        return result


class Member(models.Model):
    G_Id = models.IntegerField()
    U_Id = models.IntegerField()

    manager = models.Manager()

    class Meta:
        db_table = 'members'

    @staticmethod
    def add_member(g_id, u_id):
        query = Q(G_Id=g_id) & Q(U_Id=u_id)
        result = Member.manager.filter(query)
        created = result.count()
        if created == 0:
            result = Member.manager.create(G_Id=g_id,U_Id=u_id)
        else:
            # since the model Member has no primary key, need to use values to extract fields from filter result
            # if return the original filter result, then the serializers cannot serialize it without primary key
            result = result.values('G_Id', 'U_Id')

        return result, created

    @staticmethod
    def get_members(g_id):
        query = Q(G_Id=g_id)
        result = Member.manager.filter(query).order_by('U_Id').values('U_Id')
        return result

    @staticmethod
    def get_groups(u_id):
        query = Q(U_Id=u_id)
        result = Member.manager.filter(query).order_by('G_Id').values('G_Id')
        return result

    @staticmethod
    def delete_member(g_id, u_id):
        query = Q(G_Id=g_id) & Q(U_Id=u_id)
        result = Member.manager.filter(query).delete()
        return result


class Group_Transaction(models.Model):
    GT_Id = models.AutoField(primary_key=True, unique=True)
    G_Id = models.IntegerField()
    T_Id = models.IntegerField()

    manager = models.Manager()

    class Meta:
        db_table = 'group_transactions'
        ordering = ['GT_Id']

    @staticmethod
    def add_transaction(g_id, t_id):
        query = Q(G_Id=g_id) & Q(T_Id=t_id)
        result = Group_Transaction.manager.filter(query)
        created = result.count()
        if created == 0:
            result = Group_Transaction.manager.create(G_Id=g_id, T_Id=t_id)

        return result, created

    @staticmethod
    def get_transactions(g_id):
        query = Q(G_Id=g_id)
        result = Group_Transaction.manager.filter(query).order_by('T_Id').values('T_Id')
        return result

    @staticmethod
    def get_group(t_id):
        query = Q(T_Id=t_id)
        result = Group_Transaction.manager.filter(query).order_by('G_Id').values('G_Id')
        return result

    @staticmethod
    def delete_transaction(g_id, t_id):
        query = Q(G_Id=g_id) & Q(T_Id=t_id)
        result = Group_Transaction.manager.filter(query).delete()
        return result
