import sys
import max7219.led as led

device = led.matrix(cascaded=8,vertical=True)
device.brightness(0)
device.invert(False)

device.show_message( sys.argv[1] )
