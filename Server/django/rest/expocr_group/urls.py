from django.conf.urls import url
from . import views

urlpatterns = [
    url(r'^create_group', views.expocr_group_create),
    url(r'^get_group_name', views.expocr_group_get_group_name),
    url(r'^update_group_name', views.expocr_group_update_group_name),
    url(r'^delete_group', views.expocr_group_delete),
    url(r'^add_member_by_email', views.expocr_group_add_member_by_email),
    url(r'^get_members', views.expocr_group_get_members),
    url(r'^get_groups_by_member', views.expocr_group_get_groups_by_member),
    url(r'^delete_member', views.expocr_group_delete_member),
    url(r'^add_transaction_by_email', views.expocr_group_add_transaction_by_email),
    url(r'^add_transaction', views.expocr_group_add_transaction),
    url(r'^get_user_transactions', views.expocr_group_get_user_transactions),
    url(r'^get_group_transactions', views.expocr_group_get_group_transactions),
    url(r'^delete_transaction_by_date', views.expocr_group_delete_by_date),
]
