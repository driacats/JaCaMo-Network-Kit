using System;
using UnityEngine;

public abstract class AgentAction {
    public string type;

    protected AgentAction(string type) {
        this.type = type;
    }
}

[Serializable]
public class TypeOneAction : AgentAction {
    public int param;

    public TypeOneAction(char param) : base("one") {
        this.param = param;
    }
}

[Serializable]
public class TypeTwoAction : AgentAction {
    public TypeTwoAction() : base("two") {}
}