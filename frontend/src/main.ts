/* eslint-disable jsdoc/require-jsdoc */
import yorkie, { OperationInfo } from 'yorkie-js-sdk';
import { basicSetup, EditorView } from 'codemirror';
import { java } from '@codemirror/lang-java';
import { Transaction } from '@codemirror/state';
import { network } from './network';
import { YorkieDoc } from './type';
import { darcula } from '@uiw/codemirror-theme-darcula';
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'
import axios from 'axios';

const editorParentElem = document.getElementById('editor')!;
const networkStatusElem = document.getElementById('network-status')!;


async function main(roomId: string, userId: string) {

    // 01. create client with RPCAddr(envoy) then activate it.
    const client = new yorkie.Client(import.meta.env.VITE_YORKIE_API_ADDR, {
        apiKey: import.meta.env.VITE_YORKIE_API_KEY,
    });

    await client.activate();

    // subscribe peer change event
    client.subscribe((event) => {
        network.statusListener(networkStatusElem)(event);
    });


    // 02-1. create a document then attach it into the client.
    const doc = new yorkie.Document<YorkieDoc>(roomId); // 초대 링크를 파라미터로 기입
    await client.attach(doc);


    doc.update((root) => {
        if (!root.content) {
            root.content = new yorkie.Text();
        }
    }, 'create content if not exists');

    // 02-2. subscribe document event.
    const syncText = () => {
        const text = doc.getRoot().content;
        view.dispatch({
            changes: { from: 0, to: view.state.doc.length, insert: text.toString() },
            annotations: [Transaction.remote.of(true)],
        });
    };
    doc.subscribe((event) => {
        if (event.type === 'snapshot') {
            // The text is replaced to snapshot and must be re-synced.
            syncText();
        }
    });

    doc.subscribe('$.content', (event) => {
        if (event.type === 'remote-change') {
            const { operations } = event.value;
            handleOperations(operations);
        }
    });

    await client.sync();

    // 03-1. define function that bind the document with the codemirror(broadcast local changes to peers)
    const updateListener = EditorView.updateListener.of((viewUpdate) => {
        if (viewUpdate.docChanged) {
            for (const tr of viewUpdate.transactions) {
                const events = ['select', 'input', 'delete', 'move', 'undo', 'redo'];
                if (!events.map((event) => tr.isUserEvent(event)).some(Boolean)) {
                    continue;
                }
                if (tr.annotation(Transaction.remote)) {
                    continue;
                }
                tr.changes.iterChanges((fromA, toA, _, __, inserted) => {
                    doc.update((root) => {
                        root.content.edit(fromA, toA, inserted.toJSON().join('\n'));
                    }, `update content byA ${client.getID()}`);
                });
            }
        }
    });

    // 03-2. create codemirror instance
    const view = new EditorView({
        doc: '',
        extensions: [
            basicSetup,
            java(),
            updateListener,
            darcula,
        ],
        parent: editorParentElem,
    });

    // 03-3. define event handler that apply remote changes to local
    function handleOperations(operations: Array<OperationInfo>) {
        operations.forEach((op) => {
            if (op.type === 'edit') {
                handleEditOp(op);
            }
        });
    }
    function handleEditOp(op: any) {
        const changes = [
            {
                from: Math.max(0, op.from),
                to: Math.max(0, op.to),
                insert: op.value!.content,
            },
        ];

        view.dispatch({
            changes,
            annotations: [Transaction.remote.of(true)],
        });
    }

    syncText();


    // 방 이름을 사용해 WebSocket 서버와 연결
    const socket = new SockJS('/websocket');
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
        console.log('Connected to WebSocket');
        stompClient.subscribe('/topic/room/chat/' + roomId, (response) => {
            displayMessage(JSON.parse(response.body));
        });
        stompClient.subscribe('/topic/room/compile/' + roomId, (response) => {
            const data = JSON.parse(response.body);
            if (data.result === "성공") {
                document.getElementById("output")!.style.color = "#000";
                document.getElementById("result")!.style.color = "#000";
            } else {
                document.getElementById("output")!.style.color = "#f00";
                document.getElementById("result")!.style.color = "#f00";
            }

            document.getElementById("output")!.innerHTML = data.SystemOut != null ? data.SystemOut.replace(/\n/g, "<br>") : "";
            document.getElementById("performance")!.innerText = data.performance;
            document.getElementById("result")!.innerText = data.result;
        });
    });


    function displayMessage(message: any) {
        const messages = document.getElementById('messages')!;
        const li = document.createElement('li');
        messages.appendChild(li);

        // 내 채팅인 경우 오른쪽, 상대방 채팅인 경우 왼쪽으로 정렬
        const LR_className = message.sender === userId ? 'right' : 'left';

        li.innerHTML = `
        <div class="message ${LR_className}">
            <div class="sender">${message.sender}</div>
            <div class="text">${message.content}</div>
        </div>`;

        // 스크롤바를 가장 아래로 이동
        messages.scrollTop = messages.scrollHeight;
    }

    document.getElementById('sendBtn')?.addEventListener('click', () => {
        const messageInput = <HTMLInputElement>document.getElementById('message')!;
        const message = { "content": messageInput.value, "sender": userId };
        stompClient.send('/app/room/chat/' + roomId, {}, JSON.stringify(message));
        //입력창 비우기
        messageInput.value = '';
    });

    document.getElementById('compile')?.addEventListener('click', () => {
        stompClient.send('/app/room/compile/' + roomId, {}, JSON.stringify({ "code": view.contentDOM.innerText }));
    });

    const chatBtn = document.getElementById('messageToggle')!;
    const chatting = document.getElementById('chatting')!;
    const print = document.getElementById('systemOut')!;

    chatBtn.addEventListener('click', () => {
        if (chatting.classList.contains('inactive')) {
            chatting.classList.remove('inactive');
            print.classList.add('inactive');
        } else {
            chatting.classList.add('inactive');
            print.classList.remove('inactive');
        }
    });

    window.addEventListener('beforeunload', (event) => {
        // 명세에 따라 preventDefault는 호출해야하며, 기본 동작을 방지합니다.
        event.preventDefault();

        const users = doc.getPresences();
        if (users.length===1) {
            axios({
                method: 'delete',
                url: 'http://localhost:8083/delete',
                data: {
                    id: roomId
                }
            })
            .then(() => {
                console.log(`${roomId}에는 아무도 없기 때문에 방을 삭제합니다.`);
                location.href = "http://localhost:8083";
            })
        }

        event.returnValue = '';
    });
}

const currentURL: string = window.location.href;
const pathSegments: string[] = currentURL.split('/');
const roomId: string = pathSegments[pathSegments.length - 1];

const sessionId = sessionStorage.getItem('userId')
if (sessionId) {
    main(roomId, sessionId);
    console.log(`유저 정보 불러오기: ${sessionId}`);
} else {
    const paramId = new URLSearchParams(window.location.search).get("name");
    if (paramId) {
        console.log(`유저 정보 불러오기: ${paramId}`);
        main(roomId, paramId);
    } else {
        console.log(`불러올 유저 정보가 없습니다.`);
        location.href = "http://localhost:8083";
    }
}