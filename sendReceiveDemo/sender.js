/*
* Alex Rufer
* Anthony Minerva
* Senior Design Spring 2019
* BQuiet Messaging App: Client
*/

var mongoose = require("mongoose")
var	readline = require("readline"),
	inputInterface = readline.createInterface({input: process.stdin}),
	standard_input = process.stdin

var moment = require("moment")
var now
var rcpt = process.argv[2]
var myUserName = process.argv[3]

mongoose.connect("mongodb://localhost:27017/BQuiet", { useNewUrlParser: true }, function(err, db) {
	if(err) throw err

	console.log("Connected to database, start chatting with " + rcpt +"!")
	process.stdout.write("To " + rcpt + ": ")

	inputInterface.on("line", function(line) {
		now = moment().format()
		db.collection("messages").insertOne( {recipient: rcpt, content: line, sender: myUserName, sentAt: now })
		process.stdout.write("To " + rcpt + ": ")
	}).on("close", function() {
		console.log("And in case I don't see ya! Good morning, good evening, and good night!")
		process.exit(0);
	})
})