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
            print ret
        return

if __name__ == "__main__": 
    app = web.application(urls, globals())
    app.run()