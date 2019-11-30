package com.example.localizer;

public class Note {
    private String titre;
    private String contenu;
    private int image;
    private double coordN;  //La latitude
    private double coordO;  //La longitude

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
