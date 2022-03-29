package sec.server;

import sec.common.BasicMessage;
import sec.common.MsgType;
import sec.common.TextMessage;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Random;

public class ServerMain extends BasicServer
{
    private final UserDB userDB;
    private static final Random RANDOM = new Random();

    public ServerMain(int listeningPort, String filePath) throws IOException
    {
        super(listeningPort);
        userDB = new UserDB(filePath);

        registerHandlers();
    }

    @Override
    public void start() throws IOException
    {
        // Example usage of UserDB -->
        // Create an user.
        byte[] password = "password".getBytes();
        byte[] otherData = {0, 42, 21};
        UserDB.User admin = new UserDB.User("admin", password, otherData);

        System.out.println("admin user in DB: " + userDB.isRegistered("admin"));

        // Add (if not already present) the user to the database.
        if (userDB.add(admin))
        {
            System.out.println("Added dummy admin user");
        }

        // Fetch it back and display data.
        UserDB.User user = userDB.get("admin");
        String readPassword = new String(user.getField(0));
        System.out.println(
                "User " + user + " has password "
                + readPassword + " and otherData: " +
                Arrays.toString(otherData));
        // <-- Example

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
        registerHandler(MsgType.REGISTER,(message, in, out)->{
            TextMessage msg = (TextMessage) message;
            String[] user= msg.getText().split(" ");
            byte[] salt = new byte[16];
            RANDOM.nextBytes(salt);
            PBEKeySpec hashing = new PBEKeySpec(user[1].toCharArray(),salt,100);
            UserDB.User userReg = new UserDB.User(user[0],hashing.getSalt(),
                    Charset.forName("UTF-8").encode(CharBuffer.wrap(hashing.getPassword())).array());
            if(userDB.add(userReg))
                out.writeObject("Successfully added.");
            else
                out.writeObject("User already exists.");
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
        try (ServerMain server = new ServerMain(42000, "userdb.txt"))
        {
            server.start();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
