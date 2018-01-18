package asiakas;

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
 * @author Niklas Kiuru nioski@utu.fi
 * @author Asad Ijaz asaija@utu.fi
 *
 */
public class ServeriSaie implements Runnable{
    private Socket soketti;
    private String kayttajaNimi;
    private final LinkedList<String> viestit;
    private boolean onViesteja = false;

    /**
     * Alustaa luokkamuuttujat.
     *
     * @param soketti
     * @param kayttajaNimi
     */
    public ServeriSaie(Socket soketti, String kayttajaNimi) {
        this.soketti = soketti;
        this.kayttajaNimi = kayttajaNimi;
        viestit = new LinkedList<>();
    }

    /**
     * Lisää synkronoidun viestin linkedlistiin, eli "työjonoon"
     * ja asettaa veistijonon voimassaolevaksi (onViesteja).
     * @param uusiViesti
     */
    public void lisaaUusiViesti(String uusiViesti) {
        synchronized (viestit) {        // Synkronoinnilla vältetään konfliktit työjonon kanssa.
            onViesteja = true;
            viestit.push(uusiViesti);
        }
    }

    @Override
    /**
     * Suoritettava metodi. Viestit ovat synkronoitu, jotta ei tapahdu konflikteja
     * suorittaessa "lisaaUusiViesti":ä. Metodi käyttää linked listiä viestien
     * lajitteluun, josta syntyy ns. työjono.
     *
     */
    public void run() {
        // Tervetuliaiset
        System.out.println("Tervetuloa ChattiSoftaan, " + kayttajaNimi);
        System.out.println("Paikallinen portti: " + soketti.getLocalPort());
        System.out.println("Palvelin: " + soketti.getRemoteSocketAddress());

        try {
            // Alustetaan sokettiin luku ja kirjoitus
            PrintWriter serveriTulostus = new PrintWriter(soketti.getOutputStream(), false);    // Kirjoitetaan
            InputStream serveriVastaanotto = soketti.getInputStream();                              // Luetaan
            Scanner lukija = new Scanner(serveriVastaanotto);   // Luetaan serveristä, ei System.in:stä


            while (!soketti.isClosed()) {   // Jos kaikki pelaa, niin soketti on auki ja while toteutuu.
                if (serveriVastaanotto.available() > 0 ) {  // Jos soketin sisääntulossa on tavaraa
                    if (lukija.hasNextLine()) {
                        System.out.println(lukija.nextLine());  // Tulostaa: *kayttajaNimi* > *lukija.nextline*
                                                                // ja tekee lukijaan rivinvaihdon
                    }
                }

                if (onViesteja) {
                    String seuraavaViesti = viestit.getFirst(); // Palautetaan päällimmäinen viesti

                    serveriTulostus.println(kayttajaNimi + " > " + seuraavaViesti); // Tulostaa viestin
                    serveriTulostus.flush();

                    synchronized (viestit) {    // Synkronoidaan ettei ongelmia tule samanaikaisten viestinlisäysten kanssa
                        seuraavaViesti = viestit.pop(); // poistetaan päällimmäinen (eka) viesti
                        onViesteja = !viestit.isEmpty();    //Jos viestijono on tyhjä, viestejä ei enää ole >> false
                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
