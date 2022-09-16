package com.company.strategia.kupowania;

import com.company.agent.Robotnik;
import com.company.giełda.Giełda;

import static com.company.produkt.Produkt.Typ.PROGRAMY_KOMPUTEROWE;

public class Gadżeciarz extends kupujacyNarzedzia {

    public Gadżeciarz(int liczba_narzedzi) {
        super(liczba_narzedzi);
    }

    @Override
    protected void wystawOfertęKupnaBezJedzenia(Robotnik robotnik, Giełda giełda) {
        kupNarzedzia(robotnik, giełda);
        Czyścioszek.zadbajOUbrania(robotnik, giełda);
        kupProgramy(robotnik, giełda);
    }

    private void kupProgramy(Robotnik robotnik, Giełda giełda) {
        int ileWyprodukowałem = robotnik.ileWyprodukowałemSztukProduktowZPoziomamiJakosci();
        kupTyleZebyWystarczylo(PROGRAMY_KOMPUTEROWE, ileWyprodukowałem,
                robotnik, giełda);
    }
}
