package com.atoudeft.banque;

public class OperationTransfer extends Operation{

    private double montant;
    private String compteDestinataire;

    public OperationTransfer(double montant, String compteDestinataire) {
        this.compteDestinataire = compteDestinataire;
        this.montant = montant;
    }


    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public String getCompteDestinataire() {
        return compteDestinataire;
    }
    public void setCompteDestinataire(String compteDestinataire) {
        this.compteDestinataire = compteDestinataire;
    }


    @Override
    public String toString(){
        return String.format("%s %s %.2f -> %s", super.getDate(), super.getType(), montant, compteDestinataire);    }

}
