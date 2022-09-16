package com.company.strategia.kupowania;

import com.company.agent.Robotnik;
import com.company.giełda.Giełda;
import com.company.oferta.Oferta;
import com.company.oferta.OfertaRobotnika;
import com.company.produkt.FabrykaProduktów;
import com.company.produkt.Produkt;

import static com.company.oferta.Oferta.Typ.KUPNA;
import static com.company.produkt.Produkt.Typ.NARZĘDZIA;

public class Zmechanizowany extends kupujacyNarzedzia {
    public Zmechanizowany(int liczba_narzedzi) {
        super(liczba_narzedzi);
    }

    @Override
    protected void wystawOfertęKupnaBezJedzenia(Robotnik robotnik, Giełda giełda) {
        Czyścioszek.zadbajOUbrania(robotnik, giełda);
        kupNarzedzia(robotnik, giełda);
    }
}
