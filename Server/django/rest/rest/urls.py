"""rest URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/1.10/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  url(r'^$', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  url(r'^$', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.conf.urls import url, include
    2. Add a URL to urlpatterns:  url(r'^blog/', include('blog.urls'))
"""
from django.conf.urls import url, include
from django.contrib import admin
from expocr_user import views

urlpatterns = [
    url(r'^admin/', admin.site.urls),
    url(r'^user/test', views.expocr_user_test),
    url(r'^user/count_edu', views.expocr_user_count_edu_user),
    url(r'^user/gmail_user', views.expocr_user_get_gmail_user),
    url(r'^user/get_user_by_id', views.expocr_user_get_user_by_id),
    url(r'^user/create', views.expocr_user_create_user),
    url(r'^user/update_name', views.expocr_user_update_name),
    url(r'^user/delete', views.expocr_user_delete)
]
