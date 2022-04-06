package sec.client;

import sec.common.BasicMessage;
import sec.common.MsgType;
import sec.common.TextMessage;

import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Scanner;
import java.net.Socket;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class BasicTextClient
{
    String ip;
    int port;
    private Key serverKey;

    public BasicTextClient(String ip, int port)
    {
        this.ip = ip;
        this.port = port;
        try {
            this.serverKey = getPublicKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Key getPublicKey() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        File publicKeyFile = new File("server/public/public.key");
        byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        return keyFactory.generatePublic(publicKeySpec);
    }

    private String encryptPassword(String password) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, serverKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(password.getBytes()));
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
                    }
                    if(msgType==MsgType.LOGIN){
                        String username = textData.substring(0, textData.indexOf(' '));
                        String password = textData.substring(textData.indexOf(' ')+1);
                        textData=username+" "+encryptPassword(password);
                    }
                    textData+=" " + socket.getLocalPort();
                    out.writeObject(new TextMessage(textData, msgType));
                    out.flush();
                    System.out.println((String) in.readObject());
                }
                catch (IllegalArgumentException | IndexOutOfBoundsException ex)
                {
                    System.out.println("Unknown command");
                }
                System.out.print("> ");
            }
            System.out.println("Graceful exit");
            out.writeObject(BasicMessage.EXIT_MSG);
            out.writeObject(new TextMessage(socket.getLocalPort()+"",MsgType.EXIT));
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
    private boolean isPasswordValid(String password) {
        if (password == null) {
            return false;
        }
        if (password.length() < 6 || password.length() > 8) {
            return false;
        }
        return password.matches("^[a-zA-Z0-9]*$");
    }
}