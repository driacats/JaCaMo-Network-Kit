using System;
using System.Collections.Generic;

[Serializable]
public abstract class Message {
    public string message_type;
    
    public Message(string message_type) {
        this.message_type = message_type;
    }
}

[Serializable]
public class InitMessage : Message {
    public string agent_name;

    public InitMessage(string agent_name) : base("init") {
        this.agent_name = agent_name;
    }
}

[Serializable]
public class StartGameMessage : Message {
    public StartGameMessage() : base("start_game") {}
}

[Serializable]
public class PerceptMessage : Message
{
    public string receiver;
    public string percept;

    public PerceptMessage(string receiver, string percept) : base("percept") {
        this.receiver = receiver;
        this.percept = percept;
    }
}

[Serializable]
public class ActionMessage : Message {
    public string sender;
    public AgentAction action;

    public ActionMessage(string sender, AgentAction action) : base("action") {
        this.sender = sender;
        this.action = action;
    }
}