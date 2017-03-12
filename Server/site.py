import web
import MySQLdb as mdb
import database

urls = ('/', 'index', '/add', 'add')

class index:
    def GET(self):
        return "Hello, world!"

class add:
    def GET(self):
        return "add"
    
    def POST(self):
        input=web.input()
        if(input.funcname=="createUser"):
            ret=database.createUser(input.username, input.email, input.password)
            return ret
	    if (input.funcname == "comparePassword"):
	        ret = database.comparePasswords(input.email, input.password);
	        return ret
        if(input.funcname=="addTransaction"):
        	ret=database.addTransaction(input.sender, input.receiver, input.category, input.memo, input.amount, input.date)
		if (input.funcname=="getTransaction"):
			ret = database.getTransacation(input.tid)
			print ret
		if (input.funcname=="getTransactions"):
			ret = database.getTransactions(input.uid)
		if (input.funcname == "getGroup"):
			ret = database.getGroup(input.gid)
		if (input.funcname == "getPassword"):
			ret = database.getPassword(input.email)
		return
if __name__ == "__main__": 
    app = web.application(urls, globals())
    app.run()