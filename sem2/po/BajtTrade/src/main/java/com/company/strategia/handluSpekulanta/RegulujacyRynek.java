package com.company.strategia.handluSpekulanta;

import com.company.agent.Spekulant;
import com.company.giełda.Giełda;
import com.company.produkt.Produkt;

import static java.lang.Math.max;

public class RegulujacyRynek extends StrategiaHandluSpekulanta {
    @Override
    public void wejdźZOfertami(Giełda giełda, Produkt.Typ typ, Spekulant spekulant) {
        if (giełda.czyPierwszyDzien())
            return;

        double sredniaCenaPrzed = giełda.dajSredniaCenePrzezOstatnieDni(typ, 1);
        int numerTury = giełda.dajAktualnyDzień();
        double p_i = giełda.dajLiczbeProduktowRobotnikowWystawionaDoSprzedazy(typ, numerTury);
        double p_iminus1 = giełda.dajLiczbeProduktowRobotnikowWystawionaDoSprzedazy(typ, numerTury);
        double dzielnik = max(p_iminus1, 1);

        double sredniaCena = sredniaCenaPrzed * p_i / dzielnik;

        spekulant.dodajWszystkieOfertySprzedazy(giełda, typ, sredniaCena);
        spekulant.dodajOfertęKupna(giełda, typ, sredniaCena);
    }
}
