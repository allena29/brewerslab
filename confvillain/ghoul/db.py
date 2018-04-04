import json
import falcon

class Resource(object):

    def on_get(self, req, resp):
        doc = {}

        resp.body = json.dumps(doc)

        resp.status = falcon.HTTP_200
