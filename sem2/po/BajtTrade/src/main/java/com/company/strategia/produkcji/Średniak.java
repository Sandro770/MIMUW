package com.company.strategia.produkcji;

import com.company.agent.Robotnik;
import com.company.giełda.Giełda;
import com.company.produkt.Produkt;

public class Średniak extends StrategiaProdukcji {
    private final int historia_sredniej_produkcji;

    public Średniak(int historia_sredniej_produkcji) {
        this.historia_sredniej_produkcji = historia_sredniej_produkcji;
    }

    @Override
    public double jakaWagaDlaDanegoProduktu(Produkt.Typ typProduktu, Giełda giełda, Robotnik robotnik) {
        return giełda.dajMaksymalnaSredniaCeneWPrzeciaguOstatnichDni(typProduktu,
                historia_sredniej_produkcji);
    }

}
