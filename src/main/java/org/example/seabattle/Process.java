package org.example.seabattle;
/*
program posiada opcję gry na 2 graczy - dlatego nie będzie 2 osobnych funkcji na ruch gracza i ruch komputera, tylko ruch gracza oznaczony jako AI będzie uzupełniony o ruch AI.


do zrobienia - jak zrobić aby bez klasy button spowodować aby klik uruchamiał akcję wystrzału / trafienia.
*/


import java.util.ArrayList;
import java.util.List;

public class Process {
    // typ na razie nie wiadomo jaki
    // 0 = placement; 1 = tura gracz1; 2 = tura gracz2
    private int gamestate = 0;
    private List<Sector> p1area = new ArrayList<>();
    private List<Sector> p2area = new ArrayList<>();

    // konstruktor?

    //funkcja lokowania jednostek
    void placeUnits(Ship ship) {}

    //przeciągnij i upuść jednostkę
    void pickUnit(Ship ship) {}

    //funkcja salwy
    int shoot(Sector sector, int gamestate) {}

    //funkcja wykrywania trafień
    boolean isDamaged(Sector sector, Ship ship, int shoot_return?) {}

    //funkcja zatopienia jednostki
    boolean isDestroyed(Sector sector, Ship ship) {}

    //funkcja ustalenia wygranego
    void getWinner(int gamestate) {}
}
