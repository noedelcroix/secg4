package sec.server;

import sec.common.MsgType;
import sec.common.TextMessage;

import java.io.IOException;

public class MyServer extends BasicServer
{
    UserDB userDB;

    public MyServer(int listeningPort, String filePath) throws IOException
    {
        super(listeningPort);
        userDB = new UserDB(filePath);

        registerHandlers();
    }

    @Override
    public void start() throws IOException
    {
        System.out.println(userDB.add(new UserDB.User("ante")));
        System.out.println(userDB.get("ante"));
        System.out.println(userDB.add(new UserDB.User("plopsaprout",
                "hopl:a!".getBytes())));
        System.out.println(new String(userDB.get("plopsaprout").getField(0)));

        super.start();
    }

    private void registerHandlers()
    {
        //handler 1
        registerHandler(MsgType.FATHER, (message, in, out) ->
        {

            TextMessage msg = (TextMessage) message;

            if (msg.getText()
                   .equalsIgnoreCase("You killed my father"))
            {
                out.writeObject("No, I am your father");
            }
            else
            {
                out.writeObject("Whatever");
            }
        });

        //handler 2
        registerHandler(MsgType.HELLO, (message, in, out) ->
        {

            TextMessage msg = (TextMessage) message;

            if (msg.getText().equalsIgnoreCase("Hello there"))
            {
                out.writeObject("General Kenobi!");
            }
            else
            {
                out.writeObject("Whatever");
            }
        });

        System.out.println("Handlers registered");
    }

    @Override
    public void close() throws Exception
    {
        super.close();
        userDB.close();
    }

    public static void main(String[] args)
    {
        try (MyServer server = new MyServer(42000, "userdb.txt"))
        {
            server.start();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
