package src.asiakas;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * ChattiSofta created by Tijam Moradi on 11/10/17.
 * @version : 1.3
 *
 *
 * @author Tijam Moradi tikimo@utu.fi
 *
 */
public class ServerThread implements Runnable{
    private Socket socket;
    private String userName;
    private final LinkedList<String> messages;
    private boolean hasMessages = false;

    /**
     * Alustaa luokkamuuttujat.
     *
     * @param socket soketti
     * @param userName käyttäjänimi
     */
    ServerThread(Socket socket, String userName) {
        this.socket = socket;
        this.userName = userName;
        messages = new LinkedList<>();
    }

    /**
     * Lisää synkronoidun viestin linkedlistiin, eli "työjonoon"
     * ja asettaa veistijonon voimassaolevaksi (hasMessages).
     * @param newMessage viestin sisältö
     */
    public void getNewMessage(String newMessage) {
        synchronized (messages) {        // Synkronoinnilla vältetään konfliktit työjonon kanssa.
            hasMessages = true;
            messages.push(newMessage);
        }
    }

    @Override
    /**
     * Suoritettava metodi. Viestit ovat synkronoitu, jotta ei tapahdu konflikteja
     * suorittaessa "getNewMessage":ä. Metodi käyttää linked listiä viestien
     * lajitteluun, josta syntyy ns. työjono.
     *
     */
    public void run() {
        // Tervetuliaiset
        System.out.println("Tervetuloa ChattiSoftaan, " + userName);
        System.out.println("Paikallinen portti: " + socket.getLocalPort());
        System.out.println("Palvelin: " + socket.getRemoteSocketAddress());

        try {
            // Alustetaan sokettiin luku ja kirjoitus
            PrintWriter serverPrint = new PrintWriter(socket.getOutputStream(), false);    // Kirjoitetaan
            InputStream serverReceiver = socket.getInputStream();                              // Luetaan
            Scanner scanner = new Scanner(serverReceiver);   // Luetaan serveristä, ei System.in:stä


            while (!socket.isClosed()) {   // Jos kaikki pelaa, niin socket on auki ja while toteutuu.
                if (serverReceiver.available() > 0 ) {  // Jos soketin sisääntulossa on tavaraa
                    if (scanner.hasNextLine()) {
                        System.out.println(scanner.nextLine());  // Tulostaa: *userName* > *lukija.nextline*
                                                                // ja tekee lukijaan rivinvaihdon
                    }
                }

                if (hasMessages && messages.size() > 0) {     // tarkistetaan myös viestijonon koko, ettei tule
                                                            // luku ennen kirjoitusta ongelmaa
                    String nextMessage = messages.getFirst(); // Palautetaan päällimmäinen viesti

                    serverPrint.println(userName + " > " + nextMessage); // Tulostaa viestin
                    serverPrint.flush();

                    synchronized (messages) {    // Synkronoidaan ettei ongelmia tule samanaikaisten viestinlisäysten kanssa
                        messages.pop(); // poistetaan päällimmäinen (eka) viesti
                        hasMessages = !messages.isEmpty();    //Jos viestijono on tyhjä, viestejä ei enää ole >> false
                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
