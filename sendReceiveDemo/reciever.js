/*
* Alex Rufer
* Anthony Minerva
* Senior Design Spring 2019
* BQuiet Messaging App: Server Side v1.3
*/

var myUserName = process.argv[2]

var mongoose = require("mongoose")
var moment = require("moment")
var db

function sleep(ms){
    return new Promise(resolve=>{
        setTimeout(resolve,ms)
    })
}

function displayMessages(unreadMsgs){
    unreadMsgs.forEach(function(msg) {
        console.log(msg.sender + " @ " + moment(msg.sentAt).format("h:mm:ss a") + ": " + msg.content)
    })
}

function deleteMessages(readMsgs) {
    readMsgs.forEach(function(msg) {
        db.collection("messages").deleteOne({_id: msg._id})
    })
}

async function recieveMessages(){
    while(true) {
        db.collection("messages").find({ recipient : myUserName}).toArray(function(err, results) {
            if (err) throw err
                displayMessages(results)
                deleteMessages(results) 
            })
        await sleep(1000)
    }
}

mongoose.connect("mongodb://localhost:27017/BQuiet", { useNewUrlParser: true }, function(err, database) {
    if (err) throw err
    db = database
    console.log("Connected to database")
    console.log("Recieving messages for " + myUserName)
    recieveMessages()
    })