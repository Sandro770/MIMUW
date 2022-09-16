package com.company.produkt;

import java.util.Optional;

import static com.company.produkt.Produkt.czyMaPoziomy;

public abstract class FabrykaProduktów {
    public static Produkt dajProdukt(Produkt.Typ typ, double ilosc,
                                     Optional<Integer> poziomJakości) {
        if (!czyMaPoziomy(typ))
            return dajProdukt(typ, ilosc);

        return switch (typ) {
            case UBRANIA -> new Ubrania(typ, (int) ilosc, poziomJakości);

            case NARZĘDZIA -> new Narzędzia(typ, (int) ilosc, poziomJakości);

            case PROGRAMY_KOMPUTEROWE -> new ProgramyKomputerowe(typ, (int) ilosc,
                    poziomJakości);

            default -> throw new IllegalStateException("Unexpected " + "value" +
                    ": " + typ);

        };
    }

    public static Produkt dajProdukt(Produkt.Typ typ, double ilosc) {
        if (czyMaPoziomy(typ))
            return dajProdukt(typ, ilosc, Optional.of(1));

        return switch (typ) {
            case JEDZENIE -> new Jedzenie(typ, (int) ilosc);

            case DIAMENT -> new Diamenty(typ, ilosc);

            default -> throw new IllegalStateException("Unexpected value: " + typ);
        };
    }
}
