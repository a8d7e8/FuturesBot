var net = require('net');
var mqtt = require('mqtt')
var HOST = '127.0.0.1';
var PORT = 8888;
var sock = new net.Socket();
sock.connect(PORT, HOST, function() {
	console.log('CONNECTED TO: ' + HOST + ':' + PORT);
	var d = new Date().getTime();
	var clientId = Math.floor((Math.random() * 10000) + 1);
	var client_T = mqtt.connect('mqtt://user:tradingbot@m2m.tradingbot.com.tw?clientId=VM_' + d.toString() + clientId + 'T');
	var client_K = mqtt.connect('mqtt://user:tradingbot@m2m.tradingbot.com.tw?clientId=VM_' + d.toString() + clientId + 'K');
	var client_S = mqtt.connect('mqtt://user:tradingbot@m2m.tradingbot.com.tw?clientId=VM_' + d.toString() + clientId + 'S');
	client_T.subscribe('BOT/TX00');
	client_T.on('message', function (topic, message) {
		  sock.write(message + ";");
		  console.log(message);
	});
	client_K.subscribe('BOT/KOSPI');
	client_K.on('message', function (topic, message) {
		  sock.write(message + ";");
		  console.log(message);
	});
	client_S.subscribe('BOT/TWN');
	client_S.on('message', function (topic, message) {
		  sock.write(message + ";");
		  console.log(message);
	});
});

sock.on('close', function() {
	console.log('Connection closed');
});

process.stdin.setEncoding('utf8');
process.stdin.on('data', function (chunk, key) {
	sock.destroy();
	console.log("Bye!Bye!");
	process.exit();
});
