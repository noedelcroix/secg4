package sec.client;

import sec.common.BasicMessage;
import sec.common.MsgType;
import sec.common.TextMessage;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Scanner;
import java.net.Socket;

public class BasicTextClient
{
    String ip;
    int port;
    String username;
    private Key serverKey;
    private Key publicKey;
    private PrivateKey privateKey;

    private int counter;

    public BasicTextClient(String ip, int port)
    {
        this.ip = ip;
        this.port = port;
        this.username=null;

        try {
            generateKeys();
            this.serverKey = getServerPublicKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start()
    {
        try (Socket socket = new Socket(ip, port);
             ObjectOutputStream out =
                     new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in =
                     new ObjectInputStream(socket.getInputStream()))
        {

            Scanner scanner = new Scanner(System.in);
            String line;
            System.out.print("> ");
            while (scanner.hasNextLine() &&
                   !(line = scanner.nextLine()).equalsIgnoreCase("exit"))
            {
                try
                {
                    int sepIdx = line.indexOf(' ');
                    MsgType msgType = MsgType.valueOf(
                            line.substring(0, sepIdx).toUpperCase());
                    String textData = line.substring(sepIdx + 1);
                    if(msgType==MsgType.REGISTER){
                        String username = textData.substring(0, textData.indexOf(' '));
                        String password = textData.substring(textData.indexOf(' ')+1);
                        if(isPasswordValid(password))
                            textData=username+" "+encryptPassword(password);
                        else{
                            System.out.println("Password length has to be between 6 and 8 and alphanumeric");
                            continue;
                        }
                    }else if(msgType==MsgType.LOGIN){
                        String username = textData.substring(0, textData.indexOf(' '));
                        this.username=username;
                        String password = textData.substring(textData.indexOf(' ')+1);
                        textData=username+" "+encryptPassword(password)+" "+encryptKey(this.publicKey.getEncoded(), this.serverKey);
                    }else if(msgType==msgType.FATHER){
                        if(this.username!=null) {
                            textData = textData + " " + encryptKey(this.username.getBytes(), this.privateKey);
                        }
                    }
                    this.counter = 1;
                    out.writeObject(new TextMessage(encryptInt((byte)this.counter, this.privateKey), textData, msgType));
                    out.flush();
                    System.out.println(in.readObject().toString());
                }
                catch (IllegalArgumentException | IndexOutOfBoundsException ex)
                {
                    System.out.println("Unknown command");
                }
                System.out.print("> ");
            }
            System.out.println("Graceful exit");
            out.writeObject(BasicMessage.EXIT_MSG);
        }
        catch (EOFException ex)
        {
            System.err.println("The server unexpectedly closed the connection");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private String encryptPassword(String password) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, this.serverKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(password.getBytes()));
    }

    private static boolean isPasswordValid(String password) {
        return(password !=null && password.length()>=6 && password.length()<=8 && password.matches("^[a-zA-Z\\d]*$"));
    }

    private static Key getServerPublicKey() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] publicKeyBytes = Files.readAllBytes(new File("server/public/public.key").toPath());
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        return keyFactory.generatePublic(publicKeySpec);
    }

    private void generateKeys() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair pair = keyGen.generateKeyPair();
        this.publicKey=pair.getPublic();
        this.privateKey=pair.getPrivate();
    }

    private String encryptKey(byte[] message, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.getEncoder().encodeToString(cipher.doFinal(message));
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