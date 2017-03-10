import MySQLdb as mdb
import hashlib

def connect():
   
    return mdb.connect(host="localhost",
                       user="root",
                       passwd="root",
                       db="test");

def createUser(username, email, password):

	db=connect()
	cursor=db.cursor()
	cursor.execute('INSERT INTO USERS (username, email, password) VALUES (%s,%s,%s)', (username, email, password))
	db.commit()
	return "success"
def getTransaction(tid):
	db=connect()
	cursor=db.cursor()
	cursor.execute('SELECT * FROM TRANSACTIONS WHERE T_id = %s', tid)
	db.commit()
	return cursor.fetchone()
def getGroup(gid):
	db=connect()
	cursor=db.cursor()
	cursor.execute('SELECT * FROM GROUP WHERE G_id = %s', gid)
	db.commit()
	return cursor.fetchone()
def getPassword(email):
	db=connect()
	cursor=db.cursor()
	cursor.execute('SELECT Password FROM USERS WHERE Email =%s', email)
	db.commit()
	return cursor.fetchone()
def addTransaction(sender, receiver, category, memo, amount, date):
	db=connect()
	cursor=db.cursor()
	stmt='INSERT INTO TRANSACTIONS (Sender_Id, Receiver_Id, Category, Memo, Amount, Date) VALUES (%s, %s, %s, %s, %s, %s)'
	data=(sender, receiver, category, memo, amount, date)
	cursor.execute(stmt, data)
	db.commit()
	return "success"