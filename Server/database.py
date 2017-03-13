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
	cursor2 = db.cursor()
	cursor3 = db.cursor()
	cursor2.execute('SELECT U_Name FROM USERS WHERE Email =%s',email)
	if (cursor2.fetchone()!=None):
	    return "email already exists"
	cursor3.execute('SELECT Email FROM USERS WHERE U_NAME =%s',username)
	if (cursor3.fetchone()!=None):
	    return "username already exists"
	cursor.execute('INSERT INTO USERS (U_NAME, Email, Password) VALUES (%s,%s,%s)', (username, email, password))
	db.commit()
	return "true"
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
def comparePasswords(email,password):
	db=connect()
	cursor=db.cursor()
	cursor.execute('SELECT Password FROM USERS WHERE Email =%s', email)
	db.commit()
	val = cursor.fetchone()
	if (val==None):
	    return false
	if (val[0] == password):
	    return "true"
	return "false"
def addTransaction(sender, receiver, category, memo, amount, date):
	db=connect()
	cursor=db.cursor()
	stmt='INSERT INTO TRANSACTIONS (Sender_Id, Receiver_Id, Category, Memo, Amount, Date) VALUES (%s, %s, %s, %s, %s, %s)'
	data=(sender, receiver, category, memo, amount, date)
	cursor.execute(stmt, data)
	db.commit()
	return "success"