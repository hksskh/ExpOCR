from django.conf.urls import url

from . import views

urlpatterns = [
    url(r'^get_user_by_id', views.expocr_user_get_user_by_id),
    url(r'^create_facebook_user', views.expocr_create_facebook_user),
    url(r'^try_create', views.expocr_user_try_create_user),
    url(r'^create', views.expocr_user_create_user),
    url(r'^update_name', views.expocr_user_update_name),
    url(r'^delete_friend', views.expocr_user_delete_friend_by_id),
    url(r'^delete', views.expocr_user_delete),
    url(r'^login_by_email', views.expocr_user_login_by_email),
    url(r'^login_with_facebook', views.expocr_login_with_facebook),
    url(r'^request_vericode', views.expocr_user_send_vericode),
    url(r'^check_vericode', views.expocr_user_check_vericode),
    url(r'^change_password', views.expocr_user_change_password),
    url(r'^get_two_users', views.expocr_user_get_two_users_by_id),
    url(r'^get_briefs', views.expocr_user_get_briefs),
    url(r'^upload_avatar', views.expocr_user_upload_avatar),
    url(r'^download_avatar', views.expocr_user_download_avatar),
    ]
