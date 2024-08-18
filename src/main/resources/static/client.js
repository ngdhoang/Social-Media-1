let peerConnection;
let localStream;
let remoteStream;
let groupCallId;

const groupCall = document.getElementById('groupCall');
groupCall.addEventListener('onchange', e => {
    groupCallId = e.target.value;
})

let appEndpoint = "/channel/signal/"

function sendGroupId() {
    if (client && client.connected) {
        const messageDto = {
            groupId: groupCallId,
            groupType: "PERSONAL"
        };

        client.publish({
            destination: appEndpoint,
            body: JSON.stringify(messageDto)
        });
    } else {
        console.error("Client is not connected");
    }
}

function initialize() {
    let configuration = null;

    peerConnection = new RTCPeerConnection(configuration);

    // Setup ice handling
    peerConnection.onicecandidate = function(event) {
        if (event.candidate) {
            send({
                event : "candidate",
                data : event.candidate
            });
        }
    };

    // getting local video stream
    navigator.mediaDevices.getUserMedia({ video: true, audio: true })
        .then(stream => {
            localStream = stream;
            document.getElementById("localVideo").srcObject = localStream;
            localStream.getTracks().forEach(track => peerConnection.addTrack(track, localStream));
        })
        .catch(e => console.log('getUserMedia() error: ', e));

    peerConnection.ontrack = function(event) {
        document.getElementById("remoteVideo").srcObject = event.streams[0];
        remoteStream = event.streams[0];
    };
}

function createOffer() {
    peerConnection.createOffer()
        .then(offer => {
            send({
                event : "offer",
                data : offer
            });
            return peerConnection.setLocalDescription(offer);
        })
        .catch(error => console.error("Error creating an offer:", error));
}

function handleOffer(offer) {
    peerConnection.setRemoteDescription(new RTCSessionDescription(offer))
        .then(() => peerConnection.createAnswer())
        .then(answer => {
            peerConnection.setLocalDescription(answer);
            send({
                event : "answer",
                data : answer
            });
        })
        .catch(error => console.error("Error handling offer:", error));
}

function handleCandidate(candidate) {
    peerConnection.addIceCandidate(new RTCIceCandidate(candidate))
        .catch(error => console.error("Error adding ice candidate:", error));
}

function handleAnswer(answer) {
    peerConnection.setRemoteDescription(new RTCSessionDescription(answer))
        .then(() => console.log("Connection established successfully!"))
        .catch(error => console.error("Error handling answer:", error));
}