import json
import falcon
import os
print os.getcwd()

class Resource(object):

    def on_get(self, req, resp, datastore, path):
        req.get_header('TOKEN')


        valid_datastores = ['opdata']
        if datastore not in valid_datastores:
            raise ValueError('Invalid datastore %s: select from %s' % (valid_datastores))

        base = '../heap/' + datastore 
        db = base + '/' + path + '.cvd'
        
        if os.path.exists(db):
            object = {'xxx'}
            o = open(db)
            resp.body = o.read()
            o.close()

            resp.status = falcon.HTTP_200
        else:

            resp.status = falcon.HTTP_404

