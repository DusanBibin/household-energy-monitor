import { Injectable } from '@angular/core';
import { StompService, StompState, StompHeaders} from '@stomp/ng2-stompjs';
import { Client, IMessage } from '@stomp/stompjs';
import { StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client'
import { Observable, Subject } from 'rxjs';
import { environment} from '../../../../environments/environment';
@Injectable({
  providedIn: 'root'
})
export class WebsocketService {

  private client: Client;
  private subscription: StompSubscription | null = null;
  private messageSubject = new Subject<any>();


  constructor() {
    this.client = new Client({
      brokerURL: 'ws://' + environment.ip + ':8080/ws', // your WebSocket endpoint
      connectHeaders: {},
      debug: (str) => console.log(str),
      reconnectDelay: 5000,
      webSocketFactory: () => new SockJS('http://' + environment.ip +':8080/ws') // SockJS fallback if needed
    });

    this.client.onConnect = (frame) => {
      console.log('Connected: ' + frame);
      // Note: Do not subscribe here if you want to subscribe dynamically elsewhere
    };

    this.client.activate();
  }

  subscribeToHousehold(householdId: number): Observable<any> {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  
    if (this.client.connected) {
      this.subscription = this.client.subscribe(
        `/consumption-realtime/${householdId}`,
        (msg: IMessage) => {
          const body = JSON.parse(msg.body);
          console.log('Got WS data:', body);
          this.messageSubject.next(body); // send to observable
        }
      );
    } else {
      this.client.onConnect = () => {
        console.log('Connected!');
        this.subscription = this.client.subscribe(
          `/consumption-realtime/${householdId}`,
          (msg: IMessage) => {
            const body = JSON.parse(msg.body);
            console.log('Got WS data:', body);
            this.messageSubject.next(body);
          }
        );
      };
    }
  
    return this.messageSubject.asObservable();
  }
  

  unsubscribe() {
    if (this.subscription) {
      this.subscription.unsubscribe();
      this.subscription = null;
    }
  }

}
