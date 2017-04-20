from django.test import TestCase
from django.core import serializers
from models import Transaction
from datetime import datetime

# Create your tests here.

class TransactionTestCase(TestCase):

    current_time = str(datetime.now())

    def setUp(self):
        Transaction.create_transaction(sender_id="1", receiver_id="2", category="Entertainment", memo="Lego Movie",
                                       amount="10.00", date=self.current_time)
        Transaction.create_transaction(sender_id="2", receiver_id="3", category="Food", memo="Chipotle", amount="10.00",
                                       date=self.current_time)
        Transaction.create_transaction(sender_id="2", receiver_id="3", category="Apartment", memo="Toilet Paper",
                                       amount="10.00", date=self.current_time)
        Transaction.create_transaction(sender_id="2", receiver_id="2", category="Apartment", memo="Rent",
                                       amount="-500.00", date=self.current_time)

    def test_get_all_transactions(self):
        test = Transaction.get_all_transactions()
        self.assertEqual(test.count(), 4)

    def test_transaction_is_created(self):
        test = Transaction.get_transaction_by_sender_id(sender_id="1")
        self.assertEqual(len(test), 1)
        self.assertEqual(test[0]['Receiver_Id'], 2)
        self.assertEqual(test[0]['Amount'], 10.00)
        self.assertTrue(str(test[0]['Date']).startswith(self.current_time))

    def test_transaction_get_receivers(self):
        test = Transaction.get_all_receivers(sender_id="2")
        self.assertEqual(int(test[0]['Receiver_Id']), 2)
        self.assertEqual(int(test[1]['Receiver_Id']), 3)
        self.assertEqual(len(test), 2)
        self.assertEqual(test[1]['Amount__sum'], 20.00)

    def test_transaction_get_transaction_between(self):
        test = Transaction.get_transaction_between(sender_id=2, receiver_id=3)
        self.assertEqual(len(test), 2)
        for transaction in test:
            self.assertEqual(transaction['Amount'], 10.00)

    def test_get_entertain_transaction(self):
        test = Transaction.get_entertain_transaction(sender_id=1)
        self.assertEqual(test.count(), 1)
        self.assertEqual(test[0].Memo, 'Lego Movie')

    def test_count_sender_transaction(self):
        test = Transaction.count_sender_transaction(sender_id=2)
        self.assertEqual(test, 3)

    def test_update_Memo(self):
        test = Transaction.get_entertain_transaction(sender_id=1)
        t_id = test[0].T_Id
        Transaction.update_Memo(t_id, 'Lego Batman Movie')
        test = Transaction.get_entertain_transaction(sender_id=1)
        self.assertEqual(test[0].Memo, 'Lego Batman Movie')

    def test_delete_transaction_between(self):
        test = Transaction.delete_transaction_between(sender_id=2, receiver_id=3)
        self.assertEqual(test[0], 2)
        test = Transaction.get_transaction_between(sender_id=2, receiver_id=3)
        self.assertEqual(test.count(), 0)
