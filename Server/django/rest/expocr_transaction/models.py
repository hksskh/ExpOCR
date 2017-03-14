from __future__ import unicode_literals

from django.db import models
from django.db.models import Q


# Create your models here.

class Transaction(models.Model):
    T_Id = models.AutoField(primary_key=True, unique=True)
    Sender_Id = models.IntegerField()
    Receiver_Id = models.IntegerField()
    Category = models.CharField(max_length=255)
    Memo = models.CharField(max_length=255)
    Amount = models.DecimalField(max_digits=17, decimal_places=2)
    Date = models.DateTimeField()

    manager = models.Manager()

    class Meta:
        db_table = 'transactions'
        ordering = ['T_Id']

    @staticmethod
    def create_transaction(sender_id, receiver_id, category, memo, amount, date):
        result = Transaction.manager.create(Sender_Id=sender_id, Receiver_Id=receiver_id, Category=category, Memo=memo, Amount=amount, Date=date)
        return result

    @staticmethod
    def get_transaction_by_sender_id(sender_id):
        query = Q(Sender_Id=sender_id)
        result = Transaction.manager.filter(query)
        return result

    @staticmethod
    def get_entertain_transaction():
        query = Q(Category='Entertainment') & Q(Sender_Id=1)
        result = Transaction.manager.filter(query).exclude(Receiver_Id=5).order_by('Sender_Id', 'Receiver_Id')
        return result

    @staticmethod
    def count_sender_transaction(sender_id):
        return Transaction.manager.filter(Sender_Id=sender_id).count()

    @staticmethod
    def update_Memo(t_id, memo):
        query = Q(T_Id=t_id)
        result = Transaction.manager.filter(query).update(Memo=memo)
        return result

    @staticmethod
    def delete_transaction_between(sender_id, receiver_id):
        query = Q(Sender_Id=sender_id) & Q(Receiver_Id=receiver_id)
        result = Transaction.manager.filter(query).delete()
        return result
