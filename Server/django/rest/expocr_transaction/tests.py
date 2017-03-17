from django.test import TestCase
from django.core import serializers
from expocr_transaction.models import Transaction
# Create your tests here.



class TransactionTestCase(TestCase):
    def setUp(self):
        Transaction.create_transaction(sender_id="1", receiver_id="2", category="Entertainment", memo="Lego Movie", amount="10.00", date="1000-01-01T00:20:00Z")
        Transaction.create_transaction(sender_id="2", receiver_id="3", category="Food", memo="Chipotle", amount="10.00", date="1000-01-01T00:20:00Z")
        Transaction.create_transaction(sender_id="2", receiver_id="3", category="Apartment", memo="Toilet Paper", amount="10.00", date="1000-01-01T00:20:00Z")
        Transaction.create_transaction(sender_id="2", receiver_id="2", category="Apartment", memo="Rent", amount="-500.00", date="1000-01-01T00:20:00Z")
		

    def test_transaction_is_created(self):
        test = Transaction.get_transaction_by_sender_id(sender_id="1")
        self.assertEqual(len(test), 1)
        self.assertEqual(test[0].Receiver_Id, 2)
        self.assertEqual(test[0].Category, 'Entertainment')


    def test_transaction_get_receivers(self):
    	test = Transaction.get_all_receivers(sender_id="2")
    	self.assertEqual(int(test[0]['Receiver_Id']), 2)
    	self.assertEqual(int(test[1]['Receiver_Id']), 3)
    	self.assertEqual(len(test), 2)

    def test_transaction_get_by_sender_id(self):
    	test = Transaction.get_transaction_by_sender_id(sender_id=2)
    	self.assertEqual(len(test), 3)
    	for transaction in test:
    		self.assertEqual(transaction.Sender_Id, 2)

    def test_transaction_get_transaction_between(self):
    	test = Transaction.get_transaction_between(sender_id=2, receiver_id=3)
    	self.assertEqual(len(test), 2)
    	for transaction in test:
    		self.assertEqual(transaction['Amount'], 10.00)