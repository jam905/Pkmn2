package ru.mirea.pkmn.kazbekovajm.web;

import ru.mirea.pkmn.Card;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class CardExport {
    public void saveCard(Card card) {
        String filename = card.getName() + ".crd";
        try (FileOutputStream fileOut = new FileOutputStream(filename);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(card);
            System.out.println("Сериализованная карта сохранена под именем " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
