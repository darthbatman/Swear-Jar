var twilio = require('twilio');
var app = require('express')();
var http = require('http').Server(app);
var bodyParser  = require('body-parser');

var client = new twilio.RestClient('********','*******');

var allUsers = ["nikhil", "ali"];
var phoneNumbers = ["+17323310873", "+17323221591"];

var swearArray = [];

var leaderboard = "";

app.use(bodyParser.urlencoded({
  extended: true
}));

app.use(bodyParser.json());

app.get('/', function(req, res){
	res.send('<p>Hello World</p>');
});

app.get('/smsReply', function(req, res){
	console.log('Message Sent to Twilio Number');
	res.send('Message Sent to Twilio Number');
	console.log(req.query.From + ": " + req.query.Body);
	client.sendSms({
					to: req.query.From,
					from: '+17324918329',
					body: 'Thanks for texting swearJar'
				}, function (err, data) {
					//console.log(err);
					//console.log(data);
				});
});

app.post('/swear', function(req, res){
	res.send("hello");
	//console.log(req);
	console.log(req.body);

	var ploc = 0;

	for (var g = 0; g < phoneNumbers.length; g++){
		if (req.body.user.toUpperCase() == allUsers[g].toUpperCase()){
			ploc = g;
			break;
		}
	}

	client.sendSms({
					to: phoneNumbers[ploc],
					from: '+17324918329',
					body: 'Oops! You swore! 50 cents have been donated to charity.'
				}, function (err, data) {
					//console.log(err);
					//console.log(data);
				});

	var alreadyExists = false;
	var loc = 0;

	for (var p = 0; p < swearArray.length; p++){
		if (swearArray[p].user == req.body.user){
			alreadyExists = true;
			loc = p;
		}
	}

	if (!alreadyExists) {
		var swearObj = {
			user: req.body.user,
			swears: 1
		};
		swearArray.push(swearObj);
	}
	else {
		swearArray[loc].swears++;
	}

	console.log("swearArray: " + swearArray);

	console.log(sortByKey(swearArray, "swears"));

	for (var u = 0; u < swearArray.length; u++){
		leaderboard += sortByKey(swearArray, "swears")[u].user + '\n';
	}

});

http.listen(8080, function(){
	console.log('listening on *:8080');
	swearArray = [];
});

function sortByKey(array, key) {
    return array.sort(function(a, b) {
        var x = a[key]; var y = b[key];
        return ((x > y) ? -1 : ((x < y) ? 1 : 0));
    });
}

var sortSwearArray = function(){

	var tempArray = [];
	var numSwearsArr = [];

	for (var o = 0; o < swearArray.length; o++){
		numSwearsArr[o] = swearArray[o].swears;
		console.log(swearArray[o]);
	}

	numSwearsArr.sort();

	for (var o = 0; o < swearArray.length; o++){
		for (var p = 0; p < swearArray.length; p++){
			if (numSwearsArr[o] == swearArray[p]){
				tempArray.push(swearArray[p]);
				break;
			}
		}
	}

	return {
		arr: tempArray
	};

};

setInterval(function(){ 
	
	for (var j = 0; j < swearArray.length; j++){
		client.sendSms({
					to: phoneNumbers[j],
					from: '+17324918329',
					body: sortByKey(swearArray, "swears")[0].user + ' is in first place! Watch your mouth to take the lead!'
				}, function (err, data) {
					//console.log(err);
					//console.log(data);
				});
	}

}, 45000);

setInterval(function(){ 
	
	for (var j = 0; j < swearArray.length; j++){
		client.sendSms({
					to: phoneNumbers[j],
					from: '+17324918329',
					body: 'Your friends don\'t seem to have foul mouths. Here\'s the current leaderboard.\n' + leaderboard + ' Follow your friends\' example to have a chance at winning.'
				}, function (err, data) {
					//console.log(err);
					//console.log(data);
				});
	}

}, 90000);

setInterval(function(){ 
	
	for (var j = 0; j < swearArray.length; j++){
		client.sendSms({
					to: phoneNumbers[j],
					from: '+17324918329',
					body: 'A lot of your friends swore less than you today. You can do it too!'
				}, function (err, data) {
					//console.log(err);
					//console.log(data);
				});
	}

}, 30000);