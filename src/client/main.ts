import { WebSocket } from 'ws'

let ws = new WebSocket("ws://localhost:8080")
ws.onopen = ((data: any) => {
    console.log(data);
    console.log("connect")
});
