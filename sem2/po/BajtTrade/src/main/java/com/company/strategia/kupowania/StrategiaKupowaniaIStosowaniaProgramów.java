package com.company.strategia.kupowania;

import com.company.agent.Robotnik;
import com.company.giełda.Giełda;
import com.company.oferta.Oferta;
import com.company.oferta.OfertaRobotnika;
import com.company.produkt.FabrykaProduktów;
import com.company.produkt.Produkt;
import com.company.strategia.Strategia;

import java.util.Optional;

import static com.company.oferta.Oferta.Typ.KUPNA;
import static com.company.produkt.Produkt.Typ.JEDZENIE;

public abstract class StrategiaKupowaniaIStosowaniaProgramów extends Strategia {
    public void wystawOfertęKupna(Robotnik robotnik, Giełda giełda) {
        wystawJedzenie(giełda, robotnik);

        this.wystawOfertęKupnaBezJedzenia(robotnik, giełda);
    }

    protected abstract void wystawOfertęKupnaBezJedzenia(Robotnik robotnik, Giełda giełda);

    private void wystawJedzenie(Giełda giełda, Robotnik robotnik) {
        Produkt produkt = FabrykaProduktów.dajProdukt(JEDZENIE, 100);

        Oferta oferta = new OfertaRobotnika(KUPNA, robotnik, produkt);

        giełda.dodajOferte(oferta);
    }

    protected static void kupTyleZebyWystarczylo(Produkt.Typ typProduktu,
                                                 int ileMaByc,
                                                 Robotnik robotnik, Giełda giełda) {
        int ileMa = robotnik.ileMaŁącznieSztuk(typProduktu);

        int ileTrzebaKupic = ileMa - ileMaByc;

        if (ileTrzebaKupic > 0) {
            Produkt produkt = FabrykaProduktów.dajProdukt(typProduktu,
                    ileTrzebaKupic, Optional.empty());

            Oferta oferta = new OfertaRobotnika(KUPNA, robotnik, produkt);
            
            giełda.dodajOferte(oferta);
        }
    }

}
