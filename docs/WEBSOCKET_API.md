# CampusCircle WebSocket API Documentation

## Connection

### Endpoint
```
ws://localhost:8081/ws
```

### With SockJS Fallback
```
http://localhost:8081/ws
```

### Authentication
Include JWT token in the CONNECT frame headers:
```javascript
const socket = new SockJS('http://localhost:8081/ws');
const stompClient = Stomp.over(socket);

stompClient.connect(
  { 'Authorization': 'Bearer ' + accessToken },
  onConnected,
  onError
);
```

---

## Message Destinations

### Client → Server (Send Messages)

| Destination | Description | Payload |
|-------------|-------------|---------|
| `/app/chat.send` | Send direct message | `{ recipient, content, messageType }` |
| `/app/chat.typing` | Typing indicator | `{ recipient, typing: boolean }` |
| `/app/chat.read` | Read receipt | `{ recipient, conversationId }` |
| `/app/channel.subscribe/{channelId}` | Subscribe to channel | - |
| `/app/status.check` | Check users online status | `["user1", "user2"]` |
| `/app/status.online` | Get all online users | - |
| `/app/ping` | Keep-alive ping | - |

### Server → Client (Subscriptions)

| Destination | Description | When |
|-------------|-------------|------|
| `/user/queue/notifications` | Personal notifications | New notification |
| `/user/queue/messages` | Direct messages | New DM received |
| `/user/queue/typing` | Typing indicators | Someone is typing |
| `/user/queue/read-receipts` | Read receipts | Message was read |
| `/user/queue/status` | Online status response | After `/app/status.check` |
| `/user/queue/online-users` | Online users list | After `/app/status.online` |
| `/user/queue/pong` | Ping response | After `/app/ping` |
| `/topic/online-status` | Global online/offline events | User connects/disconnects |
| `/topic/channel/{channelId}` | Channel updates | New posts, etc. |
| `/topic/channel/{channelId}/posts` | New posts in channel | Post created |
| `/topic/post/{postId}/comments` | New comments on post | Comment created |
| `/topic/votes/{contentType}/{contentId}` | Vote updates | Vote changed |
| `/topic/university/{universityId}` | University broadcasts | Announcements |

---

## Message Types

```javascript
const MESSAGE_TYPES = {
  NOTIFICATION: 'NOTIFICATION',
  DIRECT_MESSAGE: 'DIRECT_MESSAGE',
  TYPING: 'TYPING',
  ONLINE_STATUS: 'ONLINE_STATUS',
  NEW_POST: 'NEW_POST',
  NEW_COMMENT: 'NEW_COMMENT',
  VOTE_UPDATE: 'VOTE_UPDATE',
  CHANNEL_UPDATE: 'CHANNEL_UPDATE',
  READ_RECEIPT: 'READ_RECEIPT',
  ERROR: 'ERROR'
};
```

---

## JavaScript Client Example

```javascript
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

class WebSocketClient {
  constructor(token) {
    this.token = token;
    this.stompClient = null;
    this.subscriptions = {};
  }

  connect(onConnected, onError) {
    const socket = new SockJS('http://localhost:8081/ws');
    this.stompClient = Stomp.over(socket);

    this.stompClient.connect(
      { 'Authorization': `Bearer ${this.token}` },
      () => {
        console.log('WebSocket connected');
        this.subscribeToPersonalQueues();
        this.subscribeToOnlineStatus();
        if (onConnected) onConnected();
      },
      (error) => {
        console.error('WebSocket error:', error);
        if (onError) onError(error);
      }
    );
  }

  subscribeToPersonalQueues() {
    // Notifications
    this.subscriptions.notifications = this.stompClient.subscribe(
      '/user/queue/notifications',
      (message) => this.handleNotification(JSON.parse(message.body))
    );

    // Direct messages
    this.subscriptions.messages = this.stompClient.subscribe(
      '/user/queue/messages',
      (message) => this.handleDirectMessage(JSON.parse(message.body))
    );

    // Typing indicators
    this.subscriptions.typing = this.stompClient.subscribe(
      '/user/queue/typing',
      (message) => this.handleTyping(JSON.parse(message.body))
    );

    // Read receipts
    this.subscriptions.readReceipts = this.stompClient.subscribe(
      '/user/queue/read-receipts',
      (message) => this.handleReadReceipt(JSON.parse(message.body))
    );
  }

  subscribeToOnlineStatus() {
    this.subscriptions.onlineStatus = this.stompClient.subscribe(
      '/topic/online-status',
      (message) => this.handleOnlineStatus(JSON.parse(message.body))
    );
  }

  subscribeToChannel(channelId, callback) {
    const key = `channel_${channelId}`;
    this.subscriptions[key] = this.stompClient.subscribe(
      `/topic/channel/${channelId}/posts`,
      (message) => callback(JSON.parse(message.body))
    );
  }

  subscribeToPostComments(postId, callback) {
    const key = `post_${postId}`;
    this.subscriptions[key] = this.stompClient.subscribe(
      `/topic/post/${postId}/comments`,
      (message) => callback(JSON.parse(message.body))
    );
  }

  // Send direct message
  sendMessage(recipient, content, messageType = 'TEXT') {
    this.stompClient.send('/app/chat.send', {}, JSON.stringify({
      recipient,
      content,
      messageType
    }));
  }

  // Send typing indicator
  sendTyping(recipient, isTyping) {
    this.stompClient.send('/app/chat.typing', {}, JSON.stringify({
      recipient,
      typing: isTyping
    }));
  }

  // Send read receipt
  sendReadReceipt(recipient, conversationId) {
    this.stompClient.send('/app/chat.read', {}, JSON.stringify({
      recipient,
      conversationId
    }));
  }

  // Check online status of users
  checkOnlineStatus(usernames) {
    this.stompClient.subscribe('/user/queue/status', (message) => {
      console.log('Online status:', JSON.parse(message.body));
    });
    this.stompClient.send('/app/status.check', {}, JSON.stringify(usernames));
  }

  // Ping to keep connection alive
  ping() {
    this.stompClient.send('/app/ping', {}, '');
  }

  // Handlers (override these in your app)
  handleNotification(notification) {
    console.log('New notification:', notification);
  }

  handleDirectMessage(message) {
    console.log('New message:', message);
  }

  handleTyping(typing) {
    console.log('Typing:', typing);
  }

  handleReadReceipt(receipt) {
    console.log('Read receipt:', receipt);
  }

  handleOnlineStatus(status) {
    console.log('Online status change:', status);
  }

  disconnect() {
    if (this.stompClient) {
      this.stompClient.disconnect();
    }
  }
}

export default WebSocketClient;
```

---

## REST API for Online Status

| Endpoint | Method | Description |
|----------|--------|-------------|
| `GET /api/online-status` | GET | Get online stats and user list |
| `GET /api/online-status/count` | GET | Get online user count |
| `GET /api/online-status/users` | GET | Get list of online usernames |
| `GET /api/online-status/check/{username}` | GET | Check if specific user is online |
| `POST /api/online-status/check` | POST | Check multiple users (body: `["user1", "user2"]`) |

---

## React Hook Example

```javascript
import { useEffect, useState, useRef } from 'react';
import WebSocketClient from './WebSocketClient';

export function useWebSocket(token) {
  const [connected, setConnected] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [messages, setMessages] = useState([]);
  const clientRef = useRef(null);

  useEffect(() => {
    if (!token) return;

    const client = new WebSocketClient(token);
    clientRef.current = client;

    client.handleNotification = (notification) => {
      setNotifications(prev => [notification, ...prev]);
    };

    client.handleDirectMessage = (message) => {
      setMessages(prev => [...prev, message]);
    };

    client.connect(
      () => setConnected(true),
      () => setConnected(false)
    );

    return () => {
      client.disconnect();
    };
  }, [token]);

  return {
    connected,
    notifications,
    messages,
    sendMessage: (recipient, content) => clientRef.current?.sendMessage(recipient, content),
    sendTyping: (recipient, isTyping) => clientRef.current?.sendTyping(recipient, isTyping),
  };
}
```

---

## 🧪 Testing WebSocket Connections

Since Postman doesn't natively support STOMP over WebSocket, here are multiple ways to test the WebSocket API:

### Option 1: Browser Console Test

Open your browser's developer console and paste this code:

```javascript
// 1. First, get a JWT token by logging in
const loginResponse = await fetch('http://localhost:8081/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ usernameOrEmail: 'testuser', password: 'password123' })
});
const { accessToken } = await loginResponse.json();
console.log('Token:', accessToken);

// 2. Load SockJS and STOMP
const sockjsScript = document.createElement('script');
sockjsScript.src = 'https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js';
document.head.appendChild(sockjsScript);

const stompScript = document.createElement('script');
stompScript.src = 'https://cdn.jsdelivr.net/npm/@stomp/stompjs@7/bundles/stomp.umd.min.js';
document.head.appendChild(stompScript);

// 3. Wait for scripts to load, then connect
setTimeout(() => {
  const socket = new SockJS('http://localhost:8081/ws');
  const stompClient = Stomp.over(socket);
  
  stompClient.connect(
    { 'Authorization': 'Bearer ' + accessToken },
    (frame) => {
      console.log('✅ Connected:', frame);
      
      // Subscribe to notifications
      stompClient.subscribe('/user/queue/notifications', (msg) => {
        console.log('📬 Notification:', JSON.parse(msg.body));
      });
      
      // Subscribe to messages
      stompClient.subscribe('/user/queue/messages', (msg) => {
        console.log('💬 Message:', JSON.parse(msg.body));
      });
      
      // Subscribe to online status
      stompClient.subscribe('/topic/online-status', (msg) => {
        console.log('🟢 Online Status:', JSON.parse(msg.body));
      });
      
      // Test ping
      stompClient.subscribe('/user/queue/pong', (msg) => {
        console.log('🏓 Pong:', JSON.parse(msg.body));
      });
      stompClient.send('/app/ping', {}, '');
      
      console.log('✅ Subscribed to all queues');
    },
    (error) => console.error('❌ Connection error:', error)
  );
}, 2000);
```

### Option 2: Node.js Test Script

Create a file `test-websocket.js`:

```javascript
const SockJS = require('sockjs-client');
const Stomp = require('stompjs');

// Replace with your actual token
const TOKEN = 'your-jwt-token-here';
const WS_URL = 'http://localhost:8081/ws';

const socket = new SockJS(WS_URL);
const stompClient = Stomp.over(socket);

stompClient.connect(
  { 'Authorization': `Bearer ${TOKEN}` },
  (frame) => {
    console.log('✅ Connected:', frame);

    // Subscribe to personal notifications
    stompClient.subscribe('/user/queue/notifications', (message) => {
      console.log('📬 Notification:', JSON.parse(message.body));
    });

    // Subscribe to direct messages
    stompClient.subscribe('/user/queue/messages', (message) => {
      console.log('💬 Message:', JSON.parse(message.body));
    });

    // Subscribe to typing indicators
    stompClient.subscribe('/user/queue/typing', (message) => {
      console.log('⌨️ Typing:', JSON.parse(message.body));
    });

    // Subscribe to online status broadcasts
    stompClient.subscribe('/topic/online-status', (message) => {
      console.log('🟢 Online Status:', JSON.parse(message.body));
    });

    // Test: Send a ping
    stompClient.subscribe('/user/queue/pong', (message) => {
      console.log('🏓 Pong received:', JSON.parse(message.body));
    });
    stompClient.send('/app/ping', {}, '');

    // Test: Check online users
    stompClient.subscribe('/user/queue/online-users', (message) => {
      console.log('👥 Online users:', JSON.parse(message.body));
    });
    stompClient.send('/app/status.online', {}, '');

    // Test: Send a direct message (uncomment to test)
    // stompClient.send('/app/chat.send', {}, JSON.stringify({
    //   recipient: 'otheruser',
    //   content: 'Hello from WebSocket test!',
    //   messageType: 'TEXT'
    // }));

    // Test: Send typing indicator (uncomment to test)
    // stompClient.send('/app/chat.typing', {}, JSON.stringify({
    //   recipient: 'otheruser',
    //   typing: true
    // }));

    console.log('✅ All subscriptions set up. Waiting for messages...');
  },
  (error) => {
    console.error('❌ Connection error:', error);
  }
);

// Keep the script running
process.on('SIGINT', () => {
  stompClient.disconnect(() => {
    console.log('👋 Disconnected');
    process.exit(0);
  });
});
```

Run with:
```bash
npm install sockjs-client stompjs
node test-websocket.js
```

### Option 3: Online WebSocket Tester (Apic)

1. Install **Apic** extension for VS Code or use online at https://apic.app
2. Connect to: `ws://localhost:8081/ws/websocket`
3. Use STOMP protocol frames

### Option 4: Using wscat + Manual STOMP Frames

```bash
# Install wscat
npm install -g wscat

# Connect
wscat -c ws://localhost:8081/ws/websocket

# Send CONNECT frame (paste this after connecting)
CONNECT
Authorization:Bearer YOUR_JWT_TOKEN
accept-version:1.2
heart-beat:10000,10000

^@
```
(Note: `^@` is the NULL character - press Ctrl+@)

---

## Test Scenarios Checklist

### ✅ Connection Tests
- [ ] Connect with valid JWT token → Should succeed
- [ ] Connect without token → Should fail
- [ ] Connect with expired token → Should fail
- [ ] Disconnect and reconnect → Should work

### ✅ Messaging Tests
- [ ] Subscribe to `/user/queue/notifications` → Should receive notifications
- [ ] Subscribe to `/user/queue/messages` → Should receive DMs
- [ ] Send message via `/app/chat.send` → Recipient should receive it
- [ ] Send typing indicator via `/app/chat.typing` → Recipient should see it

### ✅ Online Status Tests
- [ ] Connect → Should broadcast online status to `/topic/online-status`
- [ ] Disconnect → Should broadcast offline status
- [ ] Call `/app/status.online` → Should receive list of online users
- [ ] Call `/app/status.check` with usernames → Should receive their status

### ✅ Channel Tests
- [ ] Subscribe to `/topic/channel/{id}/posts` → Should receive new posts
- [ ] Subscribe to `/topic/post/{id}/comments` → Should receive new comments

### ✅ Keep-Alive Tests
- [ ] Send `/app/ping` → Should receive pong on `/user/queue/pong`

---

## Debugging Tips

1. **Enable STOMP debug logging** in `application.properties`:
   ```properties
   logging.level.org.springframework.messaging=DEBUG
   logging.level.org.springframework.web.socket=DEBUG
   ```

2. **Check online users** via REST API:
   ```bash
   curl http://localhost:8081/api/online-status/users
   ```

3. **Monitor connection events** - The `WebSocketEventListener` logs all connections/disconnections
