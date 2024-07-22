using System;
using System.Collections.Generic;
using System.Globalization;
using System.Threading.Tasks;
using UnityEngine;
using NativeWebSocket;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using Unity.VisualScripting;
using UnityEditor.Callbacks;

public class WebsocketClient : MonoBehaviour
{
    public string serverIP = "localhost";   // default value, may need to be changed in the inspector
    public int serverPort = 9080;
    private WebSocket websocket;
    private bool connected;
    private JsonSerializerSettings jsonSettings;
    
    async void Start() {
        jsonSettings = new JsonSerializerSettings {
            Culture = CultureInfo.InvariantCulture
        };
        
        await ConnectToServer();
    }
    
    private void Update() {
        websocket?.DispatchMessageQueue();
    }

    private void RegisterAvatar(int agentID, AvatarManager manager) {
        avatarManagers.Add(agentID, manager);
    }
    
    private async void OnOpen() {
        Debug.Log("[WebsocketClient] - Connection with the server opened");
        connected = true;
        
        await websocket.SendText(
            JsonUtility.ToJson(
                new InitMessage("unity")));
        
        Debug.Log("[WebsocketClient] - Init message sent to the server");
    }

    private void OnMessage(byte[] bytes) {
        string message = System.Text.Encoding.UTF8.GetString(bytes);
        Debug.Log("[WebsocketClient] - new message: '" + message + "'");

        try {
            JObject json_data = JObject.Parse(message);
            try {
                switch (json_data["message_type"].ToString()) {
                    case "start_game":
                        Debug.Log("[WebsocketClient] - It's a start message");
                        // do something to start the game
                        break;
                    case "action":
                        int agentID = int.Parse(json_data["sender"].ToString()[^1].ToString());
                        JObject action_data = JObject.Parse(json_data["action"].ToString());
                        switch (action_data["type"].ToString()) {
                            case "one":
                                Debug.Log("[WebsocketClient] - It's a drop message");
                                Debug.Log($"param: {int.Parse(action_data["param"].ToString())}");
                                // do something
                                break;
                            case "two":
                                Debug.Log("[WebsocketClient] - It's a rotate message");
                                // do something
                                break;
                        }
                        break;
                }
            }
            catch (NullReferenceException e) {
               Debug.LogError("[WebsocketClient] - The received message did not contain an expected field.");
               Debug.LogError(e);
            }

        } catch (JsonException ex) {
            Debug.LogError($"[WebsocketClient] - Failed to deserialize message: {ex.Message}");
        }
    }
    
    private void OnError(String e) {
        Debug.Log("[WebsocketClient] - Websocket error: " + e);
    }

    private void OnClose(WebSocketCloseCode cc) {
        connected = false;
        Debug.Log("Connection with the server closed with code " + cc);
    }

    private async Task ConnectToServer() {
        websocket = new WebSocket(string.Format("ws://{0}:{1}", serverIP, serverPort));
        websocket.OnOpen += OnOpen;
        websocket.OnClose += OnClose;
        websocket.OnMessage += OnMessage;
        websocket.OnError += OnError;
        
        Debug.Log(string.Format("[WebsocketClient] - Trying to connect to IP {0}, port {1}", serverIP, serverPort));
        await websocket.Connect();
    }
    
    private async void SendMessage(string message) {
        if (websocket.State == WebSocketState.Open) {
            await websocket.SendText(message);
        }
    }

    private void SendPerceptMessage(int agentID) {
        SendMessage(
            JsonConvert.SerializeObject(new PerceptMessage("agent_name", "test")));
    }

    private async void OnApplicationQuit() {
        await websocket.Close();
    }
}
