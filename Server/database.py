import MySQLdb as mdb
import hashlib

def connect():
   
    return mdb.connect(host="localhost",
                       user="root",
                       passwd="root",
                       db="expo");

def createUser(username, email, password):

	db=connect()
	cursor=db.cursor()
	cursor.execute('INSERT INTO users (username, email, password) VALUES (%s,%s,%s)', (username, email, password))
	db.commit()
	return "success"