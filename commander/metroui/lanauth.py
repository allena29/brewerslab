import os
import sys

class lanauth:

	def __init__(self):
		self.localUsers=['192.168.1.34','192.168.1.36']
#		self.localUsers=[]

		self.localUser=False
		try:
			self.localUsers.index( os.environ['REMOTE_ADDR'])
			self.localUser=True
		except ValueError:
			pass
