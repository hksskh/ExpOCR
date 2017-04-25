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
                                       amount="500.00", date=self.current_time)


    def test_transaction_is_created(self):
        test = Transaction.get_transaction_by_sender_id(sender_id="1")
        self.assertEqual(len(test), 1)
        self.assertEqual(test[0]['Receiver_Id'], 2)
        self.assertEqual(test[0]['Amount'], 10.00)
        self.assertTrue(str(test[0]['Date']).startswith(self.current_time))

    def test_transaction_get_transaction_between(self):
        test = Transaction.get_transaction_between(sender_id=2, receiver_id=3)
        self.assertEqual(len(test), 2)
        for transaction in test:
            self.assertEqual(transaction['Amount'], 10.00)


    def test_delete_transaction_between(self):
        test = Transaction.delete_transaction_between(sender_id=2, receiver_id=3)
        self.assertEqual(test[0], 2)
        test = Transaction.get_transaction_between(sender_id=2, receiver_id=3)
        self.assertEqual(test.count(), 0)

    def test_get_transaction_by_receiver_id(self):
        test = Transaction.get_transaction_by_receiver_id(3)
        self.assertTrue(len(test) == 2)
        self.assertEqual(test[0]['Receiver_Id'], 3)
        self.assertEqual(test[1]['Receiver_Id'], 3)

    def test_get_transaction_by_id(self):
        test = Transaction.get_transaction_by_id(2)
        self.assertTrue(len(test) == 4)

    def test_get_all_friends_sender(self):
        test = Transaction.get_all_friends_sender(2)
        self.assertTrue(len(test), 2)
        self.assertTrue(test[0]['Receiver_Id'] == 2 or test[0]['Receiver_Id'] == 3)
        self.assertTrue(test[1]['Receiver_Id'] == 2 or test[1]['Receiver_Id'] == 3)
        self.assertTrue(test[0]['Receiver_Id'] != test[1]['Receiver_Id'])

    def test_get_all_friends_receiver(self):
        test = Transaction.get_all_friends_receiver(2)
        self.assertTrue(len(test), 2)
        self.assertTrue(test[0]['Sender_Id'] == 1 or test[0]['Sender_Id'] == 2)
        self.assertTrue(test[1]['Sender_Id'] == 1 or test[1]['Sender_Id'] == 2)
        self.assertTrue(test[0]['Sender_Id'] != test[1]['Sender_Id'])
