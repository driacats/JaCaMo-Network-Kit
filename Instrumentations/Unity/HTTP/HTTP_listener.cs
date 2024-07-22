using UnityEngine;
using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine.Networking;

public class HttpListener : MonoBehaviour
{
    private string serverUrl = "http://localhost:8080"; // Cambia questo con l'URL del tuo server
    
    // Evento per notificare quando una risposta è ricevuta
    public event Action<string> OnResponseReceived;

    void Start()
    {
        // Esempio di come inviare una richiesta all'avvio
        // SendRequest("GET", "");
    }

    public void SendRequest(string method, string body, string contentType = "application/json")
    {
        StartCoroutine(SendRequestCoroutine(method, body, contentType));
    }

    private IEnumerator SendRequestCoroutine(string method, string body, string contentType)
    {
        UnityWebRequest request = new UnityWebRequest(serverUrl, method);

        if (!string.IsNullOrEmpty(body))
        {
            byte[] bodyRaw = System.Text.Encoding.UTF8.GetBytes(body);
            request.uploadHandler = new UploadHandlerRaw(bodyRaw);
        }

        request.downloadHandler = new DownloadHandlerBuffer();
        request.SetRequestHeader("Content-Type", contentType);

        yield return request.SendWebRequest();

        if (request.result == UnityWebRequest.Result.ConnectionError || 
            request.result == UnityWebRequest.Result.ProtocolError)
        {
            Debug.LogError("Error: " + request.error);
            OnResponseReceived?.Invoke("Error: " + request.error);
        }
        else
        {
            string responseText = request.downloadHandler.text;
            Debug.Log("Response received: " + responseText);
            OnResponseReceived?.Invoke(responseText);
        }
    }

    // Metodo di utilità per inviare una richiesta GET
    public void SendGetRequest(string endpoint = "")
    {
        SendRequest("GET", "", "application/json");
    }

    // Metodo di utilità per inviare una richiesta POST
    public void SendPostRequest(string body, string contentType = "application/json")
    {
        SendRequest("POST", body, contentType);
    }

    // Esempio di come gestire la risposta
    private void HandleResponse(string response)
    {
        Debug.Log("Handled response: " + response);
        // Qui puoi aggiungere la logica per gestire la risposta
    }
}