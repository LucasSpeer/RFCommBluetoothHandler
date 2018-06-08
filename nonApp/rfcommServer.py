# Auth: Lucas Speer
# For use with github.com/LucasSpeer/RFCommBluetoothHandler
# This file acts the server-side controller for the RFComm connection
# add the line 'sudo python /pathToFile/rfcommServer.py &' to /etc/rc.local to run at start

from bluetooth import *
import os, subprocess
import time

# Set up the bluetooth socket as a server
server_sock=BluetoothSocket( RFCOMM )
server_sock.bind(("",PORT_ANY))
server_sock.listen(1)

port = server_sock.getsockname()[1]
# Must be the same as the Android app
uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"

advertise_service( server_sock, "RFCommServer",
                   service_id = uuid,
                   service_classes = [ uuid, SERIAL_PORT_CLASS ],
                   profiles = [ SERIAL_PORT_PROFILE ], 
#                   protocols = [ OBEX_UUID ] 
                    )
                    
	
def dataHandler(data):
	if data == "textEditor":
		data = ""
		try:
			while not data.startswith("+=back") :
				data = client_sock.recv(1024)
				if len(data) == 0: break
				textEditor(data)
		except IOError:
			pass

def textEditor(data):
	if not data.startswith("textEditor") :
		dataLines = data.split("\n")
		fileName = dataLines[0]
		dataLines.pop(0)
		toWrite = ""
		for x in dataLines:
			toWrite += ( x + "\n" )
		newFile = open(fileName, 'w')
		newFile.write(toWrite[:-1])
		newFile.close()
		data = ""
	
while True:                   
	print("Waiting for connection on RFCOMM channel %d" % port)
	client_sock, client_info = server_sock.accept()		#Accept incoming connections
	print("Accepted connection from ", client_info)
	
	try:
		client_sock.send("test")
	except IOError:
		pass
	try:
		while True:
			data = client_sock.recv(1024)
			if len(data) == 0: break
			dataHandler(data)
	except IOError:
		pass
