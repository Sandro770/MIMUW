package com.company.strategia.kupowania;

import com.company.agent.Robotnik;
import com.company.giełda.Giełda;
import com.company.oferta.Oferta;
import com.company.oferta.OfertaRobotnika;
import com.company.produkt.FabrykaProduktów;
import com.company.produkt.Produkt;

import static com.company.oferta.Oferta.Typ.KUPNA;
import static com.company.produkt.Produkt.Typ.NARZĘDZIA;

public abstract class kupujacyNarzedzia extends StrategiaKupowaniaIStosowaniaProgramów {
    protected final int liczba_narzedzi;

    protected kupujacyNarzedzia(int liczba_narzedzi) {
        this.liczba_narzedzi = liczba_narzedzi;
    }

    protected void kupNarzedzia(Robotnik robotnik, Giełda giełda) {
        Produkt produkt = FabrykaProduktów.dajProdukt(NARZĘDZIA,
                liczba_narzedzi);
        Oferta oferta = new OfertaRobotnika(KUPNA, robotnik, produkt);
        giełda.dodajOferte(oferta);
    }
}
