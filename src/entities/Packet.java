package entities;

import enums.Type;

/**
 * Created by Lukasz on 2015-01-26.
 */
public class Packet {
    private Type type;
    private String text;

    public Type getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setType(Type type)
    {
        this.type=type;
    }

    public void setType(String text) {

        switch(text)
        {
            case "message":
                type=Type.message;
                break;
            case "poke":
                type=Type.poke;
                break;
        }
    }


}
