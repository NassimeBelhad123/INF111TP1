package com.atoudeft.banque;

public class OperationFacture extends Operation{


    private double montant;
    private String numeroFacture;
    private String description;


    public OperationFacture(double montant, String description, String numeroFacture) {
        this.description = description;
        this.numeroFacture = numeroFacture;
        this.montant = montant;
    }


    public double getMontant() {
        return montant;
    }

    public String getNumeroFacture() {
        return numeroFacture;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString(){
        return String.format("%s %s %.2f Facture: %s (%s)", super.getDate(), super.getType(), montant, numeroFacture, description);    }

}
