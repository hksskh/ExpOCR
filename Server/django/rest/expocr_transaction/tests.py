from django.test import TestCase
from django.core import serializers
from expocr_transaction.models import Transaction
# Create your tests here.



class TransactionTestCase(TestCase):
    def setUp(self):
        Transaction.create_transaction(sender_id="1", receiver_id="2", category="Entertainment", memo="Lego Movie", amount="10.00", date="1000-01-01T00:20:00Z")

    def test_transaction_is_created(self):
        test = serializers.serialize('json', Transaction.get_transaction_by_sender_id(sender_id="1"))
        self.assertEqual(test, '[{"model": "expocr_transaction.transaction", "pk": 1, "fields": {"Sender_Id": 1, "Receiver_Id": 2, "Category": "Entertainment", "Memo": "Lego Movie", "Amount": "10.00", "Date": "1000-01-01T00:20:00Z"}}]')