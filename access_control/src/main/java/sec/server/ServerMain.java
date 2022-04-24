package sec.server;

import sec.common.BasicMessage;
import sec.common.MsgType;
import sec.common.TextMessage;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.*;
import java.util.*;

public class ServerMain extends BasicServer
{
    private final UserDB userDB;
    private PrivateKey privateKey;
    private static final Random RANDOM = new Random();
    private HashMap<UserDB.User,Key> authenticatedUsersKeys;
    private HashMap<UserDB.User, Integer> authenticatedUsersPreviousCounters;
    private HashMap<UserDB.User, String> authenticatedUsersNextCounters;

    public ServerMain(int listeningPort, String filePath) throws IOException
    {
        super(listeningPort);
        userDB = new UserDB(filePath);
        authenticatedUsersKeys=new HashMap<>();
        authenticatedUsersPreviousCounters=new HashMap<>();
        authenticatedUsersNextCounters=new HashMap<>();
        try {
            this.privateKey = getServerPrivateKey();
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

                String[] splitMsg = msg.getText().split(" ");

                Map.Entry<UserDB.User, Key> user = checkLogged(splitMsg[splitMsg.length - 1]);
            /*int previousCounter = decryptInt(msg.getPreviousCounter(), user.getValue());
            if(msg.getCurrentCounter()!=null) {
                int currentCounter = decryptInt(msg.getCurrentCounter(), user.getValue());
            }else{
                int currentCounter = 1;
            }*/

                //if(previousCounter==authenticatedUsersPreviousCounters.get(user.getKey())) {
                    if (user != null) {
                        if (msg.getText().toLowerCase().contains("you killed my father")) {
                            //out.writeObject(new TextMessage(encryptInt((byte)++currentCounter, user.getValue()), "No, I am your father", msg.getType()));
                            out.writeObject("No, I am your father");
                        } else {
                            //out.writeObject(new TextMessage(encryptInt((byte)++currentCounter, user.getValue()), "Whatever", msg.getType()));
                            out.writeObject("Whatever");
                        }
                    } else {
                        out.writeObject("Not authenticated");
                    }
                /*}else{
                    out.writeObject("Trust Problem.");
                }*/
        });

        //handler 2
        registerHandler(MsgType.HELLO, (message, in, out) ->
        {

            TextMessage msg = (TextMessage) message;
            System.out.println(msg.getText());

            if (msg.getText().equalsIgnoreCase("there"))
            {
                out.writeObject("General Kenobi!");
            }
            else
            {
                out.writeObject("Whatever");
            }
        });

        //handler 3
        registerHandler(MsgType.REGISTER,(message, in, out)->{
            TextMessage msg = (TextMessage) message;
            String[] user= msg.getText().split(" ");
            if(user.length>=3 && !userDB.isRegistered(user[0]) && isPasswordValid(decryptPassword(user[1]))) {
                byte[] salt = new byte[16];
                RANDOM.nextBytes(salt);
                UserDB.User userReg = new UserDB.User(user[0], hashPassword(decryptPassword(user[1]),salt,100),salt);
                if (userDB.add(userReg))
                    out.writeObject("Successfully added.");
            }else
                out.writeObject("User already registered");
        });

        //handler 4
        registerHandler(MsgType.LOGIN,(message,in,out)->{
            TextMessage msg = (TextMessage) message;
            String[] user;
            try {
                user = msg.getText().split(" ");
            }catch(Exception e){
                out.writeObject("Authentication failed");
                return;
            }
            if(userDB.isRegistered(user[0])) {
                byte[] salt = userDB.get(user[0]).getField(1);
                byte[] hashed = hashPassword(decryptPassword(user[1]),salt,100);
                if (new String(hashed).equals
                        (new String(userDB.get(user[0]).getField(0)))) {
                    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                    try {
                        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(decryptKey(user[2],this.privateKey));
                        authenticatedUsersKeys.put(userDB.get(user[0]), keyFactory.generatePublic(publicKeySpec));
                    }catch(Exception e){
                        System.out.println(e);
                        out.writeObject("Authentication failed");
                        return;
                    }

                    //authenticatedUsersPrivateKeys.add(Integer.parseInt(user[2]));
                    out.writeObject("Authentication succeeds");
                } else {
                    out.writeObject("Authentication failed");
                }
            }else
                out.writeObject("User does not exist.");
        });

        System.out.println("Handlers registered");
    }

    @Override
    public void close() throws Exception
    {
        super.close();
        userDB.close();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, IOException {
        try (ServerMain server = new ServerMain(42000, "userdb.txt"))
        {
            server.start();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private static PrivateKey getServerPrivateKey() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] privateKeyBytes = Files.readAllBytes(new File("server/private/private.key").toPath());
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        return keyFactory.generatePrivate(privateKeySpec);
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

    private static boolean isPasswordValid(String password) {
        return(password !=null && password.length()>=6 && password.length()<=8 && password.matches("^[a-zA-Z\\d]*$"));
    }

    private byte[] decryptKey(String message, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(Base64.getDecoder().decode(message));
    }

    private static void generateKeys() throws NoSuchAlgorithmException, NoSuchProviderException, IOException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair pair = keyGen.generateKeyPair();
        new FileOutputStream("server/public/public.key").write(pair.getPublic().getEncoded());
        new FileOutputStream("server/private/private.key").write(pair.getPrivate().getEncoded());
    }

    private Map.Entry<UserDB.User, Key> checkLogged(String text) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        System.out.println(text);
        for(Map.Entry<UserDB.User, Key> user : authenticatedUsersKeys.entrySet()){
            System.out.println(user.getKey().getLogin());
            System.out.println(user.getValue());
            try {
                if (new String(decryptKey(text, user.getValue())).equals(user.getKey().getLogin())) return user;
            }catch(Exception e){

            }
        }

        return null;
    }

    private String encryptInt(byte number, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.getEncoder().encodeToString(cipher.doFinal(new byte[]{number}));
    }

    private byte decryptInt(String number, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(Base64.getDecoder().decode(number))[0];
    }
}
