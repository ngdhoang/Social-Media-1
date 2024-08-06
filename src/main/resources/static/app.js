const url = "ws://localhost:8080/ws";
const topic = "/topic/public";
const app = "/app/channel";
const client = new StompJs.Client({
	brokerURL: url
});
var buttonConnect;
var buttonDisConnect;
var buttonSend;
var conversation;
var greetings;
var formInput;
var nameInput;
var inputName;
var buttonSendName;
var Name;
client.onConnect = (frame) => {
	setConnected(true);
	console.log('Connected: ' + frame);
	client.subscribe(topic, (greeting) => {
		console.log(greeting)
		showGreeting(greeting.body);
	});
};
client.onWebSocketError = (error) => {
	console.error('Error with websocket', error);
};
client.onStompError = (frame) => {
	console.error('Broker reported error: ' + frame.headers['message']);
	console.error('Additional details: ' + frame.body);
};
function setConnected(connected) {
	buttonConnect.disabled = connected;
	buttonDisConnect.disabled = !connected;
	if (connected) {
		conversationDisplay.style.display = "block";
	}
	else {
		conversationDisplay.style.display = "none";
	}
	greetings.innerHTML = "";
}
function connect() {
	client.activate();
	console.log('Connected');
}
function disconnect() {
	client.deactivate();
	setConnected(false);
	console.log("Disconnected");
}
function sendName() {
	console.log("Sending name");
	client.publish({
		destination: app
		// body: JSON.stringify( nameInput.value)
	});
}
function showGreeting(message) {
	greetings.innerHTML += Name + "<tr><td>"+  message + "</td></tr>";
}
document.addEventListener("DOMContentLoaded", function() {
	buttonConnect = document.getElementById("connect");
	buttonDisConnect = document.getElementById("disconnect");
	buttonSend = document.getElementById("send");
	conversationDisplay = document.getElementById("conversation");
	greetings = document.getElementById("greetings");
	formInput = document.getElementById("form");
	nameInput = document.getElementById("name");
	inputName = document.getElementById("inputName");
	buttonSendName = document.getElementById("sendName");
	buttonConnect.addEventListener("click", (e) => {
		connect();
		e.preventDefault();});
	buttonDisConnect.addEventListener("click", (e) => {
		disconnect();
		e.preventDefault();});
	buttonSend.addEventListener("click", (e) => {
		sendName();
		e.preventDefault();});
	formInput.addEventListener("submit", (e) => e.preventDefault());
	buttonSendName.addEventListener("click", (e) => {
		Name = inputName.value;
		e.preventDefault();}
	);
	setConnected(false);
});