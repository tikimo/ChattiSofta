package src.palvelin;

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
 * @author Niklas Kiuru nioski@utu.fi
 * @author Asad Ijaz asaija@utu.fi
 */
public class ChattiPalvelin {
    //Luokkamuuttujat
    private static final int porttiNumero = 4444;   //porttinumero oltava sama kuin asiakkaalla

    private int serverinPortti;
    private List<AsiakasSaie> asiakkaat;    // voi olla myös protected


    /**
     * Main metodi, käynnistää uuden palvelimen luomalla ChattiPalvelin olion.
     *
     * @param args argumentit, tarpeeton
     */
    public static void main(String[] args) {
        new ChattiPalvelin(porttiNumero);
    }

    /**
     * Konstruktori, käynnistää tämän (this) palvelimen annetulla portilla
     *
     * @param porttiNumero portin numero
     */
    private ChattiPalvelin(int porttiNumero) {
        this.serverinPortti = porttiNumero;
        this.kaynnistaPalvelin();
    }

    /**
     * Metodi palauttaa asiakkaat (niiden säikeet) listana.
     */
    public List<AsiakasSaie> annaAsiakkaat() {
        return asiakkaat;
    }


    /**
     * Suoritettava osio. Tässä metodissa tulee esille sovelluksen logiikka.
     *
     * Try-catch:illa saadaan otettua kiinni portin ongelmat, muita ongelmia ei
     * juurikaan tule, poislukien asiakkaiden yhteysongelmat. Ne käsitellään ulkoistetussa
     * metodissa hyvaksyAsiakkaita()
     *
     */
    private void kaynnistaPalvelin() {
        asiakkaat = new ArrayList<>();
        ServerSocket palvelinSoketti = null;    //alustetaan palvelinsoketti tyhjaksi

        try {
            palvelinSoketti = new ServerSocket(serverinPortti); // luodaan soketti
            hyvaksyAsiakkaita(palvelinSoketti); // sukelletaan ulkoistettuun metodiin
        } catch (IOException e){
            System.err.println("Porttia "+serverinPortti+" ei voi kuunnella");
            System.exit(1); // suljetaan palvelin jos tuli yhteysvirhe
        }

    }

    /**
     * Metodissa käsitellään asiakkaita. Niitä otetaan olioina listaan
     * asiakkaat hallintaa varten. Asiakas otetaan vastaan soketilla ja
     * luodaan uusi asiakas säie.
     *
     * @param palvelinSoketti palvelimen soketti
     */
    private void hyvaksyAsiakkaita(ServerSocket palvelinSoketti) {

        System.out.println("Serveri avaa portin: " + palvelinSoketti.getLocalSocketAddress());
        while(true) {
            try {
                Socket soketti = palvelinSoketti.accept();  // avataan soketti kuuntelulle
                System.out.println("Otetaan vastaan: "+soketti.getRemoteSocketAddress());
                AsiakasSaie asiakas = new AsiakasSaie(this, soketti);   // luodaan uusi asiakasSäie (runnable)
                Thread saie = new Thread(asiakas);  // luodaan asiakkaalle säie
                saie.start();   // suoritetaan säie
                asiakkaat.add(asiakas); // lisätään asiakas asiakkaiden listaan hallinnointia varten
            } catch (IOException e) {
                System.out.println("Ei voinut hyväksyä yhteyttä: "+serverinPortti); // Otetaan virheet huomioon
            }
        }

    }

}
