from django.conf.urls import url
from . import views

urlpatterns = [
    url(r'^all_transactions', views.expocr_get_all_transactions),
    url(r'^count_sender', views.expocr_transaction_count_sender),
    url(r'^get_entertain', views.expocr_transaction_get_entertain),
    url(r'^get_by_sender', views.expocr_transaction_get_by_sender_id),
    url(r'^update_memo', views.expocr_transaction_update_memo),
    url(r'^create_by_id', views.expocr_transaction_create),
    url(r'^create_by_email', views.expocr_transaction_create_by_email),
    url(r'^delete_between', views.expocr_transaction_delete_between),
    url(r'^get_all_receivers', views.expocr_transaction_get_all_receivers),
    url(r'^get_between', views.expocr_transaction_get_between),
    url(r'^get_transactions_by_t_id', views.expocr_transaction_get_by_t_id),
    url(r'^ocr_test', views.expocr_transaction_ocr_test),
    url(r'^delete_by_id', views.expocr_transaction_delete_by_id),
]
