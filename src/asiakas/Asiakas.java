package asiakas;

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
 * @author Niklas Kiuru nioski@utu.fi
 * @author Asad Ijaz asaija@utu.fi
 */
public class Asiakas {
    // Luokkamuuttujat
    private static final String host = "localhost";
    private static final int porttiNumero = 4444;

    private String kayttajaNimi, serverHost;
    private int palvelinPortti;


    /**
     * Main metodi käynnistää chattaaja - säikeen private void käynnistä - metodista
     * annetuilla parametreilla. Main - metodi ei hyödynnä itsessään parametreja.
     *
     * @param args Ei käyttöä.
     */
    public static void main(String[] args) {
        String lueNimi;
        Scanner lukija = new Scanner(System.in);
        System.out.println("Anna käyttäjänimi: ");
        lueNimi = lukija.nextLine();                // Oletetaan, ettei käyttäjänimi ole tyhjä tms turhake

        Asiakas chattaaja = new Asiakas(lueNimi, host, porttiNumero);
        chattaaja.kaynnista(lukija);                // Käynnistää chattaus-threadin
    }

    /**
     * Konstruktori Asiakkaalle.
     *
     * @param nimi käyttäjänimi, joka esiintyy chattaamisessa
     * @param host Palvelimen osoite, esim localhost tai 127.0.0.1
     * @param porttiNumero Portti, jossa palvelin operoi
     */
    private Asiakas(String nimi, String host, int porttiNumero) {
        this.kayttajaNimi = nimi;
        this.serverHost = host;
        this.palvelinPortti = porttiNumero;
    }

    /**
     * Metodi käynnistää chattaajan (Asiakas). Metodin säie pysyy elossa,
     * kunnes käyttäjä terminoi sovelluksen.
     * Metodi avaa yhteyden ServeriSaie-olion kautta, joka on runnable. Asiakas syöttää tähän viestinsä ja
     * viesti välittyy palvelimelle säikeen kautta. Tämä metodi EI SIIS hoida yhteyttä, ainoastan sokettia.
     *
     * @param lukija ottaa Scanner-olion (System.in)
     */
    private void kaynnista(Scanner lukija) {
        System.out.println("Chat käynnistetty!");
        try {   // try-catch keskeytystä tai yhteysongelman vuoksi
            Socket soketti = new Socket(serverHost, palvelinPortti);    // Soketin asetukset luokasta
            Thread.sleep(1000);                                      // Estetään Yhteyden keskeytys
            ServeriSaie serveriSaie = new ServeriSaie(soketti, kayttajaNimi);   // Luodaan säie-olio
            Thread serverinYhteysSaie = new Thread(serveriSaie);        // Luodaan oliosta varsinainen säie
            serverinYhteysSaie.start();                                 // Käynnistetään ServeriSäie.run()
            System.out.println("Serverin yhteyssäie avattu... " + serverinYhteysSaie.getState()
                    + ", elossa: " + serverinYhteysSaie.isAlive());     // Tulostetaan konfirmaatio

            while (serverinYhteysSaie.isAlive()) {                  // Jos säie on aktiivinen
                String nykyinenViesti;
                if (lukija.hasNextLine()) {                         // Odottaa syötettä, pysäyttää säikeen
                    nykyinenViesti = lukija.nextLine();
                    serveriSaie.lisaaUusiViesti(nykyinenViesti);    // Lisätään viesti työjonoon
                }
            }

        } catch (IOException ioe) {             // Soketti
            System.err.println("Yhteysvirhe!");
        } catch (InterruptedException ie) {     // Thread.sleep()
            System.err.println("Keskeytys...");
        }
    }


}
