import subprocess
import os

myusername = input("Hey there! Who are you? ")
recipient = input("With whomst would like to chat sire? ")

sendercmd = "node ./sender.js " + recipient + " " + myusername
recievercmd = "node ./reciever.js " + myusername
subprocess.call("start " + sendercmd, shell=True)
subprocess.call("start " + recievercmd, shell=True)