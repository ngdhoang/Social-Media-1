document.addEventListener("DOMContentLoaded", function () {
    elements.sendFriendRequestButton = document.getElementById("sendFriendRequest");
    elements.friendUserIdInput = document.getElementById("friendUserId");
    elements.friendStatusSelect = document.getElementById("friendStatus");
    elements.processFriendRequestButton = document.getElementById("processFriendRequest");
    elements.friendRequestIdInput = document.getElementById("friendRequestId");
    elements.acceptFriendRequestSelect = document.getElementById("acceptFriendRequest");

    elements.sendFriendRequestButton.addEventListener("click", sendFriendRequest);
    elements.processFriendRequestButton.addEventListener("click", processFriendRequest);

    setConnected(false);
});

function sendFriendRequest() {
    const userReceiveId = elements.friendUserIdInput.value;
    const status = elements.friendStatusSelect.value;

    axios.post('http://localhost:8080/api/v1/friends', {
        userReceiveId: parseInt(userReceiveId),
        status: status
    }, {
        headers: {
            "Authorization": `Bearer ${elements.tokenInput.value}`
        }
    })
        .then((res) => {
            console.log('Friend request sent:', res.data);
            alert('Friend request sent successfully!');
        })
        .catch((err) => {
            console.error('Error sending friend request:', err);
            alert('Failed to send friend request. Please try again.');
        });
}

function processFriendRequest() {
    const friendId = elements.friendRequestIdInput.value;
    const isAccept = elements.acceptFriendRequestSelect.value;

    axios.post('http://localhost:8080/api/v1/friends/accept', {
        friendId: parseInt(friendId),
        isAccept: parseInt(isAccept)
    }, {
        headers: {
            "Authorization": `Bearer ${elements.tokenInput.value}`
        }
    })
        .then((res) => {
            console.log('Friend request processed:', res.data);
            alert('Friend request processed successfully!');
        })
        .catch((err) => {
            console.error('Error processing friend request:', err);
            alert('Failed to process friend request. Please try again.');
        });
}