package palvelin;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * ChattiSofta created by Tijam Moradi on 11/10/17.
 * Version: 1.1
 *
 * @author Tijam Moradi tikimo@utu.fi
 * @author Niklas Kiuru nioski@utu.fi
 * @author Asad Ijaz asaija@utu.fi
 */
public class AsiakasSaie implements Runnable {
    // Luokkamuuttujat
    private Socket soketti;
    private PrintWriter kirjoitin;
    private ChattiPalvelin palvelin;

    /**
     * Konstruktori
     * @param chattiPalvelin Ottaa palvelimen parametrikseen
     * @param soketti Soketti "kopioidaan"
     */
    public AsiakasSaie(ChattiPalvelin chattiPalvelin, Socket soketti)  {
        this.palvelin = chattiPalvelin;
        this.soketti = soketti;
    }

    private PrintWriter getKirjoitin() {
        return kirjoitin;
    }

    @Override
    public void run() {
        try {
            // Asetukset ensin
            this.kirjoitin = new PrintWriter(soketti.getOutputStream(), false);
            Scanner lukija = new Scanner(soketti.getInputStream());

            while (!soketti.isClosed()) {   // Aloitetaan yhteys
                if (lukija.hasNextLine()) {
                    String syote = lukija.nextLine() + "\r\n";
                    System.err.println("Debug: " + syote);

                    for (AsiakasSaie asiakas : palvelin.annaAsiakkaat()) {
                        PrintWriter asiakasKirjoitin = asiakas.getKirjoitin();
                        if (asiakasKirjoitin != null) {
                            asiakasKirjoitin.write(syote);
                            asiakasKirjoitin.flush();
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
