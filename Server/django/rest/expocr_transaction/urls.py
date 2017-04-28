from django.conf.urls import url
from . import views

urlpatterns = [
    url(r'^get_by_sender', views.expocr_transaction_get_by_sender_id),
    url(r'^get_by_receiver', views.expocr_transaction_get_by_receiver_id),
    url(r'^create_by_id', views.expocr_transaction_create),
    url(r'^create_by_email', views.expocr_transaction_create_by_email),
    url(r'^get_all_friends', views.expocr_transaction_get_all_friends),
    url(r'^get_between', views.expocr_transaction_get_between),
    url(r'^get_transactions_by_t_id', views.expocr_transaction_get_by_t_id),
    url(r'^ocr_test', views.expocr_transaction_ocr_test),
    url(r'^delete_by_id', views.expocr_transaction_delete_by_id),
    url(r'^get_by_uid', views.expocr_transaction_get_by_uid)
]
