# Auth: Lucas Speer
# This file formats the list of files as a json array and saves it as 


import urllib, os

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
jsonStr = jsonStr[:-1] #remove the last comma
jsonStr += "\n}\n\"fileNames\": \"" + nameStr + "\"\n}"
fileFile.close()
fileFile = open('files.json', 'w') #close and reopen the wifilist file to overwrite the contents with the better formatted list
fileFile.write(jsonStr)
fileFile.close()
