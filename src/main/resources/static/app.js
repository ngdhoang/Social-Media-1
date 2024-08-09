// WebSocket client configuration
let wsConfig = {
	url: "ws://localhost:8080/ws",
	topic: "/topic/public",
	app: "/app/channel"
};

let registerForm = {
	"firstName": "",
	"lastName": "",
	"userEmail": "",
	"password": "",
	"confirmPassword": "",
};

let client;
let currentUser = "";

// DOM elements
const elements = {};

function setConnected(connected) {
	elements.connectButton.disabled = connected;
	elements.disconnectButton.disabled = !connected;
	elements.conversationDiv.style.display = connected ? "block" : "none";
	elements.greetingsBody.innerHTML = "";
}

function connect() {
	const token = elements.tokenInput.value.trim();
	if (!token) {
		alert("Please enter a token.");
		return;
	}

	const urlWithToken = `${wsConfig.url}?token=${encodeURIComponent(token)}`;

	client = new StompJs.Client({
		brokerURL: urlWithToken,
		reconnectDelay: 5000, // Tự động kết nối lại sau 5 giây nếu bị ngắt kết nối
		heartbeatIncoming: 4000, // Thiết lập heartbeat để giữ kết nối
		heartbeatOutgoing: 4000
	});

	client.onConnect = (frame) => {
		setConnected(true);
		console.log('Connected: ' + frame);
		client.subscribe(wsConfig.topic, (message) => {
			console.log('Received message: ', message.body);
			showMessage(JSON.parse(message.body));
		});
	};

	client.onWebSocketError = (error) => {
		console.error('WebSocket error', error);
	};

	client.onStompError = (frame) => {
		console.error('Broker reported error: ' + frame.headers['message']);
		console.error('Additional details: ' + frame.body);
	};

	client.activate();
	console.log('Attempting to connect...');
}

function disconnect() {
	if (client) {
		client.deactivate();
		setConnected(false);
		console.log("Disconnected");
	}
}

function sendMessage() {
	if (client && client.connected) {
		const messageDto = {
			groupId: elements.groupIdInput.value,
			groupType: elements.groupTypeInput.value,
			content: elements.contentInput.value,
			msgType: elements.msgTypeInput.value
		};

		console.log('Sending message:', messageDto);

		client.publish({
			destination: wsConfig.app,
			body: JSON.stringify(messageDto)
		});

		// Show the message locally
		showMessage({
			userId: currentUser || "You",
			msgId: "LocalMessage", // Use a local ID for sent messages
			message: { content: messageDto.content },
			reactionQuantity: 0, // Default value
			images: [] // Default value
		});

		elements.contentInput.value = '';
	} else {
		console.error("Client is not connected");
	}
}

function showMessage(message) {
	const messageHtml = `
        <tr>
            <td>
                <strong>User ID:</strong> ${message.userId}<br>
                <strong>Message ID:</strong> ${message.msgId}<br>
                <strong>Message Content:</strong> ${message.message.content}<br>
                <strong>Reaction Quantity:</strong> ${message.reactionQuantity}<br>
                <strong>Images:</strong> ${message.images.map(img => `<img src="${img}" alt="image" style="width:50px;height:auto;">`).join(' ')}
            </td>
        </tr>
    `;
	elements.greetingsBody.innerHTML += messageHtml;
}

function updateConfig() {
	wsConfig.url = elements.wsUrlInput.value || wsConfig.url;
	wsConfig.topic = elements.topicInput.value || wsConfig.topic;
	wsConfig.app = elements.appDestinationInput.value || wsConfig.app;
	console.log("Updated configuration:", wsConfig);
}

function setCurrentUser() {
	currentUser = elements.userNameInput.value;
	console.log("Current user set to:", currentUser);
}

document.addEventListener("DOMContentLoaded", function() {
	// Initialize DOM elements
	console.log('DOM fully loaded and parsed');

	elements.connectButton = document.getElementById("connect");
	elements.disconnectButton = document.getElementById("disconnect");
	elements.sendButton = document.getElementById("send");
	elements.conversationDiv = document.getElementById("conversation");
	elements.greetingsBody = document.getElementById("greetings");
	elements.tokenInput = document.getElementById("token");
	elements.groupIdInput = document.getElementById("groupId");
	elements.groupTypeInput = document.getElementById("groupType");
	elements.contentInput = document.getElementById("content");
	elements.msgTypeInput = document.getElementById("msgType");
	elements.wsUrlInput = document.getElementById("wsUrl");
	elements.topicInput = document.getElementById("topic");
	elements.appDestinationInput = document.getElementById("appDestination");
	elements.userNameInput = document.getElementById("userName");
	elements.setUserButton = document.getElementById("setUser");
	elements.updateConfigButton = document.getElementById("updateConfig");
	elements.loginButton = document.getElementById("login");
	elements.emailInput = document.getElementById("email");
	elements.passwordInput = document.getElementById("password");
	elements.registerButton = document.getElementById("register");
	elements.firstNameInput = document.getElementById("firstName");
	elements.lastNameInput = document.getElementById("lastName");
	elements.userEmailInput = document.getElementById("userEmail");
	elements.userPasswordInput = document.getElementById("userPassword");
	elements.userPasswordConfirmInput = document.getElementById("userPassword");
	elements.registerOtpButton = document.getElementById("registerOtp");
	elements.otpInput = document.getElementById("otp");

	// Event listeners
	elements.connectButton.addEventListener("click", connect);
	elements.disconnectButton.addEventListener("click", disconnect);
	elements.sendButton.addEventListener("click", sendMessage);
	// elements.setUserButton.addEventListener("click", setCurrentUser);
	elements.updateConfigButton.addEventListener("click", updateConfig);
	elements.loginButton.addEventListener("click", function() {
		axios.post('http://localhost:8080/api/v1/auth/authentication', {
			"userEmail": elements.emailInput.value,
			"password": elements.passwordInput.value,
		}, {
			headers: {
				"fingerprinting": "231231243124"
			}
		}).then((res) => {
			console.log(res);
			elements.tokenInput.value = res.data.data.accessToken;})
			.catch((err) => {console.log(err)})
		});

		elements.registerButton.addEventListener("click", function() {
			registerForm.firstName = elements.firstNameInput.value;
			registerForm.lastName = elements.lastNameInput.value;
			registerForm.userEmail = elements.userEmailInput.value;
			registerForm.password = elements.userPasswordInput.value;
			registerForm.confirmPassword = elements.userPasswordConfirmInput.value;
			axios.post('http://localhost:8080/api/v1/auth/register', registerForm, {
				headers: {
					"fingerprinting": "231231243124"
				}
			})
				.then((res) => {console.log(res)})
				.catch((err) => {console.log(err)})
		});

		elements.registerOtpButton.addEventListener("click", function() {
			console.log(registerForm);
			axios.post('http://localhost:8080/api/v1/auth/register/check-otp', {
				...registerForm,
				"otp": elements.otpInput.value
			}, {
				headers: {
					"fingerprinting": "231231243124"
				}
			}).then((res) => {console.log(res)})
				.catch((err) => {console.log(err)})
			});
	setConnected(false);
});
