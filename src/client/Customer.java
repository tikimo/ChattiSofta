package src.client;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * ChattiSofta created by Tijam Moradi on 11/10/17.
 * @version : 1.1
 *
 * TODO: SSL-implementaatio
 *
 * @author Tijam Moradi tikimo@utu.fi
 */
public class Customer {
    // Luokkamuuttujat
    private static final String host = "localhost";
    private static final int portNumber = 4444;

    private String userName, serverHost;
    private int serverPort;


    /**
     * Main metodi käynnistää chattaaja - säikeen private void käynnistä - metodista
     * annetuilla parametreilla. Main - metodi ei hyödynnä itsessään parametreja.
     *
     * @param args Ei käyttöä.
     */
    public static void main(String[] args) {
        String readName;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Anna käyttäjänimi: ");
        readName = scanner.nextLine();                // Oletetaan, ettei käyttäjänimi ole tyhjä tms turhake

        Customer chatter = new Customer(readName, host, portNumber);
        chatter.launch(scanner);                // Käynnistää chattaus-threadin
    }

    /**
     * Konstruktori Asiakkaalle.
     *
     * @param name käyttäjänimi, joka esiintyy chattaamisessa
     * @param host Palvelimen osoite, esim localhost tai 127.0.0.1
     * @param portNumber Portti, jossa server operoi
     */
    private Customer(String name, String host, int portNumber) {
        this.userName = name;
        this.serverHost = host;
        this.serverPort = portNumber;
    }

    /**
     * Metodi käynnistää chattaajan (Customer). Metodin säie pysyy elossa,
     * kunnes käyttäjä terminoi sovelluksen.
     * Metodi avaa yhteyden ServerThread-olion kautta, joka on runnable. Customer syöttää tähän viestinsä ja
     * viesti välittyy palvelimelle säikeen kautta. Tämä metodi EI SIIS hoida yhteyttä, ainoastan sokettia.
     *
     * @param scanner ottaa Scanner-olion (System.in)
     */
    private void launch(Scanner scanner) {
        System.out.println("Chat käynnistetty!");
        try {   // try-catch keskeytystä tai yhteysongelman vuoksi
            Socket socket = new Socket(serverHost, serverPort);    // Soketin asetukset luokasta
            Thread.sleep(1000);                                      // Estetään Yhteyden keskeytys
            ServerThread serverThread = new ServerThread(socket, userName);   // Luodaan säie-olio
            Thread serverConnectionThread = new Thread(serverThread);        // Luodaan oliosta varsinainen säie
            serverConnectionThread.start();                                 // Käynnistetään ServeriSäie.run()
            System.out.println("Serverin yhteyssäie avattu... " + serverConnectionThread.getState()
                    + ", elossa: " + serverConnectionThread.isAlive());     // Tulostetaan konfirmaatio

            while (serverConnectionThread.isAlive()) {                  // Jos säie on aktiivinen
                String currentMessage;
                if (scanner.hasNextLine()) {                         // Odottaa syötettä, pysäyttää säikeen
                    currentMessage = scanner.nextLine();
                    serverThread.getNewMessage(currentMessage);    // Lisätään viesti työjonoon
                }
            }

        } catch (IOException ioe) {             // Soketti
            System.err.println("Yhteysvirhe!");
        } catch (InterruptedException ie) {     // Thread.sleep()
            System.err.println("Keskeytys...");
        }
    }


}
