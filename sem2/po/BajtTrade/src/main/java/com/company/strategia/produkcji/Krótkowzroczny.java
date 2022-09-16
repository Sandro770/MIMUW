package com.company.strategia.produkcji;

import com.company.agent.Robotnik;
import com.company.giełda.Giełda;
import com.company.produkt.Produkt;

public class Krótkowzroczny extends StrategiaProdukcji {
    @Override
    public double jakaWagaDlaDanegoProduktu(Produkt.Typ typProduktu, Giełda giełda, Robotnik robotnik) {
        return giełda.dajSredniaCene(typProduktu);
    }
}
