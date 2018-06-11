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
                    
data = "" #recklessly utilize global variables
JSONfiles = ""

def getFileList() :
	os.system("sudo rm fileList.save .fileList.swp")
	os.system("ls local/ | sudo nano fileList")
	fileFile = open('fileList.save', 'r+') #open the file containing the list of files generated above 
	nameStr = ""
	jsonStr = "{\n\"files\":\n{\n"
	for line in fileFile: #for each line in the rough File list
		tmp = line
		nameStr += line.strip("\n")
		sep = tmp.split(" ") #create a string array split on space
		for item in sep :
			item = item.strip(" \n")
			tmpFile = open("local/" + item, 'r')
			tmpContents = tmpFile.read()
			tmpContents = tmpContents.replace("\n", "\\n")
			jsonStr += "\"" + item + "\": \"" + tmpContents + "\",\n"
			tmpFile.close()
	jsonStr = jsonStr[:-2] #remove the last comma
	jsonStr += "\n},\n\"fileNames\": \"" + nameStr + "\"\n}\n}"
	fileFile.close()
	return jsonStr

def dataHandler(data):
	print(data)
	if data == "New File":
		data = ""
		try:
			while not data == "+=back" :
				data = client_sock.recv(1024)
				if len(data) == 0: break
				if data == "+=back" : break
				textEditor(data)
		except IOError:
			pass

def textEditor(data):
	dataLines = data.split("\n")	#create a list from the data (Format is filename\ndata...(app must do the same)
	fileName = dataLines[0]			#^ get the filename
	dataLines.pop(0)				#remove the filename from the list
	toWrite = ""					#create an empty string for the data
	for x in dataLines:
		toWrite += ( x + "\n" )		#Reassemble the data and add the '\n's that were removed
	if not fileName.startswith("New File") :
		newFile = open( "local/" + fileName, 'w' )	#create a file in write mode
		newFile.write(toWrite[:-1])		#write the data, ignoring the last \n with [:-1]
		newFile.close()
		print("file local/" + fileName + " saved")
		JSONfiles = getFileList()		#Whenever data is handled, update the file list and resend
		client_sock.send(JSONfiles)
	data = ""						#clear the data variable
	
	
while True:                   
	print("Waiting for connection on RFCOMM channel %d" % port)
	client_sock, client_info = server_sock.accept()		#Accept incoming connections
	print("Accepted connection from ", client_info)
	JSONfiles = getFileList()
	try:
		client_sock.send(JSONfiles)
	except IOError:
		pass
	try:
		while True:
			data = client_sock.recv(1024)
			if len(data) == 0: break
			dataHandler(data)
			JSONfiles = getFileList()		#Whenever data is handled, update the file list and resend
			client_sock.send(JSONfiles)
	except IOError:
		pass
