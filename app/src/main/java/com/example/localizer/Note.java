package com.example.localizer;

public class Note {
    private String titre;
    private String contenu;
    private int image;
    private double coordN;
    private double coordO;

    public Note(String titre, String contenu)
    {
        this.titre =titre;
        this.contenu = contenu;
        this.image = R.drawable.giorno;
        this.coordN = 0;
        this.coordO = 0;
    }

    public Note(String titre, String contenu, int image, double coordN, double coordO)
    {
        this.titre =titre;
        this.contenu = contenu;
        this.image = image;
        this.coordN = coordN;
        this.coordO = coordO;
    }

    public int getImage(){
        return image;
    }

    public String getTitre(){
        return titre;
    }

    public String getContenu(){
        return contenu;
    }

    public double getCoordN(){
        return coordN;
    }

    public double getCoordO(){
        return coordO;
    }
}
