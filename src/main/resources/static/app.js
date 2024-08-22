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

    // Verify token and get user info
    axios.get('http://localhost:8080/api/v1/auth/verify-token', {
        headers: {
            "Authorization": `Bearer ${token}`
        }
    }).then((res) => {
        const userData = res.data.data;
        updateUserInterface(userData);
        wsConfig.topic = `/channel/app/${userData.userId}`;
        connectWebSocket(token);
    }).catch((err) => {
        console.error("Token verification failed:", err);
        alert("Invalid token. Please log in again.");
    });
}

function connectWebSocket(token) {
    const urlWithToken = `${wsConfig.url}?token=${encodeURIComponent(token)}`;

    client = new StompJs.Client({
        brokerURL: urlWithToken,
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000
    });

    client.onConnect = (frame) => {
        setConnected(true);
        console.log('Connected: ' + frame);
        startPingPong(); // Start ping-pong
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

function updateUserInterface(userData) {
    const { userId, firstName, lastName, userEmail, avatar, dob, phoneNumber, homeTown, schoolName, workPlace, gender } = userData;

    // Xử lý các giá trị null hoặc không có
    const fullName = `${firstName || "Chưa cập nhật"} ${lastName || ""}`;
    const email = userEmail || "Chưa cập nhật";
    const avatarUrl = avatar || "URL của avatar mặc định";
    const dateOfBirth = dob.value || "Chưa cập nhật";
    const phone = phoneNumber.value || "Chưa cập nhật";
    const home = homeTown.value || "Chưa cập nhật";
    const school = schoolName.value || "Chưa cập nhật";
    const work = workPlace.value || "Chưa cập nhật";
    const userGender = gender.value || "Chưa cập nhật";

    // Cập nhật thông tin người dùng lên giao diện
    elements.userInfoDiv.innerHTML = `
        <h3>User Information</h3>
        <img src="${avatarUrl}" alt="Avatar" style="width: 100px; height: 100px;">
        <p><strong>User ID:</strong> ${userId}</p>
        <p><strong>Name:</strong> ${fullName}</p>
        <p><strong>Email:</strong> ${email}</p>
        <p><strong>Date of Birth:</strong> ${dateOfBirth}</p>
        <p><strong>Phone Number:</strong> ${phone}</p>
        <p><strong>Home Town:</strong> ${home}</p>
        <p><strong>School Name:</strong> ${school}</p>
        <p><strong>Work Place:</strong> ${work}</p>
        <p><strong>Gender:</strong> ${userGender}</p>
    `;

    currentUser = userId;
}


function disconnect() {
    if (client) {
        client.deactivate();
        stopPingPong(); // Stop ping-pong
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

        showMessage({
            userId: currentUser || "You",
            msgId: "Không có Id :v",
            message: {content: messageDto.content},
            reactionQuantity: 0,
            images: []
        });

        elements.contentInput.value = '';
    } else {
        console.error("Client is not connected");
    }
}

function showMessage(message) {
    console.log("Displaying message:", message);

    const images = message.images ? message.images : [];
    const isError = message.message?.msgType === "ERROR";
    const messageStyle = isError ? 'color: red;' : '';

    const messageHtml = `
        <tr style="${messageStyle}">
            <td>
                <strong>User ID:</strong> ${message.userAuthId || "N/A"}<br>
                <strong>Message ID:</strong> ${message.msgId || "N/A"}<br>
                <strong>Message Content:</strong> ${message.message?.content || "N/A"}<br>
                <strong>Message Type:</strong> ${message.msgType || "N/A"}<br>
                <strong>Reaction Quantity:</strong> ${message.reactionQuantity || 0}<br>
                <strong>Images:</strong> ${images.length > 0 ? images.map(img => `<img src="${img}" alt="image" style="width:50px;height:auto;">`).join(' ') : "No images"}
            </td>
        </tr>
    `;
    console.log("Generated HTML:", messageHtml);

    elements.greetingsBody.innerHTML += messageHtml;
}

function updateConfig() {
    wsConfig.url = elements.wsUrlInput.value || wsConfig.url;
    wsConfig.topic = elements.topicInput.value || wsConfig.topic;
    wsConfig.app = elements.appDestinationInput.value || wsConfig.app;
    console.log("Updated configuration:", wsConfig);
}

document.addEventListener("DOMContentLoaded", function () {
    console.log('DOM fully loaded and parsed');

    // Initialize elements
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
    // elements.updateConfigButton = document.getElementById("updateConfig");
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
    elements.userInfoDiv = document.getElementById("userInfo");

    // Event listeners
    elements.connectButton.addEventListener("click", connect);
    elements.disconnectButton.addEventListener("click", disconnect);
    elements.sendButton.addEventListener("click", sendMessage);
    // elements.updateConfigButton.addEventListener("click", updateConfig);

    elements.loginButton.addEventListener("click", function () {
        axios.post('http://localhost:8080/api/v1/auth/authentication', {
            "userEmail": elements.emailInput.value,
            "password": elements.passwordInput.value,
        }, {
            headers: {
                "fingerprinting": "231231243124"
            }
        }).then((res) => {
            console.log(res);
            const token = res.data.data.accessToken;
            elements.tokenInput.value = token;
            connect(); // Automatically connect after successful login
        }).catch((err) => {
            console.log(err);
            alert("Login failed. Please try again.");
        });
    });

    elements.registerButton.addEventListener("click", function () {
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
            .then((res) => {
                console.log(res);
                alert("Registration successful. Please check your email for OTP.");
            })
            .catch((err) => {
                console.log(err);
                alert("Registration failed. Please try again.");
            });
    });

    elements.registerOtpButton.addEventListener("click", function () {
        console.log(registerForm);
        axios.post('http://localhost:8080/api/v1/auth/register/check-otp', {
            ...registerForm,
            "otp": elements.otpInput.value
        }, {
            headers: {
                "fingerprinting": "231231243124"
            }
        }).then((res) => {
            console.log(res);
            alert("OTP verification successful. You can now log in.");
        })
            .catch((err) => {
                console.log(err);
                alert("OTP verification failed. Please try again.");
            });
    });

    setConnected(false);
});

//ping pong
function sendPing() {
    if (client && client.connected) {
        client.publish({
            destination: '/app/ping',
            body: JSON.stringify({ message: 'ping' })
        });
        console.log('Sent ping message');
    } else {
        console.warn('Cannot send ping, client is not connected');
    }
}

let pingInterval;

function startPingPong() {
    pingInterval = setInterval(sendPing, 30000); // 30 seconds
}

function stopPingPong() {
    if (pingInterval) {
        clearInterval(pingInterval);
        pingInterval = null;
        console.log('Stopped ping-pong');
    }
}



