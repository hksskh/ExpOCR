from __future__ import print_function
from __future__ import unicode_literals

from django.db import models
from django.db.models import Q


# Create your models here.
from expocr_user.models import User


class Group(models.Model):
    G_Id = models.AutoField(primary_key=True, null=False, unique=True)
    G_Name = models.CharField(max_length=255, null=False)

    manager = models.Manager()

    class Meta:
        db_table = 'GROUPS'
        ordering = ['G_Id']

    @staticmethod
    def get_all_groups():
        result = Group.manager.all()
        return result

    @staticmethod
    def create_group(name):
        print ("Group Name is")
        print (name)
        result = Group.manager.create(G_Name=name)

        return result

    @staticmethod
    def get_group_name(id):
        query = Q(G_Id=id)
        result = Group.manager.filter(query)
        return result

    @staticmethod
    def update_group_name(g_id, g_name):
        query = Q(G_Id=g_id)
        result = Group.manager.filter(query).update(G_Name=g_name)
        return result

    @staticmethod
    def delete_group(id, name):
        query = Q(G_Id=id) & Q(G_Name=name)
        result = Group.manager.filter(query).delete()
        return result


class Member(models.Model):
    GU_Id = models.AutoField(primary_key=True, null=False, unique=True)
    G_Id = models.IntegerField(null=False)
    U_Id = models.IntegerField(null=False)

    manager = models.Manager()

    class Meta:
        db_table = 'MEMBERS'
        ordering = ['GU_Id']

    @staticmethod
    def get_all_members():
        result = Member.manager.all()
        return result

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
    def add_member_by_email(g_id, u_email):
        result = User.get_user_by_email(u_email)
        u_id = result[0].U_Id
        query = Q(G_Id=g_id) & Q(U_Id=u_id)
        result = Member.manager.filter(query)
        created = result.count()
        if created == 0:
            result = Member.manager.create(G_Id=g_id, U_Id=u_id)
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
    GT_Id = models.AutoField(primary_key=True, null=False, unique=True)
    G_Id = models.IntegerField(null=False)
    U_Id = models.IntegerField(null=False)
    Amount = models.DecimalField(max_digits=17, decimal_places=2, null=False)
    Memo = models.CharField(max_length=255, null=False)
    Date = models.DateTimeField(null=False)
    Category = models.CharField(max_length=255, null=False)

    manager = models.Manager()

    class Meta:
        db_table = 'GROUP_TRANSACTIONS'
        ordering = ['GT_Id']

    @staticmethod
    def get_all_group_transactions():
        result = Group_Transaction.manager.all()
        return result

    @staticmethod
    def add_transaction(g_id, u_id, category, memo, amount, date):
        result = Group_Transaction.manager.create(G_Id=g_id, U_Id=u_id, Amount=amount, Memo=memo, Date=date, Category=category)

        return result

    @staticmethod
    def get_user_transactions(g_id, u_id):
        query = Q(G_Id=g_id) & Q(U_Id=u_id)
        result = Group_Transaction.manager.filter(query).order_by('-Date')
        return result

    @staticmethod
    def get_group_transactions(g_id):
        query = Q(G_Id=g_id)
        result = Group_Transaction.manager.filter(query)
        return result

    @staticmethod
    def get_group(gt_id):
        query = Q(GT_Id=gt_id)
        result = Group_Transaction.manager.filter(query).order_by('G_Id').values('G_Id')
        return result

    @staticmethod
    def delete_transaction(g_id, gt_id):
        query = Q(G_Id=g_id) & Q(GT_Id=gt_id)
        result = Group_Transaction.manager.filter(query).delete()
        return result

    @staticmethod
    def delete_transaction_by_date(date):
        date_array = date.split(" ")
        date = date_array[0]
        time = date_array[1]
        date_array = date.split("-")
        year = int(date_array[0])
        month = int(date_array[1])
        day = int(date_array[2])
        time_array = time.split(":")
        hour = int(time_array[0])
        minute = int(time_array[1])
        second = int(time_array[2])
        query = Q(Date__year=year) & Q(Date__month=month) & Q(Date__day=day) & Q(Date__hour=hour) & Q(Date__minute=minute) & Q(Date__second=second)
        result = Group_Transaction.manager.filter(query).delete()
        return result
