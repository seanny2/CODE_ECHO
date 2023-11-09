/* eslint-disable jsdoc/require-jsdoc */
import yorkie, { DocEventType, OperationInfo } from 'yorkie-js-sdk';
import { basicSetup, EditorView } from 'codemirror';
import { java } from '@codemirror/lang-java';
import { Transaction } from '@codemirror/state';
import { network } from './network';
import { displayPeers } from './utils';
import { YorkieDoc } from './type';
// import SockJS from 'sockjs-client'
// import Stomp from 'stompjs'

import './style.css';
import './theme.css';

const editorParentElem = document.getElementById('editor')!;
const peersElem = document.getElementById('peers')!;
// const documentElem = document.getElementById('document')!;
// const documentTextElem = document.getElementById('document-text')!;
const networkStatusElem = document.getElementById('network-status')!;
// const compileBtn = document.getElementById('compile')!;


async function main() {
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
  // const doc = new yorkie.Document<YorkieDoc>(Math.random().toString()); // 초대 링크를 파라미터로 기입
  const doc = new yorkie.Document<YorkieDoc>(
    `codemirror6-${new Date()
      .toISOString()
      .substring(0, 10)
      .replace(/-/g, '')}`,
  );
  console.log(doc);
  doc.subscribe('presence', (event) => {
    if (event.type !== DocEventType.PresenceChanged) {
      displayPeers(peersElem, doc.getPresences(), client.getID()!);
    }
  });
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
    // displayLog(documentElem, documentTextElem, doc);
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
  // displayLog(documentElem, documentTextElem, doc);

  // if (message.body.result === "성공") {
  //   document.getElementById("output").style.color = "#000";
  //   document.getElementById("result").style.color = "#000";
  // } else {
  //   document.getElementById("output").style.color = "#f00";
  //   document.getElementById("result").style.color = "#f00";
  // }

  // document.getElementById("output").innerHTML = response.data.SystemOut != null ? response.data.SystemOut.replace(/\n/g, "<br>") : "";
  // document.getElementById("performance").textContent = response.data.performance;
  // document.getElementById("result").textContent = response.data.result;



}

main();