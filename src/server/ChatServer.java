package src.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * ChattiSofta created by Tijam Moradi on 11/10/17.
 * @version 1.1
 *
 * @author Tijam Moradi tikimo@utu.fi
 */
public class ChatServer {
    //Luokkamuuttujat
    private static final int portNumber = 4444;   //porttinumero oltava sama kuin asiakkaalla

    private int serverPort;
    private List<CustomerThread> customers;    // voi olla myös protected


    /**
     * Main metodi, käynnistää uuden palvelimen luomalla ChatServer olion.
     *
     * @param args argumentit
     */
    public static void main(String[] args) {
        new ChatServer(portNumber);
    }

    /**
     * Konstruktori, käynnistää tämän (this) palvelimen annetulla portilla
     *
     * @param portNumber porttinumero
     */
    private ChatServer(int portNumber) {
        this.serverPort = portNumber;
        this.kaynnistaPalvelin();
    }

    /**
     * Metodi palauttaa customers (niiden säikeet) listana.
     */
    public List<CustomerThread> getClients() {
        return customers;
    }


    /**
     * Suoritettava osio. Tässä metodissa tulee esille sovelluksen logiikka.
     *
     * Try-catch:illa saadaan otettua kiinni portin ongelmat, muita ongelmia ei
     * juurikaan tule, poislukien asiakkaiden yhteysongelmat. Ne käsitellään ulkoistetussa
     * metodissa acceptClients()
     *
     */
    private void kaynnistaPalvelin() {
        customers = new ArrayList<>();
        ServerSocket serverSocket = null;    //alustetaan palvelinsoketti tyhjaksi

        try {
            serverSocket = new ServerSocket(serverPort); // luodaan soketti
            acceptClients(serverSocket); // sukelletaan ulkoistettuun metodiin
        } catch (IOException e){
            System.err.println("Porttia "+ serverPort +" ei voi kuunnella");
            System.exit(1); // suljetaan server jos tuli yhteysvirhe
        }

    }

    /**
     * Metodissa käsitellään asiakkaita. Niitä otetaan olioina listaan
     * customers hallintaa varten. Customer otetaan vastaan soketilla ja
     * luodaan uusi client säie.
     *
     * @param palvelinSoketti palvelimen soketti
     */
    private void acceptClients(ServerSocket palvelinSoketti) {

        System.out.println("Serveri avaa portin: " + palvelinSoketti.getLocalSocketAddress());
        while(true) {
            try {
                Socket socket = palvelinSoketti.accept();  // avataan socket kuuntelulle
                System.out.println("Otetaan vastaan: "+socket.getRemoteSocketAddress());
                CustomerThread client = new CustomerThread(this, socket);   // luodaan uusi asiakasSäie (runnable)
                Thread clientThread = new Thread(client);  // luodaan asiakkaalle säie
                clientThread.start();   // suoritetaan säie
                customers.add(client); // lisätään client asiakkaiden listaan hallinnointia varten
            } catch (IOException e) {
                System.out.println("Ei voinut hyväksyä yhteyttä: "+ serverPort); // Otetaan virheet huomioon
            }
        }

    }

}
