from django.conf.urls import url
from . import views

urlpatterns = [
    url(r'^all_groups', views.expocr_get_all_groups),
    url(r'^all_members', views.expocr_get_all_members),
    url(r'^all_group_transactions', views.expocr_get_all_group_transactions),
    url(r'^create_group', views.expocr_group_create),
    url(r'^update_group_name', views.expocr_group_update_group_name),
    url(r'^delete_group', views.expocr_group_delete),
    url(r'^add_member_by_email', views.expocr_group_add_member_by_email),
    url(r'^add_member', views.expocr_group_add_member),
    url(r'^get_members', views.expocr_group_get_members),
    url(r'^get_groups_by_member', views.expocr_group_get_groups_by_member),
    url(r'^delete_member', views.expocr_group_delete_member),
    url(r'^add_transaction', views.expocr_group_add_transaction),
    url(r'^get_user_transactions', views.expocr_group_get_user_transactions),
    url(r'^get_group_transactions', views.expocr_group_get_group_transactions),
    url(r'^get_group_by_transaction', views.expocr_group_get_group_by_transaction),
    url(r'^delete_transaction', views.expocr_group_delete_transaction),
    url(r'^net_balance', views.expocr_net_balance),
]
