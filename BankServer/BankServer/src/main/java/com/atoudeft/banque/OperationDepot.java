package com.atoudeft.banque;

public class OperationDepot extends Operation {

    private double montant;

    public OperationDepot(double montant) {
        this.montant = montant;
        this.montant = montant;
    }


    public double getMontant() {
        return montant;
    }

    @Override
    public String toString(){
        return String.format("%s %s %.2f", super.getDate(), super.getType(), montant);    }



}
