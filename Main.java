package SwingFinal;

import org.json.JSONObject;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;


public class Main {
    static String city= "Lublin";
    static String imie = "Szymon";
    static boolean is_Logged_in=false;
    private final static String newline = "\n";
    public static void main(String[] args) throws IOException {

        //Creating the Frame
        JFrame frame = new JFrame("Chat Frame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        //Creating the MenuBar and adding components
        JMenuBar mb = new JMenuBar();
        JMenu m1 = new JMenu("Weather");
        JMenu m2 = new JMenu("Help");
        mb.add(m1);
        mb.add(m2);
        JMenuItem m11 = new JMenuItem("Show");
        JMenuItem m22 = new JMenuItem("Change location");
        m11.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=d3280d7598f9d5220b33a10e9ac60652&units=metric"))
                        .build();

                HttpResponse<String> response = null;
                try {
                    response = client.send(request,
                            HttpResponse.BodyHandlers.ofString());
                    System.out.println(response.body());
                    JSONObject jsonObject = new JSONObject(response.body());
                    System.out.println(jsonObject.getJSONObject("main").get("temp"));
                    //custom title, no icon
                    JOptionPane.showMessageDialog(frame,
                            "Temperatura : "+jsonObject.getJSONObject("main").get("temp")+"\n Pogoda: "+jsonObject.getJSONArray("weather").getJSONObject(0).get("description"),
                            "Pogoda w "+city,
                            JOptionPane.PLAIN_MESSAGE);
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });
        m22.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String m = JOptionPane.showInputDialog("Jakie miasto?");
                System.out.println(m);
                city = m;
            }
        });
        m1.add(m11);
        m1.add(m22);


        //Creating the panel at bottom and adding components
        JPanel panel = new JPanel(); // the panel is not visible in output
        JLabel label = new JLabel("Enter Text");
        JTextField tf = new JTextField(10); // accepts upto 10 characters
        JButton send = new JButton("Send");
        panel.add(label); // Components Added using Flow Layout
        panel.add(tf);
        panel.add(send);

        // Text Area at the Center
        JTextArea ta = new JTextArea();

        //Scrollable list at the right
        DefaultListModel listModel = new DefaultListModel();
        listModel.addElement("Working hard");
        listModel.addElement("Working medium");
        listModel.addElement("Working light");

        JList list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        ListSelectionListener lsl = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(e.getValueIsAdjusting()) {
                    JList list = (JList) e.getSource();
                    int selections[] = list.getSelectedIndices();
                    Object selectionValues[] = list.getSelectedValues();
                    for (int i = 0, n = selections.length; i < n; i++) {
                        if (i == 0) {
                            System.out.println("Selections: ");
                        }
                        System.out.println(selections[i] + "/" + selectionValues[i] + " ");
                    }
                }
        }};

        list.addListSelectionListener(lsl);


        JScrollPane scrollableList = new JScrollPane(list);
        JScrollPane scrollableTextArea = new JScrollPane(ta);


        //Adding Components to the frame.
        frame.getContentPane().add(BorderLayout.SOUTH, panel);
        frame.getContentPane().add(BorderLayout.NORTH, mb);
        frame.getContentPane().add(BorderLayout.CENTER, scrollableTextArea);
        frame.getContentPane().add(BorderLayout.EAST, scrollableList);
        frame.setVisible(true);


        Socket s = new Socket("localhost", 8189);
        InputStream inputStream = s.getInputStream();
        OutputStream outputStream = s.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream, true);
        Scanner scanner = new Scanner(System.in);
        Scanner socketScanner = new Scanner(inputStream);

        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(is_Logged_in) {
                    String text = tf.getText();
                    String tablica[] = text.split(" ");
                    imie= tablica[1];
                    printWriter.println(imie + ": " + tf.getText());
                }
                else{
                    printWriter.println(tf.getText());
                }
                printWriter.flush();
            }
        });


        SwingWorker<Boolean, Integer> worker2 = new SwingWorker<Boolean, Integer>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                while (true) {
                    String m =socketScanner.nextLine();
                    if(!m.isEmpty()){
                        ta.append(m+newline);
                        if(m.contains("Zalogowano pomyslnie")){
                            is_Logged_in=true;
                        }
                    }
                }
            }
        };

        worker2.execute();

    }
}

