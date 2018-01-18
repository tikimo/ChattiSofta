package src.palvelin;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * ChattiSofta created by Tijam Moradi on 11/10/17.
 * @version : 1.1
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

    /**
     * Runnable malli. Säikeen logiikka, eli itse chattaaminen tapahtuu täällä.
     */
    @Override
    public void run() {
        try {
            // I/O asetukset ensin
            this.kirjoitin = new PrintWriter(soketti.getOutputStream(), false);
            Scanner lukija = new Scanner(soketti.getInputStream());

            while (!soketti.isClosed()) {   // Aloitetaan prosessi jos soketti on auki
                if (lukija.hasNextLine()) { // katsotaan onko tullut syötettä
                    String syote = lukija.nextLine() + "\r\n";  // luetaan syöte ja vaihdetaan riviä
                    System.err.println("Debug: " + syote);

                    for (AsiakasSaie asiakas : palvelin.annaAsiakkaat()) {  // lähetetään luettu viesti kaikille asiakkaille
                        PrintWriter asiakasKirjoitin = asiakas.getKirjoitin();
                        if (asiakasKirjoitin != null) { // vältetään virhetilanne
                            asiakasKirjoitin.write(syote);  // kirjoitetaan viesti asiakkaalle
                            asiakasKirjoitin.flush(); // valmistellaan kirjoitin seuraavaa viestiä varten
                        }
                    }
                }
            }

        } catch (IOException e) {   // virheen hallinta
            e.printStackTrace();
        }

    }
}
