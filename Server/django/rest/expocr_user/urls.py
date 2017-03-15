from django.conf.urls import url

from . import views

urlpatterns = [
    url(r'^count_edu', views.expocr_user_count_edu_user),
    url(r'^gmail_user', views.expocr_user_get_gmail_user),
    url(r'^get_user_by_id', views.expocr_user_get_user_by_id),
    url(r'^create', views.expocr_user_create_user),
    url(r'^update_name', views.expocr_user_update_name),
    url(r'^delete', views.expocr_user_delete),
]