import falcon

from db import Resource as dbres


api = application = falcon.API()



db_handler = dbres()
api.add_route('/v1/db', db_handler)

