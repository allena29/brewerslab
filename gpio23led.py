import RPi.GPIO as GPIO
import sys
import time

try:
	state=int(sys.argv[1])
except:
	state=1


GPIO.setmode(GPIO.BOARD)
GPIO.setup(23,0)

if state == 4:

	while 1:
		GPIO.output(23,0)
		time.sleep( 0.15)
		GPIO.output(23,1)
		time.sleep( 2.5)
elif state == 3:

	while 1:
		GPIO.output(23,0)
		time.sleep( 0.15)
		GPIO.output(23,1)
		time.sleep(9.85)
elif state == 2:

	while 1:
		time.sleep(0.125)
		GPIO.output(23,1)
		time.sleep(0.25)
		GPIO.output(23,0)
		time.sleep(0.125)
		

else:
	GPIO.output(23,state)

