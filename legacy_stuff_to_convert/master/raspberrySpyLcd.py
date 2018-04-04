#!/usr/bin/python
#
# This class deliberately does nothing - looking back the license isn't 
# defined. Ultimately it traces back to here 
# https://www.raspberrypi.org/forums/viewtopic.php?p=96361#p96361
# but it was inspried by the code here
# https://www.raspberrypi-spy.co.uk/


import os
import sys
import syslog
import threading
import RPi.GPIO as GPIO
import time

class raspberrySpyLcd:


	def __init__(self,backlightTimeout=5):
		return

	def __del__(self):
		return

	def _log(self,msg):
		return


	def _backlightTrigger(self):
		return

	def _backlightTimer(self):
		return

	def _doScrollText(self,text,line,autoBlank,doNotScrollToStart,alignCenter,autoWait,cropText,alert,importance):
		return

	def reinitDisplay(self):
		return

	def enableBacklight(self):
		return

	def scrollText(self,text,line=0,autoBlank=0,doNotScrollToStart=1,alignCenter=0,autoWait=0,cropText=0,alert=1,importance=1,onInterruptMessage=""):
		return

	def _lcdString(self,message):
		return

	def _lcdByte(self,bits, mode):
		return


