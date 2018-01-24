package src.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * ChattiSofta created by Tijam Moradi on 11/10/17.
 * @version : 1.1
 *
 * @author Tijam Moradi tikimo@utu.fi
 */
public class CustomerThread implements Runnable {
    // Luokkamuuttujat
    private Socket socket;
    private PrintWriter writer;
    private ChatServer server;

    /**
     * Konstruktori
     * @param chatServer Ottaa palvelimen parametrikseen
     * @param socket Soketti "kopioidaan"
     */
    public CustomerThread(ChatServer chatServer, Socket socket)  {
        this.server = chatServer;
        this.socket = socket;
    }

    private PrintWriter getWriter() {
        return writer;
    }

    /**
     * Runnable malli. Säikeen logiikka, eli itse chattaaminen tapahtuu täällä.
     */
    @Override
    public void run() {
        try {
            // I/O asetukset ensin
            this.writer = new PrintWriter(socket.getOutputStream(), false);
            Scanner scanner = new Scanner(socket.getInputStream());

            while (!socket.isClosed()) {   // Aloitetaan prosessi jos socket on auki
                if (scanner.hasNextLine()) { // katsotaan onko tullut syötettä
                    String input = scanner.nextLine() + "\r\n";  // luetaan syöte ja vaihdetaan riviä
                    System.err.println("Debug: " + input);

                    for (CustomerThread client : server.getClients()) {  // lähetetään luettu viesti kaikille asiakkaille
                        PrintWriter clientWriter = client.getWriter();
                        if (clientWriter != null) { // vältetään virhetilanne
                            clientWriter.write(input);  // kirjoitetaan viesti asiakkaalle
                            clientWriter.flush(); // valmistellaan writer seuraavaa viestiä varten
                        }
                    }
                }
            }

        } catch (IOException e) {   // virheen hallinta
            e.printStackTrace();
        }

    }
}
