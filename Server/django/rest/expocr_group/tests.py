from django.test import TestCase
from models import Group, Member, Group_Transaction
from expocr_user.models import User
from datetime import datetime


class GroupTestCase(TestCase):
    current_time = str(datetime.now())

    def test_update_group_name(self):
        test = Group.create_group('group3')
        self.assertEqual(test.G_Name, 'group3')
        Group.update_group_name(g_id=test.G_Id, g_name='group111')
        test = Group.get_group_name(id=test.G_Id)
        self.assertEqual(test[0].G_Name, 'group111')

    def test_delete_group(self):
        group1 = Group.create_group(name="group1")
        user1 = User.create_user(username="joesmith1", email="joesmith1@gmail.com",
                                 password="joesmith1password")
        User.create_user(username="joesmith2", email="joesmith2@gmail.com",
                                 password="joesmith2password")
        User.create_user(username="joesmith3", email="joesmith3@gmail.com",
                                 password="joesmith3password")

        Member.add_member_by_email(group1.G_Id, "joesmith1@gmail.com")
        Member.add_member_by_email(group1.G_Id, "joesmith2@gmail.com")
        Member.add_member_by_email(group1.G_Id, "joesmith3@gmail.com")

        Group_Transaction.add_transaction(1, 1, "Food", "Panda Express", 20.00,
                                                         self.current_time)
        Group_Transaction.add_transaction(1, 2, "Food", "Panda Express", -10.00,
                                                         self.current_time)
        Group_Transaction.add_transaction(1, 3, "Food", "Panda Express", -10.00,
                                                         self.current_time)
        Group.delete_group(group1.G_Id, group1.G_Name)
        self.assertEqual(len(Group_Transaction.get_user_transactions(group1.G_Id, user1[0].U_Id)), 0)

    def test_get_groups(self):
        group1 = Group.create_group(name="group1")
        group2 = Group.create_group(name="group2")
        user1 = User.create_user(username="joesmith1", email="joesmith1@gmail.com",
                                 password="joesmith1password")
        Member.add_member_by_email(group1.G_Id, "joesmith1@gmail.com")
        Member.add_member_by_email(group2.G_Id, "joesmith1@gmail.com")

        test = Member.get_groups(user1[0].U_Id)
        self.assertEqual(test.count(), 2)
        Member.delete_member(group1.G_Id, user1[0].U_Id)
        test = Member.get_groups(user1[0].U_Id)
        self.assertEqual(test.count(), 1)

    def test_get_group_transactions(self):
        group1 = Group.create_group(name="group1")
        user1 = User.create_user(username="joesmith1", email="joesmith1@gmail.com",
                                 password="joesmith1password")
        user2 = User.create_user(username="joesmith2", email="joesmith2@gmail.com",
                         password="joesmith2password")
        user3 = User.create_user(username="joesmith3", email="joesmith3@gmail.com",
                         password="joesmith3password")

        Member.add_member_by_email(group1.G_Id, "joesmith1@gmail.com")
        Member.add_member_by_email(group1.G_Id, "joesmith2@gmail.com")
        Member.add_member_by_email(group1.G_Id, "joesmith3@gmail.com")

        test = Group_Transaction.get_group_transactions(group1.G_Id)
        self.assertEqual(test.count(), 0)

        Group_Transaction.add_transaction(group1.G_Id, user1[0].U_Id, "Food", "Panda Express", 20.00,
                                          self.current_time)
        Group_Transaction.add_transaction(group1.G_Id, user2[0].U_Id, "Food", "Panda Express", -10.00,
                                          self.current_time)
        Group_Transaction.add_transaction(group1.G_Id, user3[0].U_Id, "Food", "Panda Express", -10.00,
                                          self.current_time)
        test2 = Group_Transaction.get_group_transactions(group1.G_Id)
        self.assertEqual(test2.count(), 3)

    def test_get_user_transactions(self):
        group1 = Group.create_group(name="group1")
        user1 = User.create_user(username="joesmith1", email="joesmith1@gmail.com",
                                 password="joesmith1password")

        Member.add_member_by_email(group1.G_Id, "joesmith1@gmail.com")


        transaction1 = Group_Transaction.add_transaction(group1.G_Id, user1[0].U_Id, "Food", "Panda Express", 10.00,
                                          self.current_time)
        Group_Transaction.add_transaction(group1.G_Id, user1[0].U_Id, "Food", "Pizza Hut", -10.00,
                                          self.current_time)
        Group_Transaction.add_transaction(group1.G_Id, user1[0].U_Id, "Food", "Arbys", 5.00,
                                          self.current_time)
        self.assertEqual(len(Group_Transaction.get_user_transactions(group1.G_Id, user1[0].U_Id)), 3)
        Group_Transaction.delete_transaction(group1.G_Id, transaction1.GT_Id)
        self.assertEqual(len(Group_Transaction.get_user_transactions(group1.G_Id, user1[0].U_Id)), 2)

    def test_get_members(self):
        group1 = Group.create_group(name="group1")
        user1 = User.create_user(username="joesmith1", email="joesmith1@gmail.com",
                                 password="joesmith1password")
        User.create_user(username="joesmith2", email="joesmith2@gmail.com",
                                 password="joesmith2password")
        User.create_user(username="joesmith3", email="joesmith3@gmail.com",
                                 password="joesmith3password")

        Member.add_member_by_email(group1.G_Id, "joesmith1@gmail.com")
        Member.add_member_by_email(group1.G_Id, "joesmith2@gmail.com")
        Member.add_member_by_email(group1.G_Id, "joesmith3@gmail.com")
        test = Member.get_members(group1.G_Id)
        self.assertEqual(len(test), 3)
        test = Member.delete_member(group1.G_Id, user1[0].U_Id)
        self.assertEqual(len(test), 2)

    def test_delete_transaction_by_date(self):
        group1 = Group.create_group(name="group1")
        user1 = User.create_user(username="joesmith1", email="joesmith1@gmail.com",
                                 password="joesmith1password")

        Member.add_member_by_email(group1.G_Id, "joesmith1@gmail.com")
        Group_Transaction.add_transaction(group1.G_Id, user1[0].U_Id, "Food", "Panda Express", 10.00,
                                                         '2017-04-04 08:00:01')
        Group_Transaction.add_transaction(group1.G_Id, user1[0].U_Id, "Food", "Pizza Hut", -10.00,
                                          self.current_time)
        test = Group_Transaction.get_user_transactions(group1.G_Id, user1[0].U_Id)
        self.assertEqual(len(test), 2)
        Group_Transaction.delete_transaction_by_date('2017-04-04 08:00:01')
        test = Group_Transaction.get_user_transactions(group1.G_Id, user1[0].U_Id)
        self.assertEqual(len(test), 1)


