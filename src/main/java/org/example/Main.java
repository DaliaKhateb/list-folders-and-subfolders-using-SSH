package org.example;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.jcraft.jsch.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;


public class Main {

    private static  String REMOTE_HOST; //= "10.0.0.17";
    private static  String USERNAME;// = "akhateb";
    private static  String PASSWORD;// = "1205geneve";
    private static  int REMOTE_PORT = 22;
    private static String PATH;

    static Channel channel=null;
    static Session jschSession = null;
    static ChannelSftp sftpChannel=null;

    public static void main(String[] args) {


        if(args.length<4 || args.length>4){
            System.err.println("Invalid input");
        }
        USERNAME=args[0];
        PASSWORD=args[1];
        REMOTE_HOST=args[2];
        PATH= args[3];

        //String PATH="/C:/Users/akhateb/desktop/sweet students";

        try {

            JSch jsch = new JSch();
            jschSession = jsch.getSession(USERNAME, REMOTE_HOST, REMOTE_PORT);

            jschSession.setConfig("StrictHostKeyChecking", "no");
            // authenticate using password
            jschSession.setPassword(PASSWORD);

            System.out.println("Connecting------");
            jschSession.connect();
            System.out.println("connection succeeded");

            channel = jschSession.openChannel("sftp");
            channel.connect();
            sftpChannel=(ChannelSftp)channel;

            System.out.println("Opened sftp Channel");


            sftpChannel.cd(PATH);
            printContents(PATH);
            System.out.println("**********************************************");


            sftpChannel.disconnect();
            jschSession.disconnect();
            System.out.println("Disconnected from sftp");

        } catch (Exception e) {

            e.printStackTrace();

        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }

    }

    public static void printContents(String maindirpath) throws SftpException {

        System.out.println(
                "**********************************************");
        System.out.println(
                "Files from main directory : " + maindirpath);
        System.out.println(
                "**********************************************");
        RecursivePrint(maindirpath,0,0);

    }

    public static void RecursivePrint( String path, int index, int level)throws SftpException
    {
        Vector<ChannelSftp.LsEntry> arr = sftpChannel.ls(path);
        // terminate condition
        if (index == arr.size())
            return;

        // tabs for internal levels
        for (int i = 0; i < level; i++)
            System.out.print("\t");

        // for files
        if (!arr.get(index).getAttrs().isDir())
            System.out.println(arr.get(index).getFilename());

            // for sub-directories
        else if (arr.get(index).getAttrs().isDir()) {
            System.out.println("[" + arr.get(index).getFilename()
                    + "]");

            // recursion for sub-directories
            RecursivePrint(path + "/" + arr.get(index).getFilename(), 0,
                    level + 1);
        }

        // recursion for main directory
        RecursivePrint(path, ++index, level);
    }



}
