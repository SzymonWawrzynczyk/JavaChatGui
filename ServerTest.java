package SocketTut;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerTest {


    public static void main(String[] args) throws IOException {//tworzymy serwer monitorujacy port 8189:
        ServerSocket s = new ServerSocket(8189);
        ExecutorService executor = Executors.newCachedThreadPool();
        List<SocketHandler> socketHandlers= Collections.synchronizedList(new ArrayList<>());
        System.out.println("dziala?");
        Map<String,String> authenticationMap = load_map_from_file();
        authenticationMap.put("Szymon","Szymon1");
        authenticationMap.put("ClientTest","PasswordTest");
        System.out.println(authenticationMap);
        while(true){
            Socket incoming = s.accept();
            synchronized (socketHandlers){
                SocketHandler sh = new SocketHandler(incoming);
                synchronized (socketHandlers) {
                    socketHandlers.add(sh);
                    System.out.println(socketHandlers);
                }

                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        while (sh.getIn().hasNextLine()) {
                            String text = sh.getIn().nextLine();
                            if(sh.getIs_logged_in()){
                                synchronized (socketHandlers) {
                                    for(SocketHandler sh_ : socketHandlers) {
                                        sh_.getOut().println(text);
                                    }
                                }
                            } else{
                                System.out.println("Linia od niezarejestrowanego uzytkownika "+text);
                                String tablica[] = text.split(" ");
                                System.out.println("Logika");
                                System.out.println("tablica[0]"+tablica[0]);
                                if(tablica[0].equals("login")){
                                    System.out.println("Logowanie");
                                    if(tablica[2].equals(authenticationMap.get(tablica[1]))){
                                        sh.set_logged_in();
                                        sh.getOut().println("Zalogowano pomyslnie");
                                        System.out.println("Logowanie pomyslne");
                                    }else{
                                        sh.getOut().println("Bledne dane logowania");
                                        System.out.println("Logowanie niepomyslne");
                                    }
                                }
                                if(tablica[0].equals("register")){
                                    System.out.println("rejestracja");
                                    authenticationMap.put(tablica[1],tablica[2]);
                                    sh.getOut().println("Zarejestrowano uzytkownika");
                                    try {
                                        save_map_to_file(authenticationMap);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        }
                        synchronized (socketHandlers) {
                            socketHandlers.remove(sh);
                            System.out.println(socketHandlers);
                        }
                    }
                });
        }
        }
    }

    private static void save_map_to_file(Map<String,String> map) throws IOException {
        FileWriter myWriter = new FileWriter("data.csv");
        for(String key: map.keySet()){
            myWriter.write(key+";"+map.get(key).toString()+"\n");
        }
        myWriter.close();
    }

    private static Map<String, String> load_map_from_file() {
        Map<String,String> result_map= new HashMap<String,String>();
        try {
            BufferedReader  bufferreader  = new BufferedReader(new FileReader("data.csv"));
            String line = bufferreader.readLine();
            while(line!=null){
                String s[] = line.split(";");
                result_map.put(s[0],s[1]);
                line = bufferreader.readLine();
            }
            bufferreader.close();

        }catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result_map;
    }
}
