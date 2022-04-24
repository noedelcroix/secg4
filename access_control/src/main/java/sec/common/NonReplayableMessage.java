package sec.common;

import java.io.Serializable;

public class NonReplayableMessage extends BasicMessage implements Serializable {
    private String previousCounter;
    private String nextCounter;
public NonReplayableMessage(String counter, MsgType type){
    super(type);
    this.previousCounter=counter;
}

public String getPreviousCounter(){
    return this.previousCounter;
}

public String getCurrentCounter(){
    return this.nextCounter;
}

public void setPreviousCounter(String previousCounter){
    this.previousCounter=previousCounter;
}

    public void setCurrentCounter(String currentCounter){
        this.nextCounter=currentCounter;
    }
}
