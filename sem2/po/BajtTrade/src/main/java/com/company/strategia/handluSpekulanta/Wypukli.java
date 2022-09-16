package com.company.strategia.handluSpekulanta;

import com.company.agent.Spekulant;
import com.company.giełda.Giełda;
import com.company.produkt.Produkt;

public class Wypukli extends StrategiaHandluSpekulanta {
    @Override
    public void wejdźZOfertami(Giełda giełda, Produkt.Typ typ, Spekulant spekulant) {
        double sredniaCena = giełda.
                dajMaksymalnaSredniaCeneWPrzeciaguOstatnichDni(typ, 1);

        if (giełda.czyFunkcjaZeSrednichCenScisleWypuklaPrzezOstatnie3Dni(giełda, typ))
            spekulant.dodajOfertęKupna(giełda, typ, sredniaCena);

        if (giełda.czyFunkcjaZeSrednichCenScisleWkleeslaPrzezOstatnie3Dni(giełda, typ))
            spekulant.dodajWszystkieOfertySprzedazy(giełda, typ, sredniaCena);
    }
}
