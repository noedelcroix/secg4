package sec.server;

import sec.common.MsgType;
import sec.common.TextMessage;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.*;
import java.util.*;

public class ServerMain extends BasicServer
{
    private final UserDB userDB;
    private static final Random RANDOM = new Random();
    private List<Integer> authenticated;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public ServerMain(int listeningPort, String filePath) throws IOException
    {
        super(listeningPort);
        userDB = new UserDB(filePath);
        authenticated=new ArrayList<>();
        try {
            initializeKeys();
        } catch (Exception e) {
            e.printStackTrace();
        }
        registerHandlers();

    }

    @Override
    public void start() throws IOException
    {
        super.start();
    }

    private void registerHandlers()
    {
        //handler 1
        registerHandler(MsgType.FATHER, (message, in, out) ->
        {
            TextMessage msg = (TextMessage) message;
            String[] user= msg.getText().split(" ");
            if(authenticated.contains(Integer.parseInt(user[user.length-1]))) {
                if (msg.getText()
                        .equalsIgnoreCase("You killed my father")) {
                    out.writeObject("No, I am your father");
                } else {
                    out.writeObject("Whatever");
                }
            }else{
                out.writeObject("Not authenticated");
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
            if(!userDB.isRegistered(user[0])) {
                byte[] salt = new byte[16];
                RANDOM.nextBytes(salt);
                UserDB.User userReg = new UserDB.User(user[0], hashPassword(decryptPassword(user[1]),salt,100),salt);
                if (userDB.add(userReg))
                    out.writeObject("Successfully added.");
            }else
                out.writeObject("User already registered");
        });

        registerHandler(MsgType.LOGIN,(message,in,out)->{
            TextMessage msg = (TextMessage) message;
            String[] user = msg.getText().split(" ");
            if(userDB.isRegistered(user[0])) {
                byte[] salt = userDB.get(user[0]).getField(1);
                byte[] hashed = hashPassword(decryptPassword(user[1]),salt,100);
                if (new String(hashed).equals
                        (new String(userDB.get(user[0]).getField(0)))) {
                    authenticated.add(Integer.parseInt(user[2]));
                    out.writeObject("Authentication succeeds");
                } else {
                    out.writeObject("Authentication failed");
                }
            }else
                out.writeObject("User does not exist.");
        });

        registerHandler(MsgType.EXIT,(message,in,out)->{
            TextMessage msg = (TextMessage) message;
            if(authenticated.contains(msg.getText())){
                authenticated.remove(msg.getText());
                out.writeObject("Successful logout.");
            }else{
                out.writeObject("You were not connected.");
            }
        });
        System.out.println("Handlers registered");
    }
    private void initializeKeys() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {

        File publicKeyFile = new File("server/public/public.key");
        byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        this.publicKey = keyFactory.generatePublic(publicKeySpec);

        File file = new File("server/private/private.key");
        FileInputStream fis = new FileInputStream(file);
        DataInputStream dis = new DataInputStream(fis);

        byte[] keyBytes = new byte[(int) file.length()];
        dis.readFully(keyBytes);
        dis.close();

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        privateKey =  kf.generatePrivate(spec);

    }
    private String decryptPassword(String password) {
        byte[] encryptedBytes = Base64.getDecoder().decode(password);
        Cipher cipher = null;
        String pswd = null;
        try {
            cipher = Cipher.getInstance("RSA");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            cipher.init(Cipher.DECRYPT_MODE,privateKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        byte[] decryptedMessage = new byte[0];
        try {
            decryptedMessage = cipher.doFinal(encryptedBytes);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        pswd = new String(decryptedMessage, StandardCharsets.UTF_8);
        return pswd;
    }
    private byte[] hashPassword(String password, byte[] salt, int count)  {
        KeySpec spec = null;
        SecretKeyFactory factory = null;
        byte[] hashed= null;
        spec = new PBEKeySpec(password.toCharArray(), salt, count, 128);
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        try {
             hashed = factory.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return hashed;
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
