package sec.common;

import java.io.Serializable;

public class TextMessage extends NonReplayableMessage implements Serializable
{
    private final String txt;

    public TextMessage(String counter, String txt, MsgType type)
    {
        super(counter, type);
        this.txt = txt;
    }

    public String getText()
    {
        return txt;
    }

    @Override
    public String toString() {
        return this.txt;
    }
}
